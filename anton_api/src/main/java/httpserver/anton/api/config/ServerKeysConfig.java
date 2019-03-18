package httpserver.anton.api.config;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-06
 */
public class ServerKeysConfig {
    private ServerKeysConfig() {
    }

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String VISIBLE_REQUEST = "visible_request";
    public static final String EMPTY_REQUEST_TIME = "empty_request_time_out";
    public static final String KEEP_ALIVE = "keep_alive";
    public static final String KEEP_ALIVE_TIME = "keep_alive_time_out";
    public static final String KEEP_ALIVE_TRANSACTIONAL = "keep_alive_count_transactional";
    public static final String POOL_TYPE = "pool_type";
    public static final String STATIC_RESOURCE_PREFIX = "static_resource_prefix";

    // Request Pool
    public static final String CORE_REQUEST_POOL_SIZE = "core_request_pool_size";
    public static final String MAX_REQUEST_POOL_SIZE = "max_request_pool_size";
    public static final String TIME_IDLE_REQUEST_POOL = "time_idle_request_pool";

    // KeepAlive Pool
    public static final String CORE_KEEP_ALIVE_POOL_SIZE = "core_keep_alive_pool_size";
    public static final String MAX_KEEP_ALIVE_POOL_SIZE = "max_keep_alive_pool_size";
    public static final String TIME_IDLE_KEEP_ALIVE_POOL = "time_idle_keep_alive_pool";
}
