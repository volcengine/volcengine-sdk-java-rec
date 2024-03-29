package volcengine.core;

import volcengine.core.volcAuth.Credential;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static volcengine.core.Constant.VOLC_AUTH_SERVICE;

@Getter
public class Context {

    // A unique token assigned by bytedance, which is used to
    // generate an authenticated signature when building a request.
    // It is sometimes called "secret".
    private final String token;

    private Credential volcCredential;

    private final boolean useAirAuth;

    private final String tenantId;

    // A unique identity assigned by Bytedance, which is need to fill in URL.
    // It is sometimes called "company".
    private final String tenant;

    // Schema of URL, server supports both "HTTPS" and "HTTP",
    // in order to ensure communication security, please use "HTTPS"
    private String schema = "https";

    // Server address, china use "api.byteair.volces.com", sg use "byteair-api-sg1.byteintlapi.com".
    private List<String> hosts;

    // Customer-defined http headers, all requests will include these headers
    private Map<String, String> customerHeaders = Collections.emptyMap();

    @Slf4j
    @Accessors(chain = true)
    @Setter
    public static class Param {
        private String tenant;

        private String tenantId;

        private String token;

        private String ak; // AccessKey of a volcengine tenant

        private String sk; // SecretKey of a volcengine tenant

        private boolean useAirAuth;

        private String schema;

        private List<String> hosts;

        private Map<String, String> headers;

        private Region region;
    }

    public Context(Param param) {
        checkRequiredField(param);
        this.tenant = param.tenant;
        this.tenantId = param.tenantId;
        this.token = param.token;
        fillHosts(param);
        fillVolcCredential(param);

        if (Objects.nonNull(param.schema)) {
            this.schema = param.schema;
        }
        if (Objects.nonNull(param.headers)) {
            this.customerHeaders = param.headers;
        }
        this.useAirAuth = param.useAirAuth;
    }

    private void checkRequiredField(Param param) {
        if (Objects.isNull(param.tenant)) {
            throw new RuntimeException("application id is null");
        }
        if (Objects.isNull(param.tenantId)) {
            throw new RuntimeException("tenant id is null");
        }
        if (Objects.isNull(param.region)) {
            throw new RuntimeException("region is null");
        }
        checkAuthRequiredField(param);
    }

    private void checkAuthRequiredField(Param param) {
        // air auth need token
        if (param.useAirAuth) {
            if (Objects.isNull(param.token)) {
                throw new RuntimeException("token cannot be null");
            }
            return;
        }
        // volc auth need ak and sk
        if (Objects.isNull(param.ak) || param.ak.equals("") ||
                Objects.isNull(param.sk) || param.sk.equals("")) {
            throw new RuntimeException("ak and sk cannot be null");
        }
    }

    private void fillHosts(Param param) {
        if (Objects.nonNull(param.hosts) && !param.hosts.isEmpty()) {
            this.hosts = param.hosts;
            return;
        }
        if (param.region == Region.AIR_CN) {
            hosts = Constant.AIR_CN_HOSTS;
            return;
        }
        if (param.region == Region.AIR_SG) {
            hosts = Constant.AIR_SG_HOSTS;
            return;
        }
    }

    private void fillVolcCredential(Param param) {
        String region = "";
        switch (param.region) {
            case AIR_SG:
                region = "ap-singapore-1";
                break;
            default: //Region "CN" and "AIR_CN" belong to "cn-north-1"
                region = "cn-north-1";
        }
        this.volcCredential = new Credential(param.ak, param.sk, VOLC_AUTH_SERVICE, region);
    }
}
