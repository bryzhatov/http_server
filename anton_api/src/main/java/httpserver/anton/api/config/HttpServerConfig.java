package httpserver.anton.api.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-12
 */
@Data
@NoArgsConstructor
public class HttpServerConfig extends Properties {

    public HttpServerConfig(Properties properties) {
        putAll(properties);
    }

    public HttpServerConfig setHost(String host) {
        this.setProperty(ServerKeysConfig.HOST, host);
        return this;
    }

    public HttpServerConfig setPort(int port) {
        this.setProperty(ServerKeysConfig.PORT, Integer.toString(port));
        return this;
    }

    public HttpServerConfig setUrlStaticResouruse(String prefix) {
        this.setProperty(ServerKeysConfig.STATIC_RESOURCE_PREFIX, prefix);
        return this;
    }

    public HttpServerConfig setVisibleRequest(boolean visibleRequest) {
        this.setProperty(ServerKeysConfig.VISIBLE_REQUEST, String.valueOf(visibleRequest));
        return this;
    }

    public HttpServerConfig setKeepAlive(boolean keepAlive) {
        this.setProperty(ServerKeysConfig.KEEP_ALIVE, String.valueOf(keepAlive));
        return this;
    }

    /**
     * @param keepAliveTimeOut is time in second
     */
    public HttpServerConfig setKeepAliveTimeOut(int keepAliveTimeOut) {
        this.setProperty(ServerKeysConfig.KEEP_ALIVE_TIME, String.valueOf(keepAliveTimeOut));
        return this;
    }

    public HttpServerConfig setKeepAliveTransactional(int countTransactional) {
        this.setProperty(ServerKeysConfig.KEEP_ALIVE_TRANSACTIONAL,
                String.valueOf(countTransactional));
        return this;
    }

    public HttpServerConfig setEmptyRequestTimeOut(int emptyRequestTimeOut) {
        this.setProperty(ServerKeysConfig.EMPTY_REQUEST_TIME,
                String.valueOf(emptyRequestTimeOut));
        return this;
    }

    public HttpServerConfig setPoolType(String poolType) {
        this.setProperty(ServerKeysConfig.POOL_TYPE, poolType);
        return this;
    }

    // Request Pool

    public HttpServerConfig setCoreRequestPoolSize(int coreRequestPoolSize) {
        this.setProperty(ServerKeysConfig.CORE_REQUEST_POOL_SIZE,
                String.valueOf(coreRequestPoolSize));
        return this;
    }

    public HttpServerConfig setMaxRequestPoolSize(int maxRequestPoolSize) {
        this.setProperty(ServerKeysConfig.MAX_REQUEST_POOL_SIZE,
                String.valueOf(maxRequestPoolSize));
        return this;
    }

    public HttpServerConfig setTimeIdleRequestPool(int timeIdleRequestPool) {
        this.setProperty(ServerKeysConfig.TIME_IDLE_REQUEST_POOL,
                String.valueOf(timeIdleRequestPool));
        return this;
    }

    // KeepAlive Pool

    public HttpServerConfig setCoreKeepAlivePoolSize(int coreKeepAlivePoolSize) {
        this.setProperty(ServerKeysConfig.CORE_KEEP_ALIVE_POOL_SIZE,
                String.valueOf(coreKeepAlivePoolSize));
        return this;
    }

    public HttpServerConfig setMaxKeepAlivePoolSize(int maxKeepAlivePoolSize) {
        this.setProperty(ServerKeysConfig.MAX_KEEP_ALIVE_POOL_SIZE,
                String.valueOf(maxKeepAlivePoolSize));
        return this;
    }

    public HttpServerConfig setTimeIdleKeepAlivePool(int timeIdleKeepAlivePool) {
        this.setProperty(ServerKeysConfig.TIME_IDLE_KEEP_ALIVE_POOL,
                String.valueOf(timeIdleKeepAlivePool));
        return this;
    }
}
