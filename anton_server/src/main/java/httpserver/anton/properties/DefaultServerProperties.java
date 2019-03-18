package httpserver.anton.properties;

import httpserver.anton.api.config.ServerKeysConfig;
import lombok.extern.log4j.Log4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-06
 */
@Log4j
public class DefaultServerProperties extends ServerProperties {

    public DefaultServerProperties() {
        try {
            innerProperty.load(new FileReader("../conf/server.properties"));
            initDefaultProperty();
        } catch (IOException e) {
            initDefaultProperty();
            log.debug("Can't load properties file for server.", e);
        }
    }

    private void initDefaultProperty() {
        innerProperty.putIfAbsent(ServerKeysConfig.HOST, "localhost");
        innerProperty.putIfAbsent(ServerKeysConfig.PORT, "8080");
        innerProperty.putIfAbsent(ServerKeysConfig.KEEP_ALIVE, "true");
        innerProperty.putIfAbsent(ServerKeysConfig.VISIBLE_REQUEST, "false");
        innerProperty.putIfAbsent(ServerKeysConfig.KEEP_ALIVE_TIME, "10");
        innerProperty.putIfAbsent(ServerKeysConfig.KEEP_ALIVE_TRANSACTIONAL, "30");
        innerProperty.putIfAbsent(ServerKeysConfig.EMPTY_REQUEST_TIME, "500");

        innerProperty.putIfAbsent(ServerKeysConfig.POOL_TYPE, "executor");

        // Request Pool
        innerProperty.putIfAbsent(ServerKeysConfig.CORE_REQUEST_POOL_SIZE, "5");
        innerProperty.putIfAbsent(ServerKeysConfig.MAX_REQUEST_POOL_SIZE, "6");
        innerProperty.putIfAbsent(ServerKeysConfig.TIME_IDLE_REQUEST_POOL, "10000");
        // KeepAlive Pool
        innerProperty.putIfAbsent(ServerKeysConfig.CORE_KEEP_ALIVE_POOL_SIZE, "5");
        innerProperty.putIfAbsent(ServerKeysConfig.MAX_KEEP_ALIVE_POOL_SIZE, "6");
        innerProperty.putIfAbsent(ServerKeysConfig.TIME_IDLE_KEEP_ALIVE_POOL, "10000");
    }

    @Override
    public void mergeOuterInInner(Properties outerProperty) {
        throw new UnsupportedOperationException();
    }
}
