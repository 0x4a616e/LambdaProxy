![Maven Build](https://github.com/0x4a616e/LambdaProxy/workflows/Maven%20Build/badge.svg?branch=master)

# LambdaProxy

LambdaProxy is a tool to for starting a local API that redirects to Lambda functions as specified in a SAM template file.

It basically launches a servlet in an embedded Tomcat listening for incoming requests. Whenever a new request is executed,
the servlet looks into the SAM template file to lookup the respective Lambda function. If a matching Lambda API event
is found, LambdaProxy loads the Lambda function in a dedicated isolated ClassLoader and calls the handler function with all request
parameters. This ClassLoader is cached for subsequent invocations, resulting in a similar behaviour as in AWS where the
execution context of a Lambda function is also kept alive for a certain time period.

## There already is `sam local start-api`, why should I use this?

`sam local start-api` is great, but it has a few shortcomings: First of all, it is comparably slow. Whenever a new API resource
is called `sam local start-api` spins up a new Docker container to invoke the requested function. As a result, invoking
a single API method can require about a second, even on properly equipped developer machines. If you're using the
local API e.g. to test a frontend application that performs a larger amount of API calls, this can become a bit annoying.

The second shortcoming is step-by-step debugging. You can enable step-by-step debugging using the `-d` flag, but this will
cause `sam` to wait on every single API invocation until a debugger is attached. So again if you're working on a frontend
application that performs various API requests, this can quickly become tedious. Unlike debugging a regular Java
application, you cannot just let your application run and just put a breakpoint in on demand during testing.

## Usage

Build your lambda application using `sam`:

    sam build
    
 This should compile everything into the `.aws-sam` folder.
 
 Build `LambdaProxy` using maven:
 
     mvn clean package
     
 Run your lambda application:
 
     java -jar ./target/LambdaProxy-1.0-SNAPSHOT.jar ~/Workspace/my-lambda-function
     
 If you want to debug your lambda function, add the remote debugger agent:
 
    java -agentlib:jdwp=transport=dt_socket,server=y,address=5858,suspend=n -jar ./target/LambdaProxy-1.0-SNAPSHOT.jar ~/Workspace/my-lambda-function

When you rebuild you lambda function, `LambdaProxy` will pick up the changes automatically and reload the API.
