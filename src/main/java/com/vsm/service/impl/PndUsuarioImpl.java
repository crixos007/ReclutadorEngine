package com.vsm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.Email;
import com.vsm.dto.OutUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioINSDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioPKDto;
import com.vsm.lib.dto.wrapper.ConsultaArchivoResponseDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndUsuarioService;
import com.vsm.util.Utility;

@Service("usrPandoraService")
public class PndUsuarioImpl implements PndUsuarioService {
	private static final Logger LOG = LogManager.getLogger(PndUsuarioImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraUsuarioDto usuario;
	ConsultaArchivoResponseDto respMail;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutUsuarioDto save(HuPandoraUsuarioINSDto huPandoraUsr) {
		OutUsuarioDto out = new OutUsuarioDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(huPandoraUsr.getApellidoMat()==null||huPandoraUsr.getApellidoMat().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "AP MATERNO"));
		if(huPandoraUsr.getApellidoPat()==null||huPandoraUsr.getApellidoPat().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "AP PATERNO"));
		if(huPandoraUsr.getDireccion()==null||huPandoraUsr.getDireccion().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "DIRECCION"));
		if(huPandoraUsr.getNombre()==null||huPandoraUsr.getNombre().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
		if(huPandoraUsr.getPassword()==null||huPandoraUsr.getPassword().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "PASSWORD"));
		if(huPandoraUsr.getRfc()==null||huPandoraUsr.getRfc().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "RFC"));
		if(huPandoraUsr.getTelefono()==null||huPandoraUsr.getTelefono().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "TELEFONO"));
		if(huPandoraUsr.getId().getEmail()==null||huPandoraUsr.getId().getEmail().isBlank())return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "EMAIL"));
		if(huPandoraUsr.getId().getIdIdioma()==0)return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID IDIOMA"));
		if(huPandoraUsr.getId().getNumCia()==0)return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID CIA"));
		if(!utils.isEmailValid(huPandoraUsr.getId().getEmail()))return new OutUsuarioDto(true,MensajesConstante.ERROR_DATO_INCORRECTO_CODE,MensajesConstante.ERROR_DATO_INCORRECTO_MSJ.replace("{nombreCampo}", "EMAIL"));
	
		/**
		 * ENCRIPTA EL PASSWORD
		 */
		try {
			huPandoraUsr.setPassword(utilService.encriptar(huPandoraUsr.getPassword()));       
		} catch (Exception e) {
			LOG.error("UsuarioPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_GENERAL);
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			return out;
		}
		
		/**
		 * VALIDA QUE EL EMAIL NO EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_USR_BYMAIL_SELECT_SERVICE + "email=" + huPandoraUsr.getId().getEmail();
		try {
			usuario = template.getForObject(servicio, HuPandoraUsuarioDto.class);
		}catch (Exception e) {
			LOG.error("UsuarioPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(usuario!=null && !usuario.getNombre().isBlank()) {
			out.setError(true);
			out.setUsuario(usuario);
			out.setCodigo(MensajesConstante.ERROR_USER_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_USER_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE EL USUARIO EN HU_PANDORA_USUARIO
		 */
		servicio = ServiciosConstante.PAN_USR_INSERT_SERVICE;
		String insertUsr = "";
		try {
			insertUsr = template.postForObject(servicio, huPandoraUsr, String.class);
		}catch (Exception e) {
			LOG.error("UsuarioPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertUsr.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(insertUsr);
			return out;
		}
		
		out.setError(false);
		out.setUsuario(usuario);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutUsuarioDto update(HuPandoraUsuarioDto huPandoraUsr) {
		OutUsuarioDto out = new OutUsuarioDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(huPandoraUsr.getId().getIdUsuario()==0)return new OutUsuarioDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID USUARIO"));
		if(huPandoraUsr.getId().getEmail()!=null && !huPandoraUsr.getId().getEmail().isBlank()) {
			if(!utils.isEmailValid(huPandoraUsr.getId().getEmail()))return new OutUsuarioDto(true,MensajesConstante.ERROR_DATO_INCORRECTO_CODE,MensajesConstante.ERROR_DATO_INCORRECTO_MSJ.replace("{nombreCampo}", "EMAIL"));
		}
		if(huPandoraUsr.getPassword()!=null && !huPandoraUsr.getPassword().isBlank()) {
			try {
				huPandoraUsr.setPassword(utilService.encriptar(huPandoraUsr.getPassword()));       
			} catch (Exception e) {
				LOG.error("UsuarioPandoraImpl update error tace: " + e.getStackTrace());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_GENERAL);
				out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
				return out;
			}
		}
		
		/**
		 * VALIDA QUE EL USUARIO EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_USR_BYUSR_SELECT_SERVICE + "numUsr=" + huPandoraUsr.getId().getIdUsuario();
		try {
			usuario = template.getForObject(servicio, HuPandoraUsuarioDto.class);
		}catch (Exception e) {
			LOG.error("UsuarioPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(usuario==null || usuario.getNombre().isBlank()) {
			out.setError(true);
			out.setUsuario(usuario);
			out.setCodigo(MensajesConstante.ERROR_USER_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_USER_NO_EXISTE_MSJ);
			return out;
		}
		if(huPandoraUsr.getApellidoMat()==null || huPandoraUsr.getApellidoMat().isBlank())huPandoraUsr.setApellidoMat(usuario.getApellidoMat());
		if(huPandoraUsr.getApellidoPat()==null || huPandoraUsr.getApellidoPat().isBlank())huPandoraUsr.setApellidoPat(usuario.getApellidoPat());
		if(huPandoraUsr.getNombre()==null || huPandoraUsr.getNombre().isBlank())huPandoraUsr.setNombre(usuario.getNombre());
		
		/**
		 * PERSISTE EL USUARIO EN HU_PANDORA_USUARIO
		 */
		servicio = ServiciosConstante.PAN_USR_UPDATE_SERVICE;
		String updtUsr = "";
		HuPandoraUsuarioPKDto pkNew = usuario.getId();
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraUsuarioDto, HuPandoraUsuarioDto> propertyMap = modelMapper.createTypeMap(HuPandoraUsuarioDto.class, HuPandoraUsuarioDto.class);
		propertyMap.setProvider(p -> usuario);			
		HuPandoraUsuarioDto usuarioNew =  modelMapper.map(huPandoraUsr, HuPandoraUsuarioDto.class);	
		
		//VALIDACION DE NULOS/BLANCK
		if(usuarioNew.getId().getEmail()==null||usuarioNew.getId().getEmail().isBlank())usuarioNew.getId().setEmail(pkNew.getEmail());
		if(usuarioNew.getId().getIdIdioma()==0)usuarioNew.getId().setIdIdioma(pkNew.getIdIdioma());
		if(usuarioNew.getId().getNumCia()==0)usuarioNew.getId().setNumCia(pkNew.getNumCia());		
		
		try {
			updtUsr = template.postForObject(servicio, usuarioNew, String.class);
		}catch (Exception e) {
			LOG.error("UsuarioPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!updtUsr.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(updtUsr);
			return out;
		}
		
		out.setError(false);
		out.setUsuario(usuario);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}	
	
	@Override
	public OutUsuarioDto getPassword(HuPandoraUsuarioDto huPandoraUsr) {
		OutUsuarioDto out = new OutUsuarioDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(huPandoraUsr.getId().getEmail()!=null && !huPandoraUsr.getId().getEmail().isBlank()) {
			if(!utils.isEmailValid(huPandoraUsr.getId().getEmail()))return new OutUsuarioDto(true,MensajesConstante.ERROR_DATO_INCORRECTO_CODE,MensajesConstante.ERROR_DATO_INCORRECTO_MSJ.replace("{nombreCampo}", "EMAIL"));
		}
		
		/**
		 * VALIDA QUE EL USUARIO EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_USR_BYMAIL_SELECT_SERVICE + "numCia=" + huPandoraUsr.getId().getNumCia() + "&email=" + huPandoraUsr.getId().getEmail();
		try {
			usuario = template.getForObject(servicio, HuPandoraUsuarioDto.class);
		}catch (Exception e) {
			LOG.error("UsuarioPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(usuario==null || usuario.getNombre().isBlank()) {
			out.setError(true);
			out.setUsuario(usuario);
			out.setCodigo(MensajesConstante.ERROR_USER_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_USER_NO_EXISTE_MSJ);
			return out;
		}		
		
		//DESENCRIPTA EL USUARIO
		String password = "";
		try {
			password = utilService.desencriptar(usuario.getPassword());       
		} catch (Exception e) {
			LOG.error("UsuarioPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_GENERAL);
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			return out;
		}
		
		//ENVIA EL CORREO CON EL PASSWORD
		List<String> lstDestinatarios = new ArrayList<String>();
		lstDestinatarios.add(usuario.getId().getEmail());
		Email email = new Email();
		email.setAsunto("Recordatorio de password Pandora");
		email.setBodyHtml(true);
		String cueropHtml = utilService.getBodyRemenberPassHtml(usuario.getId().getEmail(), password);
		email.setCuerpoMail(cueropHtml);
		email.setDestinatarios(lstDestinatarios);
		servicio = ServiciosConstante.SEND_MAIL_VSM_SERVICE;            
        template.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
        try {
        	respMail = template.postForObject(servicio ,email, ConsultaArchivoResponseDto.class);
        }catch (Exception e) {
			LOG.error("LoginImpl notificaCvsPandora errortrace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_GENERAL);
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			return out;
		}
				
		out.setError(false);
		out.setUsuario(usuario);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}	
}