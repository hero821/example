package io.github.hero821.example.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author li.qiang
 */
public class AppHttpSfcSf {
    private static final Logger logger = LoggerFactory.getLogger(AppHttpSfcSf.class);

    static {
        System.setProperty("vertx.disableDnsResolver", "true");
    }

    public static void main(String[] args) {
        String index = args.length > 0 ? args[0] : "01";
        Vertx vertx = Vertx.vertx();
        AppHttpSfcSf.sf(vertx, index);
        AppHttpSfcSf.proxy(vertx, index);
    }

    public static void sf(Vertx vertx, String index) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post().handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            json.put("name", json.getString("name") + "-sf-" + index);
            ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(json));
        });
        server.requestHandler(router).listen(9090);
        logger.debug("args[index]=" + index);
        logger.debug("sf server started");
    }

    public static void proxy(Vertx vertx, String index) {
        WebClient client = WebClient.create(vertx);
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post().handler(ctx -> {
            logger.debug("sf" + index + "被请求");
            JsonObject json = ctx.getBodyAsJson();
            json.put("name", json.getString("name") + "-proxy-" + index);
            client.post(9090, "localhost", "/").sendJsonObject(json).onSuccess(res1 -> {
                JsonArray segList = new JsonArray(ctx.request().getHeader(AppHttpSfcGw.SEG_LIST));
                int segLeft = Integer.parseInt(ctx.request().getHeader(AppHttpSfcGw.SEG_LEFT));
                logger.debug("segList=" + segList + ",segLeft=" + segLeft);
                if (0 != segLeft) {
                    segLeft--;
                    client.post(8080, "[" + segList.getString(segLeft) + "]", "/")
                            .timeout(10000)
                            .putHeader(AppHttpSfcGw.SEG_LIST, segList.toString())
                            .putHeader(AppHttpSfcGw.SEG_LEFT, Integer.toString(segLeft))
                            .sendJsonObject(res1.bodyAsJsonObject())
                            .onSuccess(res2 -> ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(res2.bodyAsJsonObject())))
                            .onFailure(err -> {
                                logger.error(err.getMessage());
                                ctx.response().setStatusCode(500).end(err.getMessage());
                            });
                } else {
                    ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(res1.bodyAsJsonObject()));
                }
            });
        });
        server.requestHandler(router).listen(8080);
        logger.debug("args[index]=" + index);
        logger.debug("proxy server started");
    }
}
