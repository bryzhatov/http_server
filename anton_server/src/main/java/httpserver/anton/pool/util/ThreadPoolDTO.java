package httpserver.anton.pool.util;

import lombok.Data;

@Data
public class ThreadPoolDTO {
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveTime;
}
