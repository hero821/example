package io.github.hero821.example.vertx;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author li.qiang
 */
@SuppressWarnings({"AlibabaRemoveCommentedCode", "CommentedOutCode", "AlibabaAvoidCommentBehindStatement"})
public class AppK8sProxy01 {
    private static final Logger logger = LoggerFactory.getLogger(AppK8sProxy01.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClient proxyClient = vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true));
//        HttpClient proxyClient = vertx.createHttpClient();
        HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
        proxy.origin(6443, "192.168.3.201");
        proxy.addInterceptor(new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                context.request().putHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkhHWlB4emNzWHJDVHZfTWl6R2JXbmQ4UTc5SzRYSFdYSWVUTWhGaGRYVGsifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi10b2tlbi1qYm1icCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJhZG1pbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImVmNGMwNDkxLWM1N2MtNDUzNi05ZmU4LWZkZmRjNjU3NzMwNiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTphZG1pbiJ9.QkZsMXBMBMOOq50NB1SXRFsD69HQPDNBnQsaR7o7d6JnqXP6SeFUJG1XLG6JtFfFqcRhf5Ohjup23QKa-ppgCSQIeu6mdC6FVm_WLPs-DtGvml6C-x0J0j22pdtbgoY2ecGQdTcPJmPl6veGrQfZ5NCDFlDYKSjUqzx51qcYpT7r7neaz7bf3d_2kjUJf18H6j3e-LCBp_F18SGeq8zjVRlGyrEB45OtYvqDJPthUIBCeDQHtJTyBFs0k26V_fz1lzuKl76ofMxJwREF1GkCWGq_UvC61koyO1hkIk2sPPnHLERfn48cO2wVz7vICxUhFLAdbOtpyjMH845wmITe6Q");
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
}
