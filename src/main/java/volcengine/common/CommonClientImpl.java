package volcengine.common;

import volcengine.core.BizException;
import volcengine.core.Context;
import volcengine.core.HTTPCaller;
import volcengine.core.NetException;
import volcengine.core.Option;
import volcengine.core.URLCenter;
import com.google.protobuf.Parser;
import lombok.extern.slf4j.Slf4j;
import volcengine.common.protocol.VolcengineCommon.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class CommonClientImpl implements CommonClient, URLCenter {

    protected final Context context;

    protected final HTTPCaller httpCaller;

    protected CommonURL commonURL;

    protected CommonClientImpl(Context.Param param) {
        this.context = new Context(param);
        this.httpCaller = new HTTPCaller(context);
        this.commonURL = new CommonURL(context);
    }

    @Override
    public final void refresh(List<String> hosts) {
        this.commonURL.refresh(hosts);
        doRefresh(hosts);
    }

    public abstract void doRefresh(List<String> host);

    public final void release() {
        doRelease();
    }

    public void doRelease() {

    }

    @Override
    public OperationResponse getOperation(
            GetOperationRequest request, Option... opts) throws NetException, BizException {
        Parser<OperationResponse> parser = OperationResponse.parser();
        int index = commonURL.getRandom().nextInt(commonURL.getGetOperationUrl().size());
        String url = commonURL.getGetOperationUrl().get(index);
        OperationResponse response = httpCaller.doPBRequest(url, request, parser, Option.conv2Options(opts));
        log.debug("[volcengineSDK][GetOperations] rsp:\n{}", response);
        return response;
    }

    @Override
    public ListOperationsResponse listOperations(
            ListOperationsRequest request, Option... opts) throws NetException, BizException {
        Parser<ListOperationsResponse> parser = ListOperationsResponse.parser();
        int index = commonURL.getRandom().nextInt(commonURL.getListOperationsUrl().size());
        String url = commonURL.getListOperationsUrl().get(index);
        ListOperationsResponse response = httpCaller.doPBRequest(url, request, parser, Option.conv2Options(opts));
        log.debug("[volcengineSDK][ListOperations] rsp:\n{}", response);
        return response;
    }

    @Override
    public DoneResponse done(List<LocalDate> dateList, String topic, Option... opts) throws NetException, BizException {
        List<Date> dates = new ArrayList<>();
        for (LocalDate date : dateList) {
            addDoneDate(dates, date);
        }
        int index = commonURL.getRandom().nextInt(commonURL.getDoneUrlFormat().size());
        String urlFormat = commonURL.getDoneUrlFormat().get(index);
        String url = urlFormat.replace("{}", topic);
        DoneRequest request = DoneRequest.newBuilder().addAllDataDates(dates).build();
        Parser<DoneResponse> parser = DoneResponse.parser();
        DoneResponse response = httpCaller.doPBRequest(url, request, parser, Option.conv2Options(opts));
        log.debug("[volcengineSDK][Done] rsp:\n{}", response);
        return response;
    }

    private void addDoneDate(List<Date> dateMapList, LocalDate date) {
        dateMapList.add(buildDoneDate(date));
    }

    private Date buildDoneDate(LocalDate date) {
        return Date.newBuilder().setYear(date.getYear()).setMonth(date.getMonthValue()).
                setDay(date.getDayOfMonth()).build();
    }
}
