package com.vsm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.OutTokenDto;
import com.vsm.lib.dto.human.HuCatToGralDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioDto;
import com.vsm.service.UserService;
import com.vsm.util.Utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service("UserService")
public class UserImpl implements UserService {
	private static final Logger LOG = LogManager.getLogger(UserImpl.class.getName());
	
	@Autowired
	RestTemplate template;
		
	@Autowired
	Utility utilService;

	@Override
	public OutTokenDto getJWTToken(String user, String cia, String getBy, String idTraza) throws Exception {
		try {
			OutTokenDto out = new OutTokenDto();
			HuPandoraUsuarioDto usuario = null;
			
			if(getBy.equalsIgnoreCase("usr")) {
				String servicio = ServiciosConstante.PAN_USR_BYUSR_SELECT_SERVICE + "numCia=" + cia + "&numUsr=" + user;
				usuario = template.getForObject(servicio, HuPandoraUsuarioDto.class);							
				if(usuario==null) {
					LOG.error(idTraza + "getJWTToken error trace: sin coincidencia en DB para el usuario: " + user);
					out.setError(true);
					out.setCodigo(MensajesConstante.ERROR_USR_NO_DATA_CODE);
					out.setMessage(MensajesConstante.ERROR_USR_NO_DATA_MSJ.replace("{usuario}", user));
					return out;
				}
			}
			
			if(getBy.equalsIgnoreCase("mail")) {
				String servicio = ServiciosConstante.PAN_USR_BYMAIL_SELECT_SERVICE + "email=" + user;			
				usuario = template.getForObject(servicio, HuPandoraUsuarioDto.class);			
				if(usuario==null) {
					LOG.error(idTraza + "getJWTToken error trace: sin coincidencia en DB para el usuario: " + user);
					out.setError(true);
					out.setCodigo(MensajesConstante.ERROR_USR_NO_DATA_CODE);
					out.setMessage(MensajesConstante.ERROR_USR_NO_DATA_MSJ.replace("{usuario}", user));
					return out;
				}
				String encodePass = "";			
				encodePass = utilService.encriptar(cia);
				if(!usuario.getPassword().equals(encodePass)) {
					LOG.error(idTraza + "getJWTToken error trace: password distinto del capturado en DB para el usuario: " + user);
					out.setError(true);
					out.setCodigo(MensajesConstante.ERROR_PASSWORD_CODE);
					out.setMessage(MensajesConstante.ERROR_PASSWORD_MSJ.replace("{usuario}", user));
					return out;
				}
			}
					
			long tokenExp = 0; 
			String servicio = ServiciosConstante.PARAMETRO_HUMAN_SERVICE;
			HuCatToGralDto[] catToGralResp = template.getForObject(servicio, HuCatToGralDto[].class, "PAN_TOKEN_EXPIRATION");		
			List<HuCatToGralDto> catToGralList = new ArrayList<HuCatToGralDto>();
			catToGralList = Arrays.asList(catToGralResp);
			for (int i = 0; i < catToGralList.size(); i++) {
				HuCatToGralDto e = catToGralList.get(i);
				tokenExp = e.getValorNumerico()!=null?e.getValorNumerico().longValue():5000;
			}		
			String secretKey = "Pandora";
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");		
			String token = Jwts.builder().setId("venturssotfJWT")
										 .setSubject(usuario.getNombre() + " " + usuario.getApellidoPat() + " " + usuario.getApellidoMat())
										 .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
										 .setIssuedAt(new Date(System.currentTimeMillis()))
										 .setExpiration(new Date(System.currentTimeMillis() + tokenExp))
										 .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
										 .compact();
			out.setError(false);
			out.setUsuario(usuario);
			out.setToken("Bearer " + token);		
			out.setCodigo(MensajesConstante.SUCCES_CODE);
			out.setMessage(MensajesConstante.SUCCES_MSJ);
			return out;
		}catch (Exception e) {
			throw new Exception(this.getClass().getName() + " : " + e.getLocalizedMessage());
		}
	}
}