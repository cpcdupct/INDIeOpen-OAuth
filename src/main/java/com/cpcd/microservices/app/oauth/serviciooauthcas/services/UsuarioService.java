package com.cpcd.microservices.app.oauth.serviciooauthcas.services;

import com.cpcd.microservices.app.oauth.serviciooauthcas.clients.LTIFeignClient;
import com.cpcd.microservices.app.oauth.serviciooauthcas.clients.UsuarioFeignClient;
import com.cpcd.microservices.app.oauth.serviciooauthcas.model.UserInfo;
import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {
    private Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioFeignClient client;

    @Autowired
    private LTIFeignClient ltiFeignClientclient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            if (username.contains("|LTI|")){
                String[] lticaden = username.split("\\|");

                byte[] decodedBytes = Base64.getDecoder().decode(lticaden[12]);
                lticaden[12] = new String(decodedBytes);

                if (ltiFeignClientclient.acceptUserLTI(lticaden[12],lticaden[6],lticaden[11])){
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_LTI_USER"));
                    String refreshcadentext = "";
                    switch (lticaden.length) {
                        case 16:
                            refreshcadentext = username.replaceFirst("([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)", "REFRESHLTI|$2|$3|$4|$5|$6|$7|$8|$9|$10|$11|$13|$14|$15");
                            break;
                        case 14:
                            refreshcadentext = username.replaceFirst("([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)", "REFRESHLTI|$2|$3|$4|$5|$6|$7|$8|$9|$10|$11|$13");
                            break;
                        default:
                            refreshcadentext = username.replaceFirst("([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)", "REFRESHLTI|$2|$3|$4|$5|$6|$7|$8|$9|$10|$11");
                    }
                    return new UserInfo(refreshcadentext, passwordEncoder.encode("MY_KEY"), true, true,
                            true, true, authorities, username);
                }else{
                    throw new UsernameNotFoundException("Error en el login LTI, no existe el usuario '" + username + "'en el sistema");
                }
            }else if (username.contains("|REFRESHLTI|")){
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_LTI_USER"));
                return new UserInfo(username, passwordEncoder.encode("MY_KEY"), true, true,
                            true, true, authorities, username);
            }else{
                Usuario usuario = client.findByEmail(username);
                List<GrantedAuthority> authorities = usuario.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                        .peek(authority -> log.info("Role " + authority.getAuthority()))
                        .collect(Collectors.toList());


                return new User(usuario.getEmail(), usuario.getPassword(), usuario.getEnabled(), true,
                        true, true, authorities);
            }


        }catch (FeignException e){
            throw new UsernameNotFoundException("Error en el login, no existe el usuario '" + username + "'en el sistema");
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        return client.findByEmail(email);
    }

    @Override
    public Usuario update(Usuario usuario, String id) {
        return  client.update(usuario, id);
    }


}
