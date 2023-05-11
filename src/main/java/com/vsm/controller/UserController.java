package com.vsm.controller;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.OutTokenDto;
import com.vsm.service.UserService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Autenticacion")
/**
 * @Api(tags = "Autenticacion")
 * @author rcaraveo
 *
 */
public class UserController {
	private static final Logger LOG = LogManager.getLogger(UserController.class.getName());
	
	@Autowired
	UserService userService;

	
	/**
	 * @ApiOperation(value = "Servicio para obtener el token")
	 * @return String
	 */
	@GetMapping("VersionDes")
	public String getDesVersion() {
		LOG.info("getDesVersion para Aplicativo Pandora en desarrollo: " + ServiciosConstante.VERSION_DEV);
		return "getDesVersion para Aplicativo Pandora: " + ServiciosConstante.VERSION_DEV;
	}
	
	/**
	 * @ApiOperation(value = "Servicio para obtener el token")
	 * @param username
	 * @param cia
	 * @return OutTokenDto
	 */
	@PostMapping("User")
	public ResponseEntity<OutTokenDto> loginByUsr(@RequestParam("user") long user, @RequestParam("cia") long cia) {
		Date date = new Date();		
    	String idTraza = (int)(Math.random()*30+1) + date.getTime() + "_" + user + cia + "_PAN ";	
		OutTokenDto out = new OutTokenDto();
		
		//LOGICA DEL SERVICIO
		try {	
			try {
				out = userService.getJWTToken(String.valueOf(user), String.valueOf(cia), "usr", idTraza);
			}catch (Exception e) {
				LOG.error(idTraza +" ERROR MSG: " + e.getLocalizedMessage());
				throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE);
			}
			return new ResponseEntity<OutTokenDto>(out,HttpStatus.OK);
		}
		catch(HttpStatusCodeException exception) {
			LOG.error(idTraza +" ERROR MSG: " + exception.getLocalizedMessage());
			HttpHeaders headers=new HttpHeaders();
			headers.add("error", MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			headers.add("errorDesc", exception.getResponseBodyAsString());			
			out.setCodigo(MensajesConstante.ERROR_GENERAL);	
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			out.setError(true);
			return new ResponseEntity<OutTokenDto>(out,headers,exception.getStatusCode());
		}		
	}
	
	/**
	 * @ApiOperation(value = "Servicio para obtener el token")
	 * @param username
	 * @param cia
	 * @return OutTokenDto
	 */
	@PostMapping("Acceso")
	public ResponseEntity<OutTokenDto> loginByMail(@RequestParam("email") String mail, @RequestParam("password") String password) {		
		Date date = new Date();		
    	String idTraza = (int)(Math.random()*30+1) + date.getTime() + "_PAN "  + mail;	
		OutTokenDto out = new OutTokenDto();
		
		//LOGICA DEL SERVICIO
		try {	
			try {
				out = userService.getJWTToken(mail, password, "mail", idTraza);
			}catch (Exception e) {
				LOG.error(idTraza +" ERROR MSG: " + e.getLocalizedMessage());
				throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE);
			}
			return new ResponseEntity<OutTokenDto>(out,HttpStatus.OK);
		}
		catch(HttpStatusCodeException exception) {
			LOG.error(idTraza +" ERROR MSG: " + exception.getLocalizedMessage());
			HttpHeaders headers=new HttpHeaders();
			headers.add("error", MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			headers.add("errorDesc", exception.getResponseBodyAsString());			
			out.setCodigo(MensajesConstante.ERROR_GENERAL);	
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			out.setError(true);
			return new ResponseEntity<OutTokenDto>(out,headers,exception.getStatusCode());
		}	
	}
}