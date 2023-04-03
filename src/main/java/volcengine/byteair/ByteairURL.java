package volcengine.byteair;

import volcengine.common.CommonURL;
import volcengine.core.Context;
import volcengine.core.URLCenter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ByteairURL extends CommonURL implements URLCenter {
    // The URL template of "predict" request, which need fill with "scene" info when use
    // Example: https://api.byteair.volces.com/predict/api/20013144/home
    private final static String PREDICT_URL_FORMAT = "%s://%s/predict/api/%s/{}";

    // The URL format of reporting the real exposure list
    // Example: https://api.byteair.volces.com/predict/api/20013144/callback
    private final static String CALLBACK_URL_FORMAT = "%s://%s/predict/api/%s/callback";

    // The URL format of data uploading
    // Example: https://api.byteair.volces.com/data/api/20013144/user?method=write
    private final static String UPLOAD_URL_FORMAT = "%s://%s/data/api/%s/{}?method=%s";

    // The URL format of marking a whole day data has been imported completely
    // Example: https://api.byteair.volces.com/data/api/20013144/done?topic=user
    private final static String DONE_URL_FORMAT = "%s://%s/data/api/%s/done?topic={}";

    // The URL template of "predict" request, which need fill with "scene" info when use
    // Example: https://api.byteair.volces.com/predict/api/20013144/home
    private volatile List<String> predictUrlFormat;

    // The URL of reporting the real exposure list
    // Example: https://api.byteair.volces.com/predict/api/20013144/callback
    private volatile List<String> callbackUrl;

    // The URL of uploading real-time user data
    // Example: https://api.byteair.volces.com/data/api/20013144/user?method=write
    private volatile List<String> writeDataUrlFormat;

    // The URL of importing batch offline user data
    // Example: https://api.byteair.volces.com/data/api/20013144/user?method=import
    private volatile List<String> importDataUrlFormat;

    // The URL format of marking a whole day data has been imported completely
    // Example: https://api.byteair.volces.com/data/api/20013144/done?topic=user
    private volatile List<String> doneUrlFormat;

    public ByteairURL(Context context) {
        super(context);
        refresh(context.getHosts());
    }

    @Override
    public void refresh(List<String> hosts) {
        List<String> predictUrlFormatTmp = new ArrayList<>();
        List<String> callbackUrlTmp = new ArrayList<>();
        List<String> writeDataUrlFormatTmp = new ArrayList<>();
        List<String> importDataUrlFormatTmp = new ArrayList<>();
        List<String> doneUrlFormatTmp = new ArrayList<>();
        for (String host : hosts) {
            predictUrlFormatTmp.add(String.format(PREDICT_URL_FORMAT, schema, host, tenant));
            callbackUrlTmp.add(String.format(CALLBACK_URL_FORMAT, schema, host, tenant));
            writeDataUrlFormatTmp.add(String.format(UPLOAD_URL_FORMAT, schema, host, tenant, "write"));
            importDataUrlFormatTmp.add(String.format(UPLOAD_URL_FORMAT, schema, host, tenant, "import"));
            doneUrlFormatTmp.add(String.format(DONE_URL_FORMAT, schema, host, tenant));
        }
        predictUrlFormat = predictUrlFormatTmp;
        callbackUrl = callbackUrlTmp;
        writeDataUrlFormat = writeDataUrlFormatTmp;
        importDataUrlFormat = importDataUrlFormatTmp;
        doneUrlFormat = doneUrlFormatTmp;
    }
}
