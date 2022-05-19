package volcengine.common;

import volcengine.common.protocol.VolcengineCommon.*;
import volcengine.core.BizException;
import volcengine.core.NetException;
import volcengine.core.Option;

import java.time.LocalDate;
import java.util.List;

public interface CommonClient {
    // GetOperation
    //
    // Gets the operation of a previous long running call.
    OperationResponse getOperation(GetOperationRequest request,
                                   Option... opts) throws NetException, BizException;

    // ListOperations
    //
    // Lists operations that match the specified filter in the request.
    ListOperationsResponse listOperations(ListOperationsRequest request,
                                          Option... opts) throws NetException, BizException;

    // Done
    //
    // When the data of a day is imported completely,
    // you should notify bytedance through `done` method,
    // then bytedance will start handling the data in this day
    // @param dateList, optional, if dataList is empty, indicate target date is previous day
    DoneResponse done(List<LocalDate> dateList, String topic,
                      Option... opts) throws NetException, BizException;

    void release();
}
