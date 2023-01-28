package volcengine.common;

import volcengine.core.Context;
import volcengine.core.URLCenter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class CommonURL implements URLCenter {
    // The URL format of operation information
    // Example: https://tob.sgsnssdk.com/data/api/retail/retail_demo/operation?method=get
    private final static String OPERATION_URL_FORMAT = "%s://%s/data/api/%s/operation?method=%s";

    // The URL format of done information
    // Example: https://tob.sgsnssdk.com/data/api/retail/retail_demo/done?topic=user
    private final static String DONE_URL_FORMAT = "%s://%s/data/api/%s/done?topic={}";

    // The URL of getting operation information which is real-time
    // Example: https://tob.sgsnssdk.com/data/api/retail_demo/operation?method=get
    private List<String> getOperationUrl;

    // The URL of query operations information which is non-real-time
    // Example: https://tob.sgsnssdk.com/data/api/retail_demo/operation?method=list
    private List<String> listOperationsUrl;

    // The URL of mark certain days that data synchronization is complete
    // Example: https://tob.sgsnssdk.com/data/api/retail_demo/done?topic=user
    private List<String> doneUrlFormat;

    protected String schema;

    protected String tenant;

    protected Random random;

    protected CommonURL(Context context) {
        this.schema = context.getSchema();
        this.tenant = context.getTenant();
        this.refresh(context.getHosts());
        this.random = new Random();
    }

    @Override
    public void refresh(List<String> hosts) {
        List<String> getOperationUrlTmp = new ArrayList<>();
        List<String> listOperationsUrlTmp = new ArrayList<>();
        List<String> doneUrlFormatTmp = new ArrayList<>();
        for (String host : hosts) {
            getOperationUrlTmp.add(String.format(OPERATION_URL_FORMAT, schema, host, tenant, "get"));
            listOperationsUrlTmp.add(String.format(OPERATION_URL_FORMAT, schema, host, tenant, "list"));
            doneUrlFormatTmp.add(String.format(DONE_URL_FORMAT, schema, host, tenant));
        }
        getOperationUrl = getOperationUrlTmp;
        listOperationsUrl = listOperationsUrlTmp;
        doneUrlFormat = doneUrlFormatTmp;
    }
}
