package de.jangassen.lambda;

import de.jangassen.lambda.api.ApiDescription;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.api.SamApiDescription;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateParserTest {

    @Test
    public void testParseSimpleTemplate() throws FileNotFoundException {
        URL resource = ClassLoader.getSystemClassLoader().getResource("template.yaml");

        ApiDescription lambdaEventResolver = new SamApiDescription(
                new TemplateParser().parse(Paths.get(URI.create(resource.toString()))));

        Optional<RequestEvent> event = lambdaEventResolver.getRequestEvent("/DynamoDBOperations/DynamoDBManager", "post");

        assertTrue(event.isPresent());
        assertEquals("LambdaFunctionOverHttps", event.get().getResourceName());
        assertEquals("/DynamoDBOperations/DynamoDBManager", event.get().getPath());
        assertEquals("index.handler", event.get().getHandlerClass());
        assertEquals("handleRequest", event.get().getHandlerMethod());
    }
}