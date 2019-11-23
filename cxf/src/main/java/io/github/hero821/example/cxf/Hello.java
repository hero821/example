package io.github.hero821.example.cxf;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface Hello {
    /*
     * WebParam必填，否则显示arg0
     * WebResult必填，否则显示return
     */
    @WebResult(name = "response")
    HelloResponse sayHello(@WebParam(name = "request") HelloRequest request);
}
