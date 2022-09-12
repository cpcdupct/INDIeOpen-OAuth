package com.cpcd.microservices.app.oauth.serviciooauthcas.security.event;

import com.cpcd.microservices.app.oauth.serviciooauthcas.services.IUsuarioService;
import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessErrorHandle implements AuthenticationEventPublisher {

    private Logger log = LoggerFactory.getLogger(AuthenticationSuccessErrorHandle.class);

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        if (authentication.getDetails() instanceof WebAuthenticationDetails){
            return;
        }
        UserDetails user = (UserDetails) authentication.getPrincipal();

        if(authentication.getAuthorities().size() == 0){
            return; 
        }

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LTI_USER"))){
            Usuario usuario  = usuarioService.findByEmail(authentication.getName());
            if (usuario.getIntentos()!=null && usuario.getIntentos()>0){
                usuario.setIntentos(0);
                usuarioService.update(usuario, usuario.getId());
            }
        }

    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {
        String mensaje = "Error en el login: " + e.getMessage();
        log.error(mensaje);
        System.out.println(mensaje);

        try{
            if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LTI_USER"))){
                Usuario usuario  = usuarioService.findByEmail(authentication.getName());
                if (usuario.getIntentos()==null){
                    usuario.setIntentos(0);
                }
                usuario.setIntentos(usuario.getIntentos()+1);

                if (usuario.getIntentos()>=3){
                    log.error(String.format("El usuario %s deshabilitado por máximos intentos,", usuario.getEmail()));
                    usuario.setEnabled(false);
                }
                usuarioService.update(usuario, usuario.getId());
            }
        }catch(FeignException exception){
            log.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
        }

    }
}
