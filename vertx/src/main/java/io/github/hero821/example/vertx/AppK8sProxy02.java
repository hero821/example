package io.github.hero821.example.vertx;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author li.qiang
 */
@SuppressWarnings({"AlibabaRemoveCommentedCode", "CommentedOutCode", "AlibabaAvoidCommentBehindStatement"})
public class AppK8sProxy02 {
    private static final Logger logger = LoggerFactory.getLogger(AppK8sProxy02.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
//        HttpClient proxyClient = vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true));
        HttpClient proxyClient = vertx.createHttpClient();
        HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
        proxy.origin(30034, "188.104.23.148");
        proxy.addInterceptor(new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                Map<String, String> config = getConfig(context);
                context.request().putHeader("X-Auth-Token", config.get("token"));
                context.request().setURI(config.get("uri"));
                return context.sendRequest();
            }

            @Override
            public Future<Void> handleProxyResponse(ProxyContext context) {
                return context.sendResponse();
            }
        });
//        SelfSignedCertificate certificate = SelfSignedCertificate.create();
//        HttpServer proxyServer = vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyCertOptions(certificate.keyCertOptions()).setTrustOptions(certificate.trustOptions()));
        HttpServer proxyServer = vertx.createHttpServer();
        proxyServer.requestHandler(proxy).listen(8080);
    }

    private static Map<String, String> getConfig(ProxyContext context) {
        return ImmutableMap.<String, String>builder()
                .put("uri", "/api/v1/cmp/tenants/e9202d68de954782920b9c69d3499041/vdcs/100000078179/k8s/clusters/22" + context.request().getURI())
                .put("token", "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJwbGF0Zm9ybUNvZGUiOiJQVDIwMjEwOTAyMTAzNDI2NDExNSIsImFjY291bnQiOiJlNTU5MDAxMDIiLCJwYXNzd29yZCI6IlBhc3M5OTk5IiwicGhvbmUiOiIxMzc1NzEwNTU5OSIsInBlcm1pc3Npb25Qb2xpY3kiOiJTUEVDSUZZIiwiaWF0IjoxNjUwMTIwMzYzLCJleHAiOjE2NTAxMjM5NjN9.8eQdHLynjC4M1fU3oCJJpaDU28P5cEKW-Wi_sjAE-2I")
                .build();
    }
}
