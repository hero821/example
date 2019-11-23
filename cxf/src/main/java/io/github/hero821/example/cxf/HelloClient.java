package io.github.hero821.example.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloClient {

    private static final Logger LOG = LoggerFactory.getLogger(HelloClient.class);

    static {
        // 使用系统代理
        // System.setProperty("java.net.useSystemProxies", "true");
    }

    public static void main(String[] args) throws Exception {
        // 调用方法1
        JaxWsProxyFactoryBean factory1 = new JaxWsProxyFactoryBean();
        factory1.setServiceClass(Hello.class);
        factory1.setAddress("http://localhost:8080/Service/Hello");
        Hello hello = (Hello) factory1.create();
        HelloRequest request = HelloRequest.builder().name("liqiang").build();
        HelloResponse response = hello.sayHello(request);
        LOG.info(response.getMessage());
        // 调用方法2
        JaxWsDynamicClientFactory factory2 = JaxWsDynamicClientFactory.newInstance();
        Client client = factory2.createClient("http://localhost:8080/Service/Hello?wsdl");
        Object[] responses = client.invoke("sayHello", request);
        LOG.info(((HelloResponse) responses[0]).getMessage());
        LOG.info("THE END");
    }
}
