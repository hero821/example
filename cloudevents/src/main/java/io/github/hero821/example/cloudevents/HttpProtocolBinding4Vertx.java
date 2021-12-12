package io.github.hero821.example.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.cloudevents.jackson.JsonFormat;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.net.URI;
import java.util.UUID;

/**
 * @author li.qiang
 */
public class HttpProtocolBinding4Vertx {
    static class Server {
        public static void main(String[] args) {
            Vertx.vertx().createHttpServer()
                    .exceptionHandler(System.err::println)
                    .requestHandler(request -> {
                        VertxMessageFactory.createReader(request)
                                .map(MessageReader::toEvent)
                                .onSuccess(event -> {
                                    System.out.println(event);
                                    VertxMessageFactory.createWriter(request.response()).writeBinary(event);
                                })
                                .onFailure(System.err::println);
                    })
                    .listen(8080, server -> {
                        if (server.succeeded()) {
                            System.out.println("Server listening on port: " + server.result().actualPort());
                        } else {
                            System.err.println(server.cause().getMessage());
                        }
                    });
        }
    }

    static class Client {
        private static final int NUM_EVENTS = 4;

        public static void main(String[] args) {
            final WebClient webClient = WebClient.create(Vertx.vertx());

            CloudEventBuilder template = CloudEventBuilder.v1()
                    .withSource(URI.create("http://localhost"))
                    .withType("example.vertx");

            for (int i = 0; i < NUM_EVENTS; i++) {
                String data = "event number " + i;
                final CloudEvent event = template.newBuilder().withId(UUID.randomUUID().toString()).withData("text/plain", data.getBytes()).build();
                Future<HttpResponse<Buffer>> response;
                if (i % 2 == 0) {
                    response = VertxMessageFactory.createWriter(webClient.postAbs("http://localhost:8080")).writeBinary(event);
                } else {
                    response = VertxMessageFactory.createWriter(webClient.postAbs("http://localhost:8080")).writeStructured(event, JsonFormat.CONTENT_TYPE);
                }
                response.map(VertxMessageFactory::createReader).map(MessageReader::toEvent).onSuccess(System.out::println).onFailure(System.err::println);
            }
        }
    }
}
