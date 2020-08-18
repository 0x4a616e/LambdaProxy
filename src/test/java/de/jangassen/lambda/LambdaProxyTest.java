package de.jangassen.lambda;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LambdaProxyTest {

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse res;

    @Test
    void createInstance() {
        when(req.getPathInfo()).thenReturn("/path/123");
        when(req.getMethod()).thenReturn(HttpMethod.GET);

        Path projectPath = new File(System.getProperty("user.dir")).toPath();
        LambdaProxy lambdaProxy = new LambdaProxy(projectPath);

        SamTemplate samTemplate = new SamTemplate();
        samTemplate.Resources = Collections.emptyMap();

        LambdaProxyServlet lambdaServlet = lambdaProxy.getLambdaServlet(projectPath, samTemplate);
        lambdaServlet.service(req, res);

        verify(res, times(1)).setStatus(HttpStatus.SC_NOT_FOUND);
    }
}