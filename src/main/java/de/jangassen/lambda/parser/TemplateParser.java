package de.jangassen.lambda.parser;

import de.jangassen.lambda.parser.intrinsics.*;
import de.jangassen.lambda.parser.yaml.IntrinsicsYamlConstructor;
import de.jangassen.lambda.parser.yaml.ResolvingIntrinsicsYamlConstructor;
import de.jangassen.lambda.parser.yaml.SamTemplate;
import de.jangassen.lambda.util.ParameterUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateParser {
    private final Yaml preParser;
    private final Representer representer;

    public TemplateParser() {
        representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        preParser = new Yaml(new IntrinsicsYamlConstructor(Map.class), representer);
    }

    public SamTemplate parse(Path templateFile) throws FileNotFoundException {
        Map<String, Object> template = preParser.load(new FileInputStream(templateFile.toFile()));
        Map<String, String> parameters = getParameters(template);

        Map<String, Intrinsic> intrinsics = Stream.of(
                new Ref(parameters),
                new Sub(parameters),
                new Join(),
                new Base64()
        ).collect(Collectors.toMap(Intrinsic::name, i -> i));

        return new Yaml(new ResolvingIntrinsicsYamlConstructor(SamTemplate.class, intrinsics), representer)
                .loadAs(new FileInputStream(templateFile.toFile()), SamTemplate.class);
    }

    private Map<String, String> getParameters(Map<String, Object> preparsedObject) {
        Object parameters = preparsedObject.get("Parameters");
        if (parameters instanceof Map) {
            return ParameterUtils.getParameters((Map<?, ?>) parameters);
        }

        return Collections.emptyMap();
    }
}
