package de.jangassen.lambda;

import de.jangassen.lambda.api.SamApiDescription;
import de.jangassen.lambda.loader.DefaultLambdaMethodInvoker;
import de.jangassen.lambda.loader.LambdaClassLoaderFactory;
import de.jangassen.lambda.loader.MethodInvocationContextCache;
import de.jangassen.lambda.loader.SamArtifactResolver;
import de.jangassen.lambda.parser.TemplateParser;
import de.jangassen.lambda.parser.yaml.SamTemplate;
import de.jangassen.lambda.watcher.DeploymentChangeWatcher;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Executors;

public class LambdaProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaProxy.class);

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
    private LambdaClassLoaderFactory classLoaderFactory;

    public LambdaProxy(Path projectPath) {
        this.projectPath = projectPath;
        tomcat = createTomcat();
        context = createContext(tomcat);

        templateParser = new TemplateParser();
    }

    public void deploy(Path rootPath) throws IOException {
        cleanupPreviousDeployment();

        classLoaderFactory = new LambdaClassLoaderFactory(new SamArtifactResolver(rootPath));
        LambdaProxyServlet lambdaServlet = getLambdaServlet(getSamTemplate(rootPath), classLoaderFactory, projectPath);
        wrapper = Tomcat.addServlet(context, LAMBDA_PROXY, lambdaServlet);
        context.addServletMappingDecoded("/*", LAMBDA_PROXY);
    }

    private SamTemplate getSamTemplate(Path rootPath) throws FileNotFoundException {
        Path templateFile = getTemplateFile(rootPath);
        return templateParser.parse(templateFile);
    }

    private void cleanupPreviousDeployment() {
        if (wrapper != null) {
            context.removeChild(wrapper);
        }
        if (classLoaderFactory != null) {
            classLoaderFactory.close();
            classLoaderFactory = null;
        }
    }

    static LambdaProxyServlet getLambdaServlet(SamTemplate samTemplate, LambdaClassLoaderFactory classLoaderFactory, Path projectPath) {
        SamApiDescription apiDescription = new SamApiDescription(samTemplate, projectPath);
        DefaultLambdaMethodInvoker lambdaMethodInvoker = new DefaultLambdaMethodInvoker();
        MethodInvocationContextCache methodInvocationContextCache = new MethodInvocationContextCache(classLoaderFactory, INSTANCE_TIMEOUT);

        return new LambdaProxyServlet(lambdaMethodInvoker, methodInvocationContextCache, getCorsSettings(samTemplate), apiDescription.getApiMethods());
    }

    private static SamTemplate.Cors getCorsSettings(SamTemplate samTemplate) {
        SamTemplate.Globals globals = samTemplate.getGlobals();
        if (globals == null) {
            return null;
        }
        SamTemplate.Api api = globals.getApi();
        if (api == null) {
            return null;
        }
        return api.getCors();
    }

    public void serve() {
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            LOGGER.error("Unable to start servlet", e);
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
        DeploymentChangeWatcher deploymentChangeWatcher = new DeploymentChangeWatcher(samBuildPath, path -> {
            try {
                lambdaProxy.deploy(samBuildPath);
            } catch (IOException e) {
                LOGGER.error("Error while watching directory '{}': {}", samBuildPath, e);
            }
        });
        Executors.newSingleThreadExecutor().submit(deploymentChangeWatcher);
    }

    private static Path getTemplateFile(Path rootPath) {
        return rootPath.resolve(TEMPLATE_YAML);
    }
}
