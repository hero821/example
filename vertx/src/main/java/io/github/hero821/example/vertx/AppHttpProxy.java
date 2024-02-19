package io.github.hero821.example.vertx;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author li.qiang
 */
public class AppHttpProxy {
    private static final Logger logger = LoggerFactory.getLogger(AppHttpProxy.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClient proxyClient = vertx.createHttpClient();
        HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
        proxy.originSelector(request -> {
            logger.info("解析参数，选择集群，获取 ip 和 port");
            return Future.succeededFuture(new SocketAddressImpl(80, "mockbin.org"));
        });
        proxy.addInterceptor(new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                logger.info("解析参数，选择集群，修改 header");
                context.request().putHeader("Host", "mockbin.org");
                logger.info("解析参数，选择集群，修改 url");
                context.request().setURI("/request" + context.request().getURI());
                return context.sendRequest();
            }

            @Override
            public Future<Void> handleProxyResponse(ProxyContext context) {
                logger.info("解析参数，选择集群，修改 header");
                context.response().putHeader("k1", "v1");
                return context.sendResponse();
            }
        });
        HttpServer proxyServer = vertx.createHttpServer();
        proxyServer.requestHandler(proxy).listen(8080);
    }
}
