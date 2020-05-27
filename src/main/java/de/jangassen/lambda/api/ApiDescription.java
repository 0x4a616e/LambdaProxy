package de.jangassen.lambda.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ApiDescription {
    Optional<RequestEvent> getRequestEvent(String path, String method);

    Map<String, List<String>> listAPIs();
}
