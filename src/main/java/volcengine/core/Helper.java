package volcengine.core;

import java.util.Objects;

import static volcengine.core.metrics.Helper.Counter;
import static volcengine.core.metrics.Helper.Latency;


public final class Helper {
    public static String bytes2Hex(byte[] bts) {
        StringBuilder sb = new StringBuilder();
        String hex;
        for (byte bt : bts) {
            hex = (Integer.toHexString(bt & 0xff));
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //report success request
    public static void reportRequestSuccess(String metricsPrefix, String url, long begin) {
        String[] urlTag = buildUrlTags(url);
        String[] tagKvs = appendBaseTags(urlTag);
        Latency(buildLatencyKey(metricsPrefix), begin, tagKvs);
        Counter(buildCountKey(metricsPrefix), 1, tagKvs);
    }

    //report fail request
    public static void reportRequestError(String metricsPrefix, String url, long begin, int code, String message) {
        String[] urlTag = buildUrlTags(url);
        String[] tagKvs = appendTags(urlTag, "code:" + code, "message:" + message);
        tagKvs = appendBaseTags(tagKvs);
        Latency(buildLatencyKey(metricsPrefix), begin, tagKvs);
        Counter(buildCountKey(metricsPrefix), 1, tagKvs);
    }

    // report exception
    public static void reportRequestException(String metricsPrefix, String url, long begin, Throwable e) {
        String[] tagKvs = withExceptionTags(buildUrlTags(url), e);
        tagKvs = appendBaseTags(tagKvs);
        Latency(buildLatencyKey(metricsPrefix), begin, tagKvs);
        Counter(buildCountKey(metricsPrefix), 1, tagKvs);
    }

    public static String[] withExceptionTags(String[] tagKvs, Throwable e) {
        String msgTag;
        String msg = e.getMessage().toLowerCase();
        if (msg.contains("time") && msg.contains("out")) {
            if (msg.contains("connect")) {
                msgTag = "message:connect-timeout";
            } else if (msg.contains("read")) {
                msgTag = "message:read-timeout";
            } else {
                msgTag = "message:timeout";
            }
        } else {
            msgTag = "message:other";
        }
        return appendTags(tagKvs, msgTag);
    }


    private static String[] buildUrlTags(String url) {
        if (url.contains("ping")) {
            return new String[]{"url:" + adjustUrlTag(url), "req_type:ping"};
        }
        if (url.contains("data/api")) {
            String tenant = parseTenant(url), scene = parseScene(url);
            return new String[]{"url:" + adjustUrlTag(url), "req_type:data-api", "tenant:" + tenant, "scene:" + scene};
        }
        if (url.contains("predict/api")) {
            String tenant = parseTenant(url), scene = parseScene(url);
            return new String[]{"url:" + adjustUrlTag(url), "req_type:predict-api", "tenant:" + tenant, "scene:" + scene};
        }
        return new String[]{"url:" + adjustUrlTag(url), "req_type:unknown"};
    }

    private static String parseTenant(String url) {
        String[] sp = url.split("\\?")[0].split("/");
        if (sp.length < 2) {
            return "";
        }
        return sp[sp.length - 2];
    }

    private static String parseScene(String url) {
        String[] sp = url.split("\\?")[0].split("/");
        if (sp.length < 2) {
            return "";
        }
        return sp[sp.length - 1];
    }

    /**
     * Parsing the url tag, replace the "=" in the url with "_is_",
     * because "=" is a special delimiter in metrics server
     * For example, url=http://xxxx?query=yyy, the direct report will fail,
     * instead, using url=http://xxxx?query_is_yyy.
     *
     * @param url full request url
     */
    private static String adjustUrlTag(String url) {
        return url.replaceAll("=", "_is_");
    }

    private static String[] appendTags(String[] oldTags, String... tags) {
        if (Objects.isNull(tags) || tags.length == 0) {
            return oldTags;
        }
        String[] newTags = new String[oldTags.length + tags.length];
        System.arraycopy(oldTags, 0, newTags, 0, oldTags.length);
        System.arraycopy(tags, 0, newTags, oldTags.length, tags.length);
        return newTags;
    }

    private static String parseReqType(String url) {
        if (url.contains("ping")) {
            return "ping";
        }
        if (url.contains("data/api")) {
            return "data-api";
        }
        if (url.contains("predict/api")) {
            return "predict-api";
        }
        return "unknown";
    }

    public static String buildCountKey(String metricsPrefix) {
        return metricsPrefix + "." + "count";
    }

    public static String buildLatencyKey(String metricsPrefix) {
        return metricsPrefix + "." + "latency";
    }

    public static String version = "1.2.0";

    public static String[] appendBaseTags(String[] tags) {
        String[] baseTags = new String[]{"language:java", "version:" + version};
        return appendTags(tags, baseTags);
    }
}
