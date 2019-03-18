package httpserver.anton.service;

import httpserver.anton.api.controller.RequestMethods;
import httpserver.anton.entity.Request;
import httpserver.anton.exception.ServerException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-12
 */
@Log4j
public class RequestService {

    public void parse(Request request) throws IOException {
        clear(request);

        String statusLine = request.getReaderStream().readLine();

        if (statusLine != null) {
            parseStatusLine(request, statusLine);
            parseHeadersRequest(request, request.getReaderStream());
            parseCookie(request);
            parseBody(request, request.getReaderStream());
        } else {
            throw new SocketException("Client closed connection.");
        }
    }

    private void parseStatusLine(Request request, String head) {
        String[] headArray = StringUtils.split(head, " ");

        try {
            request.setMethod(RequestMethods.valueOf(headArray[0]));
        } catch (IllegalArgumentException e) {
            throw new ServerException("Http method like this: " + headArray[0] + " is not supported.", e, 405);
        }

        String[] requestArray = StringUtils.split(headArray[1], "?=&");
        request.setUrl(requestArray[0]);

        for (int i = 1; i < requestArray.length; i += 2) {
            request.addParameter(requestArray[i], requestArray[i + 1]);
        }
    }

    private void parseHeadersRequest(Request request, BufferedReader streamRequest) {
        try {
            Map<String, String> headers = request.getHeaders();
            String lineHeader;

            while (!StringUtils.equals(lineHeader = streamRequest.readLine(), "")) {
                String[] splitHeader = lineHeader.split(":\\s");
                headers.put(splitHeader[0].trim(), splitHeader[1].trim());
            }
        } catch (IOException e) {
            log.error("Error while parse headers.", e);
        }
    }

    private void parseBody(Request request, BufferedReader streamRequest) {
        try {
            StringBuilder body = new StringBuilder();
            while (streamRequest.ready()) {
                body.append((char) streamRequest.read());
            }

            request.setBody(body.toString().getBytes());

        } catch (IOException e) {
            log.error("Can't parse body of request.", e);
        }
    }

    private void parseCookie(Request request) {
        String cookie = request.getHeaders().get("Cookie");
        if (cookie != null) {
            String[] cookieArray = cookie.split("[=;\\s]+");

            for (int i = 0; i < cookieArray.length - 1; i += 2) {
                request.addCookie(cookieArray[i], cookieArray[i + 1]);
            }
        }
    }

    private void clear(Request request) {
        request.getParametersUrl().clear();
        request.getHeaders().clear();
        request.getCookie().clear();
        request.setLocation("");
        request.setPathInfo("");
        request.setMethod(null);
        request.setUrl("");
    }
}
