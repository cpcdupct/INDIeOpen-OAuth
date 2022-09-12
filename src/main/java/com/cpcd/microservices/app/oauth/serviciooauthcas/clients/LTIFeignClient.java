package com.cpcd.microservices.app.oauth.serviciooauthcas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="${feign.microserviciolti}")
public interface LTIFeignClient {

    @GetMapping("/access/{keykey}/{idunidad}/{origin}")
    public Boolean acceptUserLTI(@PathVariable String keykey, @PathVariable String idunidad, @PathVariable String origin);


}
