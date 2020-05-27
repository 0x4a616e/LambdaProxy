package de.jangassen.lambda;

import de.jangassen.lambda.api.SamApiDescription;
import de.jangassen.lambda.loader.CachedLambdaMethodInvoker;
import de.jangassen.lambda.loader.LambdaClassLoaderFactory;
import de.jangassen.lambda.loader.SamArtifactResolver;
import de.jangassen.lambda.watcher.DeploymentChangeWatcher;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.Executors;

public class LambdaProxy {
    public static final int PORT = 3000;

    public static final String LAMBDA_PROXY = "lambdaProxy";
    public static final String TEMPLATE_YAML = "template.yaml";
    public static final String AWS_SAM = ".aws-sam";

    private final Context context;
    private final Tomcat tomcat;
    private final TemplateParser templateParser;

    private Wrapper wrapper;

    public LambdaProxy() {
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
        return new LambdaProxyServlet(
                new CachedLambdaMethodInvoker(new LambdaClassLoaderFactory(new SamArtifactResolver(rootPath))),
                new SamApiDescription(templateParser.parse(templateFile)));
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
        if (args.length == 0 || StringUtils.isBlank(args[0])) {
            System.err.println("Usage: " + LambdaProxy.class.getSimpleName() + " <project path>");
            return;
        }

        Path samBuildPath = new File(args[0]).toPath().resolve(AWS_SAM).resolve("build");

        LambdaProxy lambdaProxy = new LambdaProxy();
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
