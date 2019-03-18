package httpserver.anton;

import httpserver.anton.api.Reaction;
import httpserver.anton.api.Server;
import httpserver.anton.api.config.HttpServerConfig;
import httpserver.anton.api.controller.RequestMethods;
import httpserver.anton.pool.ThreadPool;
import httpserver.anton.pool.factory.ThreadPoolFactory;
import httpserver.anton.pool.util.ThreadPoolDTO;
import httpserver.anton.properties.HttpServerProperties;
import httpserver.anton.service.RequestService;
import httpserver.anton.service.ResponseService;
import httpserver.anton.tasks.http.RequestHttpTask;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Comparator.comparingInt;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-12
 */
@Data
@Log4j
public class HttpServer implements Server {
    protected final Map<String, Map<RequestMethods, Reaction>> reactionMap = new ConcurrentHashMap<>();
    protected final ReadWriteLock lockPatternMap = new ReentrantReadWriteLock(true);
    protected final RequestService requestService = new RequestService();
    protected final Map<String, Map<RequestMethods, Reaction>> patternMap =
            new TreeMap<>(comparingInt(String::length).reversed());
    protected final ServerSocket socketServer = new ServerSocket();
    protected final ResponseService responseService;
    protected final HttpServerProperties propServer;
    protected final ThreadPool keepAliveThreadPool;
    protected final ThreadPool requestThreadPool;

    public HttpServer(HttpServerConfig config) throws IOException {
        propServer = new HttpServerProperties(config);

        responseService = new ResponseService(propServer);

        keepAliveThreadPool = new ThreadPoolFactory(getKeepAlivePoolDTO(propServer))
                .getPool(propServer.getPoolType());
        requestThreadPool = new ThreadPoolFactory(getRequestPoolDTO(propServer))
                .getPool(propServer.getPoolType());
    }

    @Override
    public void deploy() {
        try {
            socketServer.bind(new InetSocketAddress(propServer.getHost(), propServer.getPort()));
            log.info("Deployed by: " + propServer.getHost() + ":" + propServer.getPort());
            socketServer.setSoTimeout(propServer.getEmptyRequestTimeOut());

        } catch (SocketException e) {
            log.error("Error when set time out for server.", e);
        } catch (IOException e) {
            log.error("Error deploy.", e);
        }


        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket connection = socketServer.accept();

                RequestHttpTask requestHttpTask = new RequestHttpTask(this, connection);
                requestThreadPool.handle(requestHttpTask);

            } catch (SocketTimeoutException e) {
                if (Thread.currentThread().isInterrupted()) {
                    close();
                }
            } catch (IOException e) {
                log.error("Error with new connection.", e);
            }
        }
    }

    @Override
    public void close() {
        try {
            requestThreadPool.shutdownNow();
            keepAliveThreadPool.shutdownNow();

            if (socketServer.isClosed()) {
                socketServer.close();
            }
            log.info("HttpServer by http:/" + socketServer.getLocalSocketAddress() + " was closed.");
        } catch (IOException e) {
            log.error("Error when close HttpServer.", e);
        }
    }

    @Override
    public void addReaction(String url, RequestMethods method, Reaction reaction) {
        if (StringUtils.countMatches(url, "*") == 1 && StringUtils.endsWith(url, "/*")) {
            url = StringUtils.removeEnd(url, "*");
            Lock writeLock = lockPatternMap.writeLock();
            try {
                writeLock.lock();
                addReaction(patternMap, url, method, reaction);
            } finally {
                writeLock.unlock();
            }
        } else {
            addReaction(reactionMap, url, method, reaction);
        }
    }

    private void addReaction(Map<String, Map<RequestMethods, Reaction>> map, String url, RequestMethods method, Reaction reaction) {
        Map<RequestMethods, Reaction> valueReactionMap = map.get(url);

        if (valueReactionMap != null && valueReactionMap.get(method) != null) {
            throw new IllegalArgumentException("Duplicate reaction url: " + url + ", method: " + method.name());
        }

        if (valueReactionMap == null) {
            valueReactionMap = new ConcurrentHashMap<>();
            map.put(url, valueReactionMap);
        }

        valueReactionMap.put(method, reaction);
    }

    private ThreadPoolDTO getRequestPoolDTO(HttpServerProperties properties) {
        ThreadPoolDTO threadPoolDTO = new ThreadPoolDTO();
        threadPoolDTO.setCorePoolSize(properties.getCoreRequestPoolSize());
        threadPoolDTO.setMaxPoolSize(properties.getMaxRequestPoolSize());
        threadPoolDTO.setKeepAliveTime(properties.getTimeIdleRequestPool());
        return threadPoolDTO;
    }

    private ThreadPoolDTO getKeepAlivePoolDTO(HttpServerProperties properties) {
        ThreadPoolDTO threadPoolDTO = new ThreadPoolDTO();
        threadPoolDTO.setCorePoolSize(properties.getCoreKeepAlivePoolSize());
        threadPoolDTO.setMaxPoolSize(properties.getMaxKeepAlivePoolSize());
        threadPoolDTO.setKeepAliveTime(properties.getTimeIdleKeepAlivePool());
        return threadPoolDTO;
    }
}
