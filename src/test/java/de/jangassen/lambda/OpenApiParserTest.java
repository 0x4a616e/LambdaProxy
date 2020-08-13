package de.jangassen.lambda;

import de.jangassen.lambda.api.ApiMethod;
import de.jangassen.lambda.api.OpenApiDescription;
import de.jangassen.lambda.yaml.SamTemplate;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenApiParserTest {
    @Test
    void testParseOpenApiFile() throws URISyntaxException, FileNotFoundException {
        URL openApiResource = ClassLoader.getSystemClassLoader().getResource("openapi.yaml");
        URL templateResource = ClassLoader.getSystemClassLoader().getResource("template.yaml");

        SamTemplate samTemplate = new TemplateParser().parse(Paths.get(URI.create(Objects.requireNonNull(templateResource).toString())));
        OpenAPI openAPI = new OpenApiParser().parse(new File(Objects.requireNonNull(openApiResource).toURI()).toPath());

        List<ApiMethod> apiMethods = new OpenApiDescription(openAPI, samTemplate).getApiMethods();

        Assertions.assertEquals(1, apiMethods.size());
        Assertions.assertEquals("LambdaFunctionOverHttps", apiMethods.get(0).getResourceName());
        assertEquals("/test/{id}", apiMethods.get(0).getPathPattern());
        assertEquals("index.handler::handleRequest", apiMethods.get(0).getHandler());
        assertEquals("POST", apiMethods.get(0).getMethod());
    }
}
