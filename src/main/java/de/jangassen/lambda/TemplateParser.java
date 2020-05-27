package de.jangassen.lambda;

import de.jangassen.lambda.yaml.IntrinsicsYamlConstructor;
import de.jangassen.lambda.yaml.ResolvingIntrinsicsYamlConstructor;
import de.jangassen.lambda.yaml.SamTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;

public class TemplateParser {
    private final Yaml preParser;
    private final Representer representer;

    TemplateParser() {
        representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        preParser = new Yaml(new IntrinsicsYamlConstructor(Map.class), representer);
    }

    public SamTemplate parse(Path templateFile) throws FileNotFoundException {
        Map<String, Object> template = preParser.load(new FileInputStream(templateFile.toFile()));

        return new Yaml(new ResolvingIntrinsicsYamlConstructor(SamTemplate.class, template), representer)
                .loadAs(new FileInputStream(templateFile.toFile()), SamTemplate.class);
    }
}
