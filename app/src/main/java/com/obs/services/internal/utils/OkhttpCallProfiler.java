package com.obs.services.internal.utils;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class OkhttpCallProfiler extends EventListener {
    private final Consumer<StringBuilder> profiler;
    private final HashMap<String, Long> progressStartTime;
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
    private static final String ProFileTimeUnit = "ms";
    static protected boolean isEnabled = true;

    public OkhttpCallProfiler(Consumer<StringBuilder> profiler) {
        this.profiler = profiler;
        progressStartTime = new HashMap<>();
    }

    public OkhttpCallProfiler() {
        this.profiler = log::debug;
        progressStartTime = new HashMap<>();
    }

    /**
     * @param call
     */
    @Override
    public void callEnd(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" callEnd ");
        appendProgressTime("call", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param ioe
     */
    @Override
    public void callFailed(Call call, IOException ioe) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" callFailed ");
        appendProgressTime("call", stringBuilder);
        appendDetailIOException(ioe, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void callStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" callStart ");
        appendProgressTime("call", stringBuilder);
        appendDetailForCall(call, stringBuilder, true);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void canceled(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" call canceled.");
        appendProgressTime("call", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param inetSocketAddress
     * @param proxy
     * @param protocol
     */
    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" connectEnd ");
        appendProgressTime("connect", stringBuilder);
        appendDetailInetSocketAddress(inetSocketAddress, stringBuilder);
        appendDetailProxy(proxy, stringBuilder);
        appendDetailProtocol(protocol, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param inetSocketAddress
     * @param proxy
     * @param protocol
     * @param ioe
     */
    @Override
    public void connectFailed(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" connectFailed ");
        appendProgressTime("connect", stringBuilder);
        appendDetailInetSocketAddress(inetSocketAddress, stringBuilder);
        appendDetailProxy(proxy, stringBuilder);
        appendDetailProtocol(protocol, stringBuilder);
        appendDetailIOException(ioe, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param inetSocketAddress
     * @param proxy
     */
    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" connectStart ");
        appendProgressTime("connect", stringBuilder);
        appendDetailInetSocketAddress(inetSocketAddress, stringBuilder);
        appendDetailProxy(proxy, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param connection
     */
    @Override
    public void connectionAcquired(Call call, Connection connection) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" connectionAcquired ");
        appendProgressTime("connection", stringBuilder);
        appendDetailConnection(connection, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param connection
     */
    @Override
    public void connectionReleased(Call call, Connection connection) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" connectionReleased ");
        appendProgressTime("connection", stringBuilder);
        appendDetailConnection(connection, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param domainName
     * @param inetAddressList
     */
    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" dnsEnd ");
        appendProgressTime("dns", stringBuilder);
        appendDetailDns(domainName, inetAddressList, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param domainName
     */
    @Override
    public void dnsStart(Call call, String domainName) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" dnsStart ");
        appendProgressTime("dns", stringBuilder);
        appendDetailDns(domainName, null, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param url
     * @param proxies
     */
    @Override
    public void proxySelectEnd(Call call, HttpUrl url, List<Proxy> proxies) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" proxySelectEnd ");
        appendProgressTime("proxySelect", stringBuilder);
        stringBuilder.append(" url{");
        stringBuilder.append(url);
        stringBuilder.append("} ");
        stringBuilder.append(" proxies{");
        stringBuilder.append(proxies);
        stringBuilder.append("} ");
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param url
     */
    @Override
    public void proxySelectStart(Call call, HttpUrl url) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" proxySelectStart ");
        appendProgressTime("proxySelect", stringBuilder);
        stringBuilder.append(" url{");
        stringBuilder.append(url);
        stringBuilder.append("} ");
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param byteCount
     */
    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" requestBodyEnd ");
        appendProgressTime("requestBody", stringBuilder);
        appendProgressTime("request", stringBuilder);
        stringBuilder.append(" byteCount{");
        stringBuilder.append(byteCount);
        stringBuilder.append("} ");
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void requestBodyStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" requestBodyStart ");
        appendProgressTime("requestBody", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param ioe
     */
    @Override
    public void requestFailed(Call call, IOException ioe) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" requestFailed ");
        appendProgressTime("request", stringBuilder);
        appendDetailIOException(ioe, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param request
     */
    @Override
    public void requestHeadersEnd(Call call, Request request) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" requestHeadersEnd ");
        appendProgressTime("requestHeaders", stringBuilder);
        appendDetailRequestHeaders(request, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void requestHeadersStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" requestHeadersStart ");
        appendProgressTime("request", stringBuilder);
        appendProgressTime("requestHeaders", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param byteCount
     */
    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" responseBodyEnd ");
        appendProgressTime("responseBody", stringBuilder);
        appendProgressTime("response", stringBuilder);
        stringBuilder.append(" byteCount{");
        stringBuilder.append(byteCount);
        stringBuilder.append("} ");
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void responseBodyStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" responseBodyStart ");
        appendProgressTime("responseBody", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param ioe
     */
    @Override
    public void responseFailed(Call call, IOException ioe) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" responseFailed ");
        appendProgressTime("response", stringBuilder);
        appendDetailIOException(ioe, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param response
     */
    @Override
    public void responseHeadersEnd(Call call, Response response) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" responseHeadersEnd ");
        appendProgressTime("responseHeaders", stringBuilder);
        appendDetailResponseHeaders(response, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void responseHeadersStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" responseHeadersStart ");
        appendProgressTime("response", stringBuilder);
        appendProgressTime("responseHeaders", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     * @param handshake
     */
    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" secureConnectEnd ");
        appendProgressTime("secureConnect", stringBuilder);
        appendDetailHandshake(handshake, stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    /**
     * @param call
     */
    @Override
    public void secureConnectStart(Call call) {
        if (!isEnabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" secureConnectStart ");
        appendProgressTime("secureConnect", stringBuilder);
        appendDetailForCall(call, stringBuilder);
        profiler.accept(stringBuilder);
    }

    protected void appendProgressTime(String progressName, StringBuilder stringBuilder) {
        Long startTime = this.progressStartTime.get(progressName);
        if (startTime == null) {
            startTime = System.currentTimeMillis();
            this.progressStartTime.put(progressName, startTime);
            stringBuilder.append(progressName);
            stringBuilder.append(" start time: ");
            stringBuilder.append(startTime);
            stringBuilder.append(ProFileTimeUnit);
            stringBuilder.append(" ");
        } else {
            long currentTime = System.currentTimeMillis();
            long costTime = currentTime - startTime;
            this.progressStartTime.remove(progressName, startTime);
            stringBuilder.append(progressName);
            stringBuilder.append(" cost time: ");
            stringBuilder.append(costTime);
            stringBuilder.append(ProFileTimeUnit);
            stringBuilder.append(" end time: ");
            stringBuilder.append(currentTime);
            stringBuilder.append(ProFileTimeUnit);
            stringBuilder.append(" ");
        }
    }

    protected void appendDetailForCall(Call call, StringBuilder stringBuilder) {
        appendDetailForCall(call, stringBuilder, false);
    }
    protected void appendDetailForCall(Call call, StringBuilder stringBuilder, boolean needDetail) {
        stringBuilder.append("call {");
        if (needDetail) {
            stringBuilder.append("url:");
            stringBuilder.append(call.request().url());
            stringBuilder.append(" method:");
            stringBuilder.append(call.request().method());
            stringBuilder.append(" ");
        }
        stringBuilder.append("hash:");
        stringBuilder.append(System.identityHashCode(call));
        stringBuilder.append("} ");
    }

    protected void appendDetailInetSocketAddress(InetSocketAddress inetSocketAddress, StringBuilder stringBuilder) {
        stringBuilder.append(" InetSocketAddress {");
        stringBuilder.append(inetSocketAddress);
        stringBuilder.append("} ");
    }

    protected void appendDetailProxy(Proxy proxy, StringBuilder stringBuilder) {
        stringBuilder.append(" Proxy {type:");
        stringBuilder.append(proxy.type());
        stringBuilder.append(" address:");
        stringBuilder.append(proxy.address());
        stringBuilder.append("} ");
    }

    protected void appendDetailProtocol(Protocol protocol, StringBuilder stringBuilder) {
        stringBuilder.append(" Protocol{");
        stringBuilder.append(protocol);
        stringBuilder.append("} ");
    }

    protected void appendDetailConnection(Connection connection, StringBuilder stringBuilder) {
        stringBuilder.append(" ");
        stringBuilder.append(connection);
        stringBuilder.append(" ");
    }

    protected void appendDetailDns(String domainName, List<InetAddress> inetAddressList, StringBuilder stringBuilder) {
        stringBuilder.append(" domainName:");
        stringBuilder.append(domainName);
        stringBuilder.append(" inetAddressList{");
        stringBuilder.append(inetAddressList);
        stringBuilder.append("} ");
    }

    protected void appendDetailRequestHeaders(Request request, StringBuilder stringBuilder) {
        stringBuilder.append(" requestHeaders{");
        stringBuilder.append(request.headers());
        stringBuilder.append("} ");
    }

    protected void appendDetailResponseHeaders(Response response, StringBuilder stringBuilder) {
        stringBuilder.append(" responseHeaders{");
        stringBuilder.append(response.headers());
        stringBuilder.append("} ");
    }

    protected void appendDetailHandshake(Handshake handshake, StringBuilder stringBuilder) {
        stringBuilder.append(" handshake{");
        stringBuilder.append(handshake);
        stringBuilder.append("} ");
    }

    protected void appendDetailIOException(IOException ioe, StringBuilder stringBuilder) {
        try (StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ioe.printStackTrace(printWriter);
            stringBuilder.append(stringWriter);
        } catch (IOException ignore) {
            stringBuilder.append("appendDetailIOException failed");
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
