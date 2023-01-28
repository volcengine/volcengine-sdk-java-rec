package volcengine.byteair;

import volcengine.byteair.protocol.VolcengineByteair.*;
import volcengine.common.CommonClientImpl;
import volcengine.core.*;
import com.google.protobuf.Parser;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static volcengine.core.Constant.MAX_IMPORT_ITEM_COUNT;

@Slf4j
public class ByteairClientImpl extends CommonClientImpl implements ByteairClient {

    private final static String ERR_MSG_TOO_MANY_ITEMS =
            String.format("Only can receive max to %d items in one request", MAX_IMPORT_ITEM_COUNT);

    public final static String DEFAULT_PREDICT_SCENE = "default";

    private final ByteairURL byteairURL;

    ByteairClientImpl(Context.Param param) {
        super(param);
        this.byteairURL = new ByteairURL(context);
    }

    @Override
    public void doRefresh(List<String> hosts) {
        this.byteairURL.refresh(hosts);
    }

    @Override
    public WriteResponse writeData(List<Map<String, Object>> dataList, String topic,
                                   Option... opts) throws NetException, BizException {
        if (Objects.nonNull(dataList) && dataList.size() > MAX_IMPORT_ITEM_COUNT) {
            throw new BizException(ERR_MSG_TOO_MANY_ITEMS);
        }
        Parser<WriteResponse> parser = WriteResponse.parser();
        int index = byteairURL.getRandom().nextInt(byteairURL.getWriteDataUrlFormat().size());
        String urlFormat = byteairURL.getWriteDataUrlFormat().get(index);
        String url = urlFormat.replace("{}", topic);
        WriteResponse response = httpCaller.doJSONRequest(url, dataList, parser, Option.conv2Options(opts));
        log.debug("[volcengineSDK][WriteData] rsp:\n{}", response);
        return response;
    }

    @Override
    public PredictResponse predict(PredictRequest request,
                                   Option... opts) throws NetException, BizException {
        Options options = Option.conv2Options(opts);
        String scene = getSceneFromOpts(options);
        int index = byteairURL.getRandom().nextInt(byteairURL.getPredictUrlFormat().size());
        String url = byteairURL.getPredictUrlFormat().get(index).replace("{}", scene);
        Parser<PredictResponse> parser = PredictResponse.parser();
        PredictResponse response = httpCaller.doPBRequest(url, request, parser, options);
        log.debug("[volcengineSDK][Predict] rsp:\n{}", response);
        return response;
    }

    private String getSceneFromOpts(Options options) {
        if (Objects.isNull(options.getScene()) || "".equals(options.getScene())) {
            return DEFAULT_PREDICT_SCENE;
        }
        return options.getScene();
    }

    @Override
    public CallbackResponse callback(CallbackRequest request,
                                     Option... opts) throws NetException, BizException {
        Parser<CallbackResponse> parser = CallbackResponse.parser();
        int index = byteairURL.getRandom().nextInt(byteairURL.getCallbackUrl().size());
        String url = byteairURL.getCallbackUrl().get(index);
        CallbackResponse response = httpCaller.doPBRequest(url, request, parser, Option.conv2Options(opts));
        log.debug("[volcengineSDK][Callback] rsp:\n{}", response);
        return response;
    }
}
