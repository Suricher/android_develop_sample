package com.pinger.mvp.model.net.intercept;

import com.pinger.mvp.utils.RetrofitLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static okhttp3.internal.http.StatusLine.HTTP_CONTINUE;

/**
 * @author Pinger
 * @since 2017/3/21 0021 上午 11:14
 * 日志拦截器，拦截日志，进行进行打印，方便调试
 */
public class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile HttpLoggingInterceptor.Level printLevel;
    private java.util.logging.Level colorLevel;
    private Logger logger;

    public HttpLoggingInterceptor() {
        this.logger = Logger.getLogger("NetGo");
        this.setPrintLevel(Level.BODY);
        this.setColorLevel(java.util.logging.Level.INFO);
        RetrofitLog.debug(true);
    }


    private void setPrintLevel(HttpLoggingInterceptor.Level level) {
        this.printLevel = level;
    }

    private void setColorLevel(java.util.logging.Level level) {
        this.colorLevel = level;
    }

    private void log(String message) {
        this.logger.log(this.colorLevel, message);
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (this.printLevel == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        } else {
            this.logForRequest(request, chain.connection());
            long startNs = System.nanoTime();

            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception var8) {
                this.log("<-- HTTP FAILED: " + var8);
                throw var8;
            }

            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            return this.logForResponse(response, tookMs);
        }
    }

    private void logForRequest(Request request, Connection connection) throws IOException {
        boolean logBody = this.printLevel == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = this.printLevel == HttpLoggingInterceptor.Level.BODY || this.printLevel == HttpLoggingInterceptor.Level.HEADERS;
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String e = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            this.log(e);
            if (logHeaders) {
                Headers headers = request.headers();
                int i = 0;

                for (int count = headers.size(); i < count; ++i) {
                    this.log("\t" + headers.name(i) + ": " + headers.value(i));
                }

                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        this.bodyToString(request);
                    } else {
                        this.log("\tbody: maybe [file part] , too large too print , ignored!");
                    }
                }
            }
        } catch (Exception var15) {
            RetrofitLog.e(var15);
        } finally {
            this.log("--> END " + request.method());
        }

    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = this.printLevel == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = this.printLevel == HttpLoggingInterceptor.Level.BODY || this.printLevel == HttpLoggingInterceptor.Level.HEADERS;

        Response var18;
        try {
            this.log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            if (!logHeaders) {
                return response;
            }

            Headers e = clone.headers();
            int body = 0;

            for (int count = e.size(); body < count; ++body) {
                this.log("\t" + e.name(body) + ": " + e.value(body));
            }

            if (!logBody || !hasBody(clone)) {
                return response;
            }

            if (!isPlaintext(responseBody.contentType())) {
                this.log("\tbody: maybe [file part] , too large too print , ignored!");
                return response;
            }

            String var17 = responseBody.string();
            this.log("\tbody:" + var17);
            responseBody = ResponseBody.create(responseBody.contentType(), var17);
            var18 = response.newBuilder().body(responseBody).build();
        } catch (Exception var15) {
            RetrofitLog.e(var15);
            return response;
        } finally {
            this.log("<-- END HTTP");
        }

        return var18;
    }

    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        } else if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        } else {
            String subtype = mediaType.subtype();
            if (subtype != null) {
                subtype = subtype.toLowerCase();
                if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) {
                    return true;
                }
            }

            return false;
        }
    }

    private void bodyToString(Request request) {
        try {
            Request e = request.newBuilder().build();
            Buffer buffer = new Buffer();
            e.body().writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = e.body().contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            this.log("\tbody:" + buffer.readString(charset));
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private boolean hasBody(Response response) {
        // HEAD requests never yield a body regardless of the response headers.
        if (response.request().method().equals("HEAD")) {
            return false;
        }

        int responseCode = response.code();
        if ((responseCode < HTTP_CONTINUE || responseCode >= 200)
                && responseCode != HTTP_NO_CONTENT
                && responseCode != HTTP_NOT_MODIFIED) {
            return true;
        }

        // If the Content-Length or Transfer-Encoding headers disagree with the response code, the
        // response is malformed. For best compatibility, we honor the headers.
        return contentLength(response) != -1
                || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"));

    }

    private long contentLength(Response response) {
        return contentLength(response.headers());
    }

    private long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private long stringToLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private enum Level {
        NONE,
        HEADERS,
        BODY
    }
}
