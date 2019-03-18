package httpserver.anton.tasks.http;

import httpserver.anton.HttpServer;
import httpserver.anton.api.Reaction;
import httpserver.anton.api.controller.RequestMethods;
import httpserver.anton.entity.Request;
import httpserver.anton.entity.Response;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-01-17
 */
@Log4j
public class RequestHttpTask extends HttpTask {
    protected final int emptyRequestTimeOut;
    protected final boolean serverKeepAlive;
    protected Socket connection;
    protected Request request;

    private RequestHttpTask(HttpServer httpServer) {
        super(httpServer);
        this.emptyRequestTimeOut = httpServer.getPropServer().getEmptyRequestTimeOut();
        this.serverKeepAlive = httpServer.getPropServer().isKeepAliveOn();
    }

    public RequestHttpTask(HttpServer httpServer, Socket connection) {
        this(httpServer);
        this.connection = connection;
    }

    public RequestHttpTask(HttpServer httpServer, Request request) {
        this(httpServer);
        this.request = request;
    }

    @Override
    public void doTask() {
        if (request == null) {
            try {
                request = new Request(connection);
                connection.setKeepAlive(true);
                connection.setSoTimeout(emptyRequestTimeOut);
                httpServer.getRequestService().parse(request);
            } catch (IOException e) {
                log.debug("Error when parsing request.", e);
            }
        }

        if (request.isCorrect()) {
            if (httpServer.getPropServer().isVisibleRequest()) {
                log.info(request.getMethod().name() + " " + request.getUrl());
            }

            Response response = new Response(request);
            try {

                executeReaction(request, response);

            } catch (RuntimeException e) {
                log.error("Client error", e);
                if (response.getStatus() == 0) {
                    response.setStatus(500);
                    request.addHeader("Connection", "close");
                }
            }

            try {
                httpServer.getResponseService().respond(request, response);
            } catch (IOException e) {
                log.error("Can't get response.", e);
                closeConnection(request);
            }

            if (request.isCloseConnection()) {
                closeConnection(request);
            } else {
                if (serverKeepAlive && request.isKeepAlive()) {
                    httpServer.getKeepAliveThreadPool().handle(new KeepAliveHttpTask(httpServer, request));
                }
            }
        } else {
            log.debug("Request is not correct: " + request);
        }
    }

    protected void executeReaction(Request request, Response response) {
        Reaction reaction = getReaction(request, request.getMethod());

        if (reaction != null) {
            reaction.react(request, response);
        }
    }

    private Reaction getReaction(Request request, RequestMethods method) {
        Lock readLock = httpServer.getLockPatternMap().readLock();
        try {
            readLock.lock();
            for (Map.Entry<String, Map<RequestMethods, Reaction>> entry : httpServer.getPatternMap().entrySet()) {

                if (StringUtils.startsWith(request.getUrl(), entry.getKey())) {

                    request.setPathInfo(StringUtils.removeStart(request.getUrl(), entry.getKey()));

                    return entry.getValue().get(method);
                }
            }
        } finally {
            readLock.unlock();
        }

        Map<RequestMethods, Reaction> reactions = httpServer.getReactionMap().get(request.getUrl());

        if (reactions != null) {
            return reactions.get(method);
        }

        return null;
    }
}
