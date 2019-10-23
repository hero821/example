package io.github.hero821.example.cxf;

public class HelloPortImpl implements Hello {
    public Response sayHello(Request request) {
        return Response.builder().message("hello " + request.getName()).build();
    }
}