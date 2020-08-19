package de.jangassen.lambda.api;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import de.jangassen.lambda.util.ResourceUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import javax.ws.rs.HttpMethod;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenApiDescription {
    public static final String X_AMAZON_APIGATEWAY_INTEGRATION = "x-amazon-apigateway-integration";
    public static final String URI = "uri";
    public static final String FN_SUB = "Fn::Sub";
    private final Pattern lambdaNamePattern = Pattern.compile("^.*/\\$\\{([^/]*)\\.Arn}/.*$");
    private final OpenAPI openAPI;
    private final SamTemplate samTemplate;

    public OpenApiDescription(OpenAPI openAPI, SamTemplate samTemplate) {
        this.openAPI = openAPI;
        this.samTemplate = samTemplate;
    }

    public List<ApiMethod> getApiMethods() {
        return openAPI.getPaths()
                .entrySet()
                .stream()
                .flatMap(this::createApiMethod)
                .collect(Collectors.toList());
    }

    private Stream<ApiMethod> createApiMethod(Map.Entry<String, PathItem> pathAndProperties) {
        String pathPattern = pathAndProperties.getKey();
        PathItem pathItem = pathAndProperties.getValue();

        return getOperations(pathItem)
                .filter(e -> e.getValue() != null)
                .map(e -> getApiMethod(pathPattern, e))
                .filter(Optional::isPresent).map(Optional::get);
    }

    private Stream<AbstractMap.SimpleImmutableEntry<String, Operation>> getOperations(PathItem pathItem) {
        return Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.PUT, pathItem.getPut()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.POST, pathItem.getPost()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.PATCH, pathItem.getPatch()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.GET, pathItem.getGet()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.HEAD, pathItem.getHead()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.DELETE, pathItem.getDelete()),
                new AbstractMap.SimpleImmutableEntry<>(HttpMethod.OPTIONS, pathItem.getOptions())
        );
    }

    private Optional<ApiMethod> getApiMethod(String pathPattern, AbstractMap.SimpleImmutableEntry<String, Operation> e) {
        String uri = getLambdaUri(e.getValue());
        if (uri == null) {
            return Optional.empty();
        }

        Matcher matcher = lambdaNamePattern.matcher(uri);
        if (matcher.matches()) {
            String resourceName = matcher.group(1);
            SamTemplate.Resource resource = samTemplate.getResources().get(resourceName);
            if (resource != null && ResourceUtils.isJava8Runtime(resource)) {
                return Optional.of(new ApiMethod(resourceName, resource.getProperties().getCodeUri(), resource.getProperties().getHandler(), pathPattern, e.getKey()));
            }
        }

        return Optional.empty();
    }

    private String getLambdaUri(Operation post) {
        Object xApiGatewayIntegration = post.getExtensions().get(X_AMAZON_APIGATEWAY_INTEGRATION);
        if (!(xApiGatewayIntegration instanceof Map)) {
            return null;
        }
        Object uri = ((Map<?, ?>) xApiGatewayIntegration).get(URI);
        if (!(uri instanceof Map)) {
            return null;
        }

        Object fnSub = ((Map<?, ?>) uri).get(FN_SUB);
        if (!(fnSub instanceof String)) {
            return null;
        }
        return (String) fnSub;
    }
}
