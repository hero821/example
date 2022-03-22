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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author li.qiang
 */
public class AppHttpSfcGw {
    private static final Logger logger = LoggerFactory.getLogger(AppHttpSfcGw.class);
    public static final String SEG_LIST = "SegList";
    public static final String SEG_LEFT = "SegLeft";

    static {
        System.setProperty("vertx.disableDnsResolver", "true");
    }

    public static void main(String[] args) {
        String config = args.length > 0 ? args[0] : "{}";
        Vertx vertx = Vertx.vertx();
        AppHttpSfcGw.gw(vertx, config);
    }

    public static void gw(Vertx vertx, String config) {
        JsonObject map = new JsonObject(config);
        if (map.size() == 0) {
            logger.warn("config is empty");
            return;
        }
        for (Map.Entry<String, Object> entry : map) {
            String[] segArr = map.getString(entry.getKey()).split(",");
            Arrays.sort(segArr, Collections.reverseOrder());
            JsonArray segList = new JsonArray(Arrays.asList(segArr));
            WebClient client = WebClient.create(vertx);
            HttpServer server = vertx.createHttpServer();
            Router router = Router.router(vertx);
            router.route().handler(BodyHandler.create());
            router.post().handler(ctx -> {
                int segLeft = segList.size();
                if (0 != segLeft) {
                    segLeft--;
                    client.post(8080, "[" + segList.getString(segLeft) + "]", "/")
                            .timeout(10000)
                            .putHeader(AppHttpSfcGw.SEG_LIST, segList.toString())
                            .putHeader(AppHttpSfcGw.SEG_LEFT, Integer.toString(segLeft))
                            .sendJsonObject(ctx.getBodyAsJson())
                            .onSuccess(res -> ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(res.bodyAsJsonObject())))
                            .onFailure(err -> {
                                logger.error(err.getMessage());
                                ctx.response().setStatusCode(500).end(err.getMessage());
                            });
                } else {
                    ctx.response().setStatusCode(500).end("segList is empty");
                }
            });
            server.requestHandler(router).listen(Integer.parseInt(entry.getKey()));
            logger.debug("gw listen port [" + entry.getKey() + "] segs [" + entry.getValue() + "]");
        }
        logger.debug("args[config]=" + config);
        logger.debug("gw server started");
    }
}
