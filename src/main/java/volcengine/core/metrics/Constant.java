package volcengine.core.metrics;


public class Constant {
    public static final String DEFAULT_METRICS_PREFIX = "volcengine.rec.sdk";
    public static final String DEFAULT_METRICS_HTTP_SCHEMA = "https";

    public static final String COUNTER_URL_FORMAT = "%s://%s/api/counter";
    public static final String OTHER_URL_FORMAT = "%s://%s/api/put";

    // metrics flush interval
    public static final int DEFAULT_FLUSH_INTERVAL_MS = 15 * 1000;

    public static final String DELIMITER = "+";

    public static final int RESERVOIR_SIZE = 65536;

    public final static int DEFAULT_HTTP_TIMEOUT_MS = 800;

    public final static int MAX_TRY_TIMES = 2;

    public final static int SUCCESS_HTTP_CODE = 200;

    enum MetricsType {
        metricsTypeCounter, metricsTypeTimer, metricsTypeStore
    }

}
