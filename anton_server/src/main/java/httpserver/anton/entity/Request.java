package httpserver.anton.entity;

import httpserver.anton.api.HttpRequest;
import httpserver.anton.api.controller.RequestMethods;
import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-12
 */
@Data
public class Request implements HttpRequest {
    private final Map<String, String> parametersUrl = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookie = new HashMap<>();
    private final BufferedReader readerStream;
    private final BufferedWriter writerStream;
    private final Socket connection;
    private RequestMethods method;
    private String pathInfo;
    private String location;
    private byte[] body;
    private String url;

    public Request(Socket connection) throws IOException {
        this.connection = connection;
        this.readerStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        this.writerStream = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
    }

    public boolean isCorrect() {
        return url != null && url.matches("/[A-z0-9$#=,+*@\\-!'%&~:_/\\.\\[\\]\\(\\)]*") && method != null;
    }

    public boolean isKeepAlive() {
        String value = headers.get("Connection");
        return value != null && value.equalsIgnoreCase("keep-alive");
    }

    public boolean isCloseConnection() {
        String value = headers.get("Connection");
        return value != null && value.equalsIgnoreCase("close");
    }

    @Override
    public String getParameter(String key) {
        return parametersUrl.get(key);
    }

    public void addParameter(String key, String value) {
        parametersUrl.put(key, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public String getCookie(String key) {
        return cookie.get(key);
    }

    public void addCookie(String key, String value) {
        cookie.put(key, value);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setUrl(String url) {
        this.url = url;
        this.location = "http:/" + connection.getLocalSocketAddress() + url;
    }
}
