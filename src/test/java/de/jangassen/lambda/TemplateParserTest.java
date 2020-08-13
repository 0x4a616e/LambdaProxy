package de.jangassen.lambda;

import de.jangassen.lambda.api.ApiDescription;
import de.jangassen.lambda.api.ApiMethod;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.api.SamApiDescription;
import de.jangassen.lambda.util.ApiMethodUtils;
import de.jangassen.lambda.yaml.SamTemplate;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateParserTest {

    @Test
    public void testParseSimpleTemplate() throws FileNotFoundException {
        Path templateFile = getTestResourcePath();
        SamTemplate samTemplate = new TemplateParser().parse(templateFile);
        ApiDescription lambdaEventResolver = new SamApiDescription(
                samTemplate);

        List<ApiMethod> apiMethods = lambdaEventResolver.getApiMethods();
        Optional<RequestEvent> event = ApiMethodUtils.getRequestEvent(apiMethods, "/DynamoDBOperations/DynamoDBManager", "post");

        assertTrue(event.isPresent());
        assertEquals("LambdaFunctionOverHttps", event.get().getResourceName());
        assertEquals("/DynamoDBOperations/DynamoDBManager", event.get().getPath());
        assertEquals("/DynamoDBOperations/{id}", event.get().getResource());
        assertEquals("index.handler", event.get().getHandlerClass());
        assertEquals("handleRequest", event.get().getHandlerMethod());
    }

    @Test
    public void testParseTemplateWithOpenApi() throws FileNotFoundException {
        Path templateFile = getTestResourcePath();
        ApiDescription lambdaEventResolver = new SamApiDescription(
                new TemplateParser().parse(templateFile));

        List<ApiMethod> apiMethods = lambdaEventResolver.getApiMethods();
        Optional<RequestEvent> event = ApiMethodUtils.getRequestEvent(apiMethods, "/test/123", "post");

        assertTrue(event.isPresent());
        assertEquals("LambdaFunctionOverHttps", event.get().getResourceName());
        assertEquals("/test/123", event.get().getPath());
        assertEquals("/test/{id}", event.get().getResource());
        assertEquals("index.handler", event.get().getHandlerClass());
        assertEquals("handleRequest", event.get().getHandlerMethod());
    }

    private Path getTestResourcePath() {
        URL resource = ClassLoader.getSystemClassLoader().getResource("template.yaml");
        return Paths.get(URI.create(Objects.requireNonNull(resource).toString()));
    }
}