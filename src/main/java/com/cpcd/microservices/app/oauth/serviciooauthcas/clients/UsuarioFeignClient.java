package com.cpcd.microservices.app.oauth.serviciooauthcas.clients;

import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="${feign.microserviciousuarios}")
public interface UsuarioFeignClient {


    @GetMapping("/email/{email}")
    public Usuario findByEmail(@RequestParam String email);

    @PutMapping("/{id}")
    public Usuario update(@RequestBody Usuario usuario, @PathVariable String id);
}
