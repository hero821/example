package io.github.hero821.example.cxf;

import javax.jws.WebService;

/*
 *
 */
@WebService(serviceName = "HelloService", portName = "HelloPort")
public class HelloImpl implements Hello {
    public HelloResponse sayHello(HelloRequest request) {
        return HelloResponse.builder().message("hello " + request.getName()).build();
    }
}
