package com.cpcd.microservices.app.oauth.serviciooauthcas.services;

import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;

public interface IUsuarioService {
    public Usuario findByEmail(String email);

    public Usuario update(Usuario usuario, String id);

}
