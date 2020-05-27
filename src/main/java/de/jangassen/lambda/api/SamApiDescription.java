package de.jangassen.lambda.api;

import de.jangassen.lambda.util.EventUtils;
import de.jangassen.lambda.yaml.SamTemplate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SamApiDescription implements ApiDescription {

    private final PathPatternParser pathPatternParser = new PathPatternParser();
    private final SamTemplate samTemplate;

    public SamApiDescription(SamTemplate samTemplate) {
        this.samTemplate = samTemplate;
    }

    @Override
    public Map<String, List<String>> listAPIs() {
        return samTemplate.Resources.values().stream()
                .flatMap(this::getAllEvents)
                .map(e -> e.Properties)
                .collect(Collectors.groupingBy(p -> p.Path, Collectors.mapping(v -> v.Method, Collectors.toList())));
    }

    @Override
    public Optional<RequestEvent> getRequestEvent(String path, String method) {
        PathContainer pathContainer = PathContainer.parsePath(path);

        return samTemplate.Resources.entrySet().stream().map(r -> getRequestEvent(method, pathContainer, r.getKey(), r.getValue()))
                .filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    private Optional<RequestEvent> getRequestEvent(String method, PathContainer pathContainer, String resourceName, SamTemplate.Resource resource) {
        return getAllEvents(resource)
                .filter(event -> StringUtils.equalsIgnoreCase(method, event.Properties.Method))
                .map(event -> createRequestEvent(pathContainer, event, resourceName, resource))
                .filter(e -> e.getPathMatchInfo() != null)
                .findFirst();
    }

    private Stream<SamTemplate.Event> getAllEvents(SamTemplate.Resource resource) {
        if (ObjectUtils.isEmpty(resource.Properties.Events)) {
            if (!ObjectUtils.isEmpty(resource.Properties.DefinitionBody)) {
                Object transform = resource.Properties.DefinitionBody.get("Fn::Transform");
                if (transform instanceof Map) {
                    String name = (String) ((Map<?, ?>) transform).get("Name");
                    Object parameters = ((Map<?, ?>) transform).get("Parameters");
                    if ("AWS::Include".equals(name) && parameters instanceof Map) {
                        String value = (String) ((Map<?, ?>) parameters).get("Location");

                        System.err.println(String.format("Including templates from '%s' is not supported.", value));
                    }
                }
            }

            return Stream.empty();
        }
        return resource.Properties.Events.values().stream()
                .filter(EventUtils::isAPIEvent);
    }

    private RequestEvent createRequestEvent(PathContainer pathContainer, SamTemplate.Event event, String resouceName, SamTemplate.Resource resource) {
        String eventPath = event.Properties.Path;
        String handler = resource.Properties.Handler;

        PathPattern eventPattern = pathPatternParser.parse(eventPath);
        PathPattern.PathMatchInfo pathMatchInfo = eventPattern.matchAndExtract(pathContainer);
        return new RequestEvent(resouceName, resource.Properties.CodeUri, handler, eventPath, pathMatchInfo);
    }
}
