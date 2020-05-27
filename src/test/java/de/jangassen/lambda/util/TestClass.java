package de.jangassen.lambda.util;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.io.Serializable;

public class TestClass implements Serializable {

    public static final String SUCCESS = "SUCCESS";

    public Object handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        return SUCCESS;
    }
}
