package httpserver.anton.api;

import java.util.Map;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-13
 */
public interface HttpRequest {
    String getUrl();

    String getLocation();

    String getParameter(String key);

    byte[] getBody();

    String getHeader(String name);

    Map<String, String> getHeaders();

    Map<String, String> getParametersUrl();

    String getPathInfo();

    String getCookie(String key);
}
