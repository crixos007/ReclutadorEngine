package com.vsm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.OutHashtagDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndHashtagService;
import com.vsm.util.Utility;

@Service("HashtagPandoraService")
public class PndHashtagImpl implements PndHashtagService {
	private static final Logger LOG = LogManager.getLogger(PndHashtagImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraHashtagDto[] hashtags;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutHashtagDto save(HuPandoraHashtagDto huPandoraHashtag) {
		OutHashtagDto out = new OutHashtagDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(huPandoraHashtag.getIdPerfilPuesto()==0)return new OutHashtagDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL PUESTO"));
		if(huPandoraHashtag.getNombre()==null||huPandoraHashtag.getNombre().isBlank())return new OutHashtagDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
		if(huPandoraHashtag.getStatus()==null||huPandoraHashtag.getStatus().isBlank())return new OutHashtagDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_INSERT_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, huPandoraHashtag, String.class);
		}catch (Exception e) {
			LOG.error("HashtagPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertHstg.equalsIgnoreCase("save")) {
			out.setError(true);
			if(insertHstg.contains("HUMAN.FK_ID_PERFIL_PUESTO")) {
				out.setCodigo(MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(insertHstg);
				return out;
			}
		}
		
		out.setError(false);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutHashtagDto update(HuPandoraHashtagDto huPandoraHashtag) {
		OutHashtagDto out = new OutHashtagDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(huPandoraHashtag.getIdHashtag()==null||huPandoraHashtag.getIdHashtag().longValue()==0)return new OutHashtagDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID HASHTAG"));
		if(huPandoraHashtag.getIdPerfilPuesto()==0)return new OutHashtagDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL PUESTO"));
		HuPandoraHashtagDto consulta = new HuPandoraHashtagDto();
		consulta.setIdHashtag(huPandoraHashtag.getIdHashtag());
		
		/**
		 * VALIDA QUE EL Hashtag EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, consulta,  HuPandoraHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("HashtagPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		servicio = ServiciosConstante.PAN_HASHTAG_INSERT_SERVICE;
		String updtPerfil = "";

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraHashtagDto, HuPandoraHashtagDto> propertyMap = modelMapper.createTypeMap(HuPandoraHashtagDto.class, HuPandoraHashtagDto.class);
		propertyMap.setProvider(p -> hashtags[0]);			
		HuPandoraHashtagDto hashtagNew =  modelMapper.map(huPandoraHashtag, HuPandoraHashtagDto.class);		
		try {
			updtPerfil = template.postForObject(servicio, hashtagNew, String.class);
		}catch (Exception e) {
			LOG.error("HashtagPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!updtPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			if(updtPerfil.contains("HUMAN.FK_ID_PERFIL_PUESTO")) {
				out.setCodigo(MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(updtPerfil);
				return out;
			}
		}
		
		out.setError(false);
		out.setHashtag(hashtagNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutHashtagDto listHashTag(HuPandoraHashtagDto huPandoraHashtag) {
		OutHashtagDto out = new OutHashtagDto();
		List<HuPandoraHashtagDto> hashtag = new ArrayList<HuPandoraHashtagDto>();
			
		/**
		 * RECUPERA EL HASHTAG DE BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, huPandoraHashtag, HuPandoraHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("hashtagPandoraImpl listhashtag error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_MSJ);
			return out;
		}	
		hashtag = Arrays.asList(hashtags);
		
		out.setError(false);
		out.setHashtags(hashtag);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutHashtagDto listAllHashTag() {
		OutHashtagDto out = new OutHashtagDto();
		List<HuPandoraHashtagDto> hashtag = new ArrayList<HuPandoraHashtagDto>();
		
		/**
		 * RECUPERA EL HASHTAG DE BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_SELALL_SERVICE;
		try {
			hashtags = template.getForObject(servicio, HuPandoraHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("hashtagPandoraImpl listAllhashtag error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_MSJ);
			return out;
		}	
		hashtag = Arrays.asList(hashtags);
		
		out.setError(false);
		out.setHashtags(hashtag);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
}