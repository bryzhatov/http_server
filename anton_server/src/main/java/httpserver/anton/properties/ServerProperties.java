package httpserver.anton.properties;

import httpserver.anton.api.config.ServerKeysConfig;

import java.util.Map;
import java.util.Properties;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-06
 */
public class ServerProperties {

    protected final Properties innerProperty = new Properties();

    protected ServerProperties() {
    }

    public String getHost() {
        return innerProperty.getProperty(ServerKeysConfig.HOST);
    }

    public Integer getPort() {
        return Integer.valueOf(innerProperty.getProperty(ServerKeysConfig.PORT));
    }

    public boolean isVisibleRequest() {
        return Boolean.parseBoolean(innerProperty.getProperty(ServerKeysConfig.VISIBLE_REQUEST));
    }

    public boolean isKeepAliveOn() {
        return Boolean.parseBoolean(innerProperty.getProperty(ServerKeysConfig.KEEP_ALIVE));
    }

    public int getKeepAliveTimeout() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.KEEP_ALIVE_TIME));
    }

    public int getKeepAliveTransactional() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.KEEP_ALIVE_TRANSACTIONAL));
    }

    public int getEmptyRequestTimeOut() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.EMPTY_REQUEST_TIME));
    }

    public String getPoolType() {
        return innerProperty.getProperty(ServerKeysConfig.POOL_TYPE);
    }

    // Request Pool

    public int getCoreRequestPoolSize() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.CORE_REQUEST_POOL_SIZE));
    }

    public int getMaxRequestPoolSize() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.MAX_REQUEST_POOL_SIZE));
    }

    public int getTimeIdleRequestPool() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.TIME_IDLE_REQUEST_POOL));
    }

    // KeepAlive Pool

    public int getCoreKeepAlivePoolSize() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.CORE_KEEP_ALIVE_POOL_SIZE));
    }

    public int getMaxKeepAlivePoolSize() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.MAX_KEEP_ALIVE_POOL_SIZE));
    }

    public int getTimeIdleKeepAlivePool() {
        return Integer.parseInt(innerProperty.getProperty(ServerKeysConfig.TIME_IDLE_KEEP_ALIVE_POOL));
    }

    public void mergeOuterInInner(Properties outerProperty) {
        merge(outerProperty, innerProperty);
    }

    public void mergeInnerInOuter(Properties outerProperty) {
        merge(innerProperty, outerProperty);
    }

    private static void merge(Properties fromProperties, Properties toProperties) {
        for (Map.Entry<Object, Object> entry : fromProperties.entrySet()) {
            toProperties.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }
}
