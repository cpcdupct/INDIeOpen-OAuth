package com.cpcd.microservices.app.oauth.serviciooauthcas.security;

import com.cpcd.microservices.app.oauth.serviciooauthcas.model.UserInfo;
import com.cpcd.microservices.app.oauth.serviciooauthcas.services.IUsuarioService;
import com.cpcd.microservices.app.oauth.serviciooauthcas.services.UsuarioService;
import com.cpcd.microservices.app.servicescommons.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoAdicionalToken implements TokenEnhancer {
	private Logger log = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
		Map<String, Object> info = new HashMap<String, Object>();

		if (oAuth2Authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LTI_USER"))){
			UserInfo userInfo = (UserInfo) oAuth2Authentication.getPrincipal();
			String[] lticaden = userInfo.getCadenLTI().split("\\|");

			info.put("nombre", lticaden[8]);
			info.put("apellido", lticaden[9]);
			info.put("correo", lticaden[5]);
			info.put("userid", lticaden[7]);
			info.put("dominio", lticaden[2]);
			info.put("curso", lticaden[3]);
			info.put("actividad", lticaden[4]);
			info.put("titulo", lticaden[10]);
			info.put("idunidad", lticaden[6]);
			switch (lticaden.length) {
				case 16:
					info.put("urlunidad", lticaden[14]);
					info.put("authortitle", lticaden[15]);
				case 14:
					info.put("profile", lticaden[13]);
					break;
			}
			if (lticaden.length == 14) {
				info.put("profile", lticaden[13]);
			}
			((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
		}else{
			Usuario usuario = usuarioService.findByEmail(oAuth2Authentication.getName());
			info.put("nombre", usuario.getNombre());
			info.put("apellido", usuario.getApellido());
			info.put("correo", usuario.getEmail());
			info.put("id", usuario.getId());
			info.put("avatar", usuario.getAvatar());
			((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
		}
		return oAuth2AccessToken;
	}
}
