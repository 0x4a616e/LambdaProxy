package de.jangassen.lambda;

import de.jangassen.lambda.api.SamApiDescription;
import de.jangassen.lambda.loader.DefaultLambdaMethodInvoker;
import de.jangassen.lambda.loader.LambdaClassLoaderFactory;
import de.jangassen.lambda.loader.MethodInvocationContextCache;
import de.jangassen.lambda.loader.SamArtifactResolver;
import de.jangassen.lambda.watcher.DeploymentChangeWatcher;
import de.jangassen.lambda.yaml.SamTemplate;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Executors;

public class LambdaProxy {
    public static final int PORT = 3000;
    public static final Duration INSTANCE_TIMEOUT = Duration.ofMinutes(5);

    public static final String LAMBDA_PROXY = "lambdaProxy";
    public static final String TEMPLATE_YAML = "template.yaml";
    public static final String AWS_SAM = ".aws-sam";

    private final Context context;
    private final Tomcat tomcat;
    private final TemplateParser templateParser;
    private final Path projectPath;

    private Wrapper wrapper;

    public LambdaProxy(Path projectPath) {
        this.projectPath = projectPath;
        tomcat = createTomcat();
        context = createContext(tomcat);

        templateParser = new TemplateParser();
    }

    public void deploy(Path rootPath) throws IOException {
        if (wrapper != null) {
            context.removeChild(wrapper);
        }

        Path templateFile = getTemplateFile(rootPath);

        wrapper = Tomcat.addServlet(context, LAMBDA_PROXY, getLambdaServlet(rootPath, templateFile));
        context.addServletMappingDecoded("/*", LAMBDA_PROXY);
    }

    private LambdaProxyServlet getLambdaServlet(Path rootPath, Path templateFile) throws FileNotFoundException {
        SamTemplate samTemplate = templateParser.parse(templateFile);

        SamApiDescription apiDescription = new SamApiDescription(samTemplate, projectPath);
        LambdaClassLoaderFactory classLoaderFactory = new LambdaClassLoaderFactory(new SamArtifactResolver(rootPath));
        DefaultLambdaMethodInvoker lambdaMethodInvoker = new DefaultLambdaMethodInvoker();
        MethodInvocationContextCache methodInvocationContextCache = new MethodInvocationContextCache(classLoaderFactory, INSTANCE_TIMEOUT);

        return new LambdaProxyServlet(lambdaMethodInvoker, methodInvocationContextCache, getCorsSettings(samTemplate), apiDescription.getApiMethods());
    }

    private SamTemplate.Cors getCorsSettings(SamTemplate samTemplate) {
        SamTemplate.Globals globals = samTemplate.Globals;
        if (globals == null) {
            return null;
        }
        SamTemplate.Api api = globals.Api;
        if (api == null) {
            return null;
        }
        return api.Cors;
    }

    public void serve() {
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private static Tomcat createTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        return tomcat;
    }

    private static Context createContext(Tomcat tomcat) {
        return tomcat.addContext("", new File(".").getAbsolutePath());
    }

    public static void main(String[] args) throws IOException {
        String projectPathString = (args.length == 0 || StringUtils.isBlank(args[0]))
                ? System.getProperty("user.dir")
                : args[0];

        Path projectPath = new File(projectPathString).toPath();
        Path samBuildPath = projectPath.resolve(AWS_SAM).resolve("build");

        LambdaProxy lambdaProxy = new LambdaProxy(projectPath);
        startChangeWatcher(samBuildPath, lambdaProxy);
        lambdaProxy.deploy(samBuildPath);
        lambdaProxy.serve();
    }

    private static void startChangeWatcher(Path samBuildPath, LambdaProxy lambdaProxy) {
        DeploymentChangeWatcher deploymentChangeWatcher = new DeploymentChangeWatcher(samBuildPath, () -> {
            try {
                lambdaProxy.deploy(samBuildPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Executors.newSingleThreadExecutor().submit(deploymentChangeWatcher);
    }

    private static Path getTemplateFile(Path rootPath) {
        return rootPath.resolve(TEMPLATE_YAML);
    }
}
