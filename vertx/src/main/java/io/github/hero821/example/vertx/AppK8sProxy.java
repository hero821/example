package io.github.hero821.example.vertx;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author li.qiang
 */
@SuppressWarnings({"AlibabaConstantFieldShouldBeUpperCase", "CommentedOutCode", "AlibabaRemoveCommentedCode", "unused"})
public class AppK8sProxy {
    private static final Logger logger = LoggerFactory.getLogger(AppK8sProxy.class);
    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKVCIsImV4cCI6MTY2OTgyNDAwMH0.IwV2EA1oOYqDygZQzrdLll5Zh8cMF7VxqvBrjSQKvSw";
    private static final Vertx vertx = Vertx.vertx();
    private static final Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(30, TimeUnit.MINUTES).build();
    private static final WebClient client = WebClient.create(vertx);
    private static final HttpServer server = vertx.createHttpServer();
    // private static final SelfSignedCertificate certificate = SelfSignedCertificate.create();
    // private static final HttpServer server = vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyCertOptions(certificate.keyCertOptions()).setTrustOptions(certificate.trustOptions()));

    public static void main(String[] args) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ctx -> getConfig(ctx).onSuccess(config -> {
            HttpServerRequest request = ctx.request();
            logger.debug("请求地址：[{}], [{}]", request.method(), request.uri());
            String authorization = request.getHeader("Authorization");
//            if (Objects.nonNull(authorization) && !authorization.endsWith(token)) {
//                ctx.response().setStatusCode(403).end("没有权限");
//            }
            logger.debug("Authorization：[{}]", authorization);
            client.request(request.method(), new RequestOptions().setHost("188.104.23.148").setPort(30034).setURI(config.get("uri"))).putHeaders(request.headers()).putHeader("X-Auth-Token", config.get("token")).sendBuffer(ctx.getBody()).onComplete(event -> {
                ctx.response().setStatusCode(event.result().statusCode());
                ctx.response().setStatusMessage(event.result().statusMessage());
                ctx.response().headers().setAll(event.result().headers());
//                ctx.response().setChunked(true);
                logger.debug("响应结果 status：[{}], [{}]", event.result().statusCode(), event.result().statusMessage());
                logger.debug("响应结果 headers：[{}]", event.result().headers());
                logger.debug("响应结果 body：[{}]", event.result().bodyAsString());
                if (event.result().body() == null) {
                    ctx.response().end();
                } else {
                    ctx.response().end(event.result().body());
                }
            });
        }));
        server.requestHandler(router).listen(8080);
    }

    private static Future<Map<String, String>> getConfig(RoutingContext context) {
        Promise<Map<String, String>> promise = Promise.promise();
        // /api/v1/cmp/tenants/{tenantId}/vdcs/{vdcId}/k8s/clusters/{clusterId}/{k8sNativePath}
        String instance = null;
        // 创研院容器实例命名规则，抽取实例唯一标识
        String regex = ".*dl-(.*)-pod.*";
        // POST 都没有 name
        // PATCH、PUT 都有 name
        // DELETE 部分有 name，部分（Delete Collection）没有 name
        // GET 部分有 name，部分（List 和 List All）没有 name
        if (context.request().method().equals(HttpMethod.POST)) {
            String name = context.getBodyAsJson().getJsonObject("metadata").getString("name");
            if (name.matches(regex)) {
                instance = name.replaceFirst(regex, "$1");
                logger.info("识别到应用实例ID：[{}]", instance);
            } else {
                logger.debug("未识别到应用实例ID，使用默认集群");
            }
        } else {
            String uri = context.request().uri();
            if (uri.matches(regex)) {
                instance = uri.replaceFirst(regex, "$1");
                logger.info("识别到应用实例ID：[{}]", instance);
            } else {
                logger.debug("未识别到应用实例ID，使用默认集群");
            }
        }
        CompositeFuture.all(getTenant(), getVdc(), getCluster(instance), getToken()).onSuccess(event -> promise.complete(ImmutableMap.<String, String>builder().put("uri", "/api/v1/cmp/tenants/" + event.resultAt(0) + "/vdcs/" + event.resultAt(1) + "/k8s/clusters/" + event.resultAt(2) + context.request().uri()).put("token", event.resultAt(3)).build()));
        return promise.future();
    }

    private static Future<String> getTenant() {
        return Future.future(promise -> promise.complete("e9202d68de954782920b9c69d3499041"));
    }

    private static Future<String> getVdc() {
        return Future.future(promise -> promise.complete("100000078179"));
    }

    private static Future<String> getCluster(String instance) {
        return Future.future(promise -> {
            // 默认 cluster 为 26，对应容器集群 移动云
            String cluster = "26";
            if (Objects.nonNull(instance)) {
                // TODO 根据 实例 选 集群
                // 1、未部署的应用：通过大脑计算选择一个集群
                // 2、已部署的应用：根据实例ID找到对应集群
                logger.debug("TODO 根据 实例 选 集群");
            }
            promise.complete(cluster);
        });
    }

    private static Future<String> getToken() {
        return Future.future(promise -> {
            final String[] token = {cache.getIfPresent("token")};
            if (Objects.nonNull(token[0])) {
                promise.complete(token[0]);
            } else {
                logger.debug("获取新的 token");
                client.postAbs("http://188.104.23.148:30034/api/v1/cmp/authentication/token").sendJsonObject(new JsonObject().put("MGMTID", "PT202109021034264115").put("CMPID", "PT202108190959156954").put("username", "e55900102").put("password", "Pass9999")).onSuccess(event -> {
                    token[0] = event.bodyAsJsonObject().getString("token");
                    cache.put("token", token[0]);
                    promise.complete(token[0]);
                });
            }
        });
    }
}
