volcengine data/predict api sdk, java version
<br><br>
demo
```java
import com.alibaba.fastjson.JSON;
import volcengine.byteair.ByteairClient;
import volcengine.byteair.ByteairClientBuilder;
import volcengine.byteair.protocol.VolcengineByteair;
import volcengine.common.protocol.VolcengineCommon;
import volcengine.core.BizException;
import volcengine.core.NetException;
import volcengine.core.Option;
import volcengine.core.Region;
import volcengine.core.metrics.MetricsCollector;

import java.time.LocalDate;
import java.util.*;

public class Example {

    public static ByteairClient byteairClient;

    public static void init() {
        byteairClient = new ByteairClientBuilder()
                // 必传,租户id.
                .tenantId("xxx")
                // 必传,项目id.
                .applicationId("xxx")
                // 必传,密钥AK,获取方式:【火山引擎控制台】->【个人信息】->【密钥管理】中获取.
                .ak("xxx")
                // 必传,密钥SK,获取方式：【火山引擎控制台】->【个人信息】->【密钥管理】中获取.
                .sk("xxx")
                // 必传,国内使用AIR_CN.
                .region(Region.AIR_CN)
                // 进行构建client
                .build();
        // metrics上报初始化.建议开启,方便火山侧排查问题.
        MetricsCollector.Init();
    }


    public static void write() {
        // 第一条数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", "1");
        item1.put("title", "test_title1");
        item1.put("status", 0);
        item1.put("brand", "volcengine");
        item1.put("pub_time", 1583641807);
        item1.put("current_price", 1.1);
        // 第二条数据
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", "1");
        item2.put("title", "test_title2");
        item1.put("status", 1);
        item2.put("brand", "volcengine");
        item2.put("pub_time", 1583641503);
        item2.put("current_price", 2.2);

        List<Map<String, Object>> datas = new ArrayList<>();
        datas.add(item1);
        datas.add(item2);

        // topic为枚举值，请参考API文档
        String topic = "item";
        Option[] opts = new Option[]{
                // 预同步("pre_sync"),历史数据同步("history_sync"),增量天级同步("incremental_sync_daily"),增量实时同步("incremental_sync_streaming")
                Option.withStage("pre_sync"),
                // 必传,数据产生日期,实际传输时需修改为实际日期.增量实时同步可不传.
                Option.withDataDate(LocalDate.of(2022, 1, 1)),
                Option.withRequestId(UUID.randomUUID().toString()),
        };

        VolcengineByteair.WriteResponse writeResponse;
        try {
            writeResponse = byteairClient.writeData(datas, topic, opts);
        } catch (NetException | BizException e) {
            System.out.printf("[WriteData] occur error, msg:%s\n", e.getMessage());
            return;
        }
        if (!writeResponse.getStatus().getSuccess()) {
            System.out.println("[WriteData] failure");
            return;
        }
        System.out.println("[WriteData] success");
    }


    public static void done() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        // 已经上传完成的数据日期，可在一次请求中传多个
        List<LocalDate> partitionDateList = Collections.singletonList(date);
        // 与离线天级数据传输的topic保持一致
        String topic = "item";
        Option[] opts = new Option[]{
                // 预同步("pre_sync"),历史数据同步("history_sync"),增量天级同步("incremental_sync_daily"),增量实时同步("incremental_sync_streaming")
                Option.withStage("pre_sync"),
                Option.withRequestId(UUID.randomUUID().toString()),
        };

        VolcengineCommon.DoneResponse doneResponse;
        try {
            doneResponse = byteairClient.done(partitionDateList, topic, opts);
        } catch (BizException | NetException e) {
            System.out.printf("[Done] occur error, msg:%s \n", e.getMessage());
            return;
        }
        if (!doneResponse.getStatus().getSuccess()) {
            System.out.println("[Done] failure");
            return;
        }
        System.out.printf("[Done] success");
    }

    public static void predict() {
        // 请求体
        VolcengineByteair.PredictUser user = VolcengineByteair.PredictUser.newBuilder()
                .setUid("uid1")
                .build();
        VolcengineByteair.PredictContext context = VolcengineByteair.PredictContext.newBuilder()
                .setSpm("1$##$2$##$3$##$4")
                .putExtra("extra_key", "extra_value")
                .build();
        List<VolcengineByteair.PredictCandidateItem> items = new ArrayList<>();
        items.add(VolcengineByteair.PredictCandidateItem.newBuilder().setId("item_id1").build());
        items.add(VolcengineByteair.PredictCandidateItem.newBuilder().setId("item_id2").build());
        VolcengineByteair.PredictRequest predictRequest = VolcengineByteair.PredictRequest.newBuilder()
                .setUser(user)
                .setContext(context)
                .addAllCandidateItems(items)
                .build();

        Option[] predictOpts = new Option[]{
                Option.withScene("default"),
                Option.withRequestId(String.valueOf(UUID.randomUUID())),
                Option.withHeaders(new HashMap<>() {{
                    put("Enable-Spm-Route", "true");
                }})
        };

        VolcengineByteair.PredictResponse predictResponse;
        try {
            predictResponse = byteairClient.predict(predictRequest, predictOpts);
        } catch (BizException | NetException e) {
            System.out.printf("[predict] occur error, msg:%s \n", e.getMessage());
            return;
        }
        if (!predictResponse.getSuccess()) {
            System.out.println("[predict] failure");
            return;
        }
        System.out.println("[predict] success");
    }

    public static void callback() {
        // 请求体
        List<VolcengineByteair.CallbackItem> callbackItems = new ArrayList<>();
        callbackItems.add(VolcengineByteair.CallbackItem.newBuilder()
                .setId("item_id1")
                .setPos("position1")
                .setExtra(JSON.toJSONString(new HashMap<String, String>() {{
                    put("reason", "exposure");
                }}))
                .build());
        callbackItems.add(VolcengineByteair.CallbackItem.newBuilder()
                .setId("item_id2")
                .setPos("position2")
                .setExtra(JSON.toJSONString(new HashMap<String, String>() {{
                    put("reason", "filter");
                }}))
                .build());
        VolcengineByteair.CallbackContext callbackContext = VolcengineByteair.CallbackContext.newBuilder()
                .setSpm("1$##$2$##$3$##$4")
                .putExtra("extra_key", "extra_value")
                .build();
        VolcengineByteair.CallbackRequest callbackRequest = VolcengineByteair.CallbackRequest.newBuilder()
                // 对应的predict请求的request id
                .setPredictRequestId("xxx")
                // 对应的predict请求的uid
                .setUid("uid1")
                // 设置上下文
                .setContext(callbackContext)
                // 对应的predict请求的scene.
                .setScene("default")
                // 对应的predict请求的items列表
                .addAllItems(callbackItems)
                .build();

        Option[] opts = new Option[]{
                // 标识callback请求自己的request id,和callback请求体里的predict request id含义不同.
                Option.withRequestId(UUID.randomUUID().toString()),
                // 是否开启SPM路由.开启的话需要保证请求体里的SPM存在且绑定了栏位.
                // server会根据body里的SPM路由到选择的栏位.
                Option.withHeaders(new HashMap<>() {{
                    put("Enable-Spm-Route", "true");
                }})
        };

        VolcengineByteair.CallbackResponse callbackResponse;
        try {
            callbackResponse = byteairClient.callback(callbackRequest, opts);
        } catch (NetException | BizException e) {
            System.out.printf("[callback] occur error, msg:%s \n", e.getMessage());
            return;
        }
        if (!callbackResponse.getSuccess()) {
            System.out.println("[callback] failure");
            return;
        }
        System.out.println("[callback] success");
    }
}

```