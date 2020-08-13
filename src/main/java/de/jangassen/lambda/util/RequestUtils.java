package de.jangassen.lambda.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static String getRequestEntity(HttpServletRequest servletRequest) throws IOException {
        return servletRequest.getReader().lines().collect(Collectors.joining());
    }

    public static Map<String, String> getHeaders(HttpServletRequest servletRequest) {
        Map<String, String> header = new HashMap<>();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            header.put(headerName, servletRequest.getHeader(headerName));
        }
        return header;
    }

    public static Map<String, String> getQueryParameters(HttpServletRequest servletRequest) {
        if (StringUtils.isBlank(servletRequest.getQueryString())) {
            return Collections.emptyMap();
        }

        return Arrays.stream(StringUtils.split(servletRequest.getQueryString(), "&"))
                .map(v -> StringUtils.split(v, "=", 2))
                .collect(Collectors.toMap(v -> getDecodedParameterComponent(v, 0), v -> getDecodedParameterComponent(v, 1)));
    }

    private static String getDecodedParameterComponent(String[] s, int index) {
        return Arrays.stream(s).skip(index).map(RequestUtils::urlDecode).findFirst().orElse("");
    }

    private static String urlDecode(String v) {
        try {
            return URLDecoder.decode(v, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
