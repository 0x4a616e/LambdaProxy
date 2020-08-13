package de.jangassen.lambda;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.nio.file.Path;

public class OpenApiParser {
    public OpenApiParser() {
    }

    public OpenAPI parse(Path templateFile) {
        return new OpenAPIV3Parser().read(templateFile.toAbsolutePath().toString());
    }
}
