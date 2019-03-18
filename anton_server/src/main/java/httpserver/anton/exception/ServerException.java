package httpserver.anton.exception;

import lombok.Getter;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-27
 */
public class ServerException extends RuntimeException {
    @Getter
    private final int statusCode;

    public ServerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServerException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
