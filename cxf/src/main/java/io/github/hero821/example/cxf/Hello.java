package io.github.hero821.example.cxf;

import lombok.Builder;
import lombok.Data;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface Hello {
    /**
     * WebParam必填，否则显示arg0
     * WebResult必填，否则显示return
     */
    @WebResult(name = "response")
    Response sayHello(@WebParam(name = "request") Request request);

    @Data
    class Request {
        private String name;
    }

    @Data
    @Builder
    class Response {
        private String message;
    }
}