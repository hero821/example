package io.github.hero821.example.springboot.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author li.qiang
 */
@RestController
public class SampleGatewayController {
    private final RestTemplate restTemplate;

    public SampleGatewayController(@Lazy RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

    @RequestMapping("/mockbin.org/**")
    public ResponseEntity<JsonNode> proxy(RequestEntity<JsonNode> request) {
        assert request.getMethod() == null;
        assert request.getBody() == null;
        return restTemplate.exchange(
                "http://mockbin.org/" + new AntPathMatcher().extractPathWithinPattern("/mockbin.org/**", request.getUrl().getPath()),
                request.getMethod(),
                new HttpEntity<>(request.getBody()),
                JsonNode.class
        );
    }
}
