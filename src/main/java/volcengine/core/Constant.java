package volcengine.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Constant {

    public final static int MAX_WRITE_ITEM_COUNT = 2000;

    public final static int MAX_IMPORT_ITEM_COUNT = 10000;

    final static List<String> AIR_CN_HOSTS = Collections.singletonList("api.byteair.volces.com");

    final static List<String> AIR_SG_HOSTS = Collections.singletonList("byteair-api-sg1.byteintlapi.com");

    /**
     * All requests will have a XXXResponse corresponding to them,
     * and all XXXResponses will contain a 'Status' field.
     * The status of this request can be determined by the value of `Status.Code`
     * Detail error code info：https://docs.byteplus.com/docs/error-code
     */
    // The request was executed successfully without any exception
    public final static int STATUS_CODE_SUCCESS = 0;
    // A Request with the same "Request-ID" was already received. This Request was rejected
    public final static int STATUS_CODE_IDEMPOTENT = 409;
    // Operation information is missing due to an unknown exception
    public final static int STATUS_CODE_OPERATION_LOSS = 410;
    // The server hope slow down request frequency, and this request was rejected
    public final static int STATUS_CODE_TOO_MANY_REQUEST = 429;

    public final static String VOLC_AUTH_SERVICE = "air";


    // metric name for volcengineSDK sdk
    public static final String METRICS_KEY_INVOKE_SUCCESS = "invoke.success";
    public static final String METRICS_KEY_INVOKE_ERROR = "invoke.error";

    public static final String METRICS_KEY_PING_SUCCESS = "ping.success";
    public static final String METRICS_KEY_PING_ERROR = "ping.error";

}
