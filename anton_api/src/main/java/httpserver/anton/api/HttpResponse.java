package httpserver.anton.api;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-13
 */
public interface HttpResponse {
    byte[] getBody();

    void write(String response);

    void write(byte[] response);

    void addHeader(String key, String value);

    void setStatus(int status);
}
