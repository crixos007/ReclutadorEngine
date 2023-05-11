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
import com.vsm.dto.OutHashtagPubDto;
import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndHashtagPubService;
import com.vsm.util.Utility;

@Service("HashtagPubPandoraService")
public class PndHashtagPubImpl implements PndHashtagPubService {
	private static final Logger LOG = LogManager.getLogger(PndHashtagPubImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraHashtagPublicacionDto[] hashtags;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutHashtagPubDto save(HuPandoraHashtagPublicacionDto in) {
		OutHashtagPubDto out = new OutHashtagPubDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getIdPublicacion()==null||in.getIdPublicacion().longValue()==0)return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PPUBLICACION"));
		if(in.getNombre()==null||in.getNombre().isBlank())return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
		if(in.getStatus()==null||in.getStatus().isBlank())return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSERT_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, in, String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertHstg.equalsIgnoreCase("save")) {
			out.setError(true);
			if(insertHstg.contains("HUMAN.FK_ID_PUBLICACION")) {
				out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
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
	public OutHashtagPubDto update(HuPandoraHashtagPublicacionDto in) {
		OutHashtagPubDto out = new OutHashtagPubDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(in.getIdHashtag()==0)return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID HASHTAG"));
		if(in.getIdPublicacion()==null || in.getIdPublicacion().longValue()==0)return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));
		HuPandoraHashtagPublicacionDto consulta = new HuPandoraHashtagPublicacionDto();
		consulta.setIdHashtag(in.getIdHashtag());
		
		/**
		 * VALIDA QUE EL HASHTAG EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, consulta,  HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSERT_SERVICE;
		String updtPerfil = "";

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraHashtagPublicacionDto, HuPandoraHashtagPublicacionDto> propertyMap = modelMapper.createTypeMap(HuPandoraHashtagPublicacionDto.class, HuPandoraHashtagPublicacionDto.class);
		propertyMap.setProvider(p -> hashtags[0]);			
		HuPandoraHashtagPublicacionDto hashtagNew =  modelMapper.map(in, HuPandoraHashtagPublicacionDto.class);		
		try {
			updtPerfil = template.postForObject(servicio, hashtagNew, String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!updtPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			if(updtPerfil.contains("HUMAN.FK_ID_PUBLICACION")) {
				out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(updtPerfil);
				return out;
			}
		}
		
		out.setError(false);
		out.setHashPub(hashtagNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutHashtagPubDto baja(HuPandoraHashtagPublicacionDto in) {
		OutHashtagPubDto out = new OutHashtagPubDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(in.getIdHashtag()==0)return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID HASHTAG"));
		if(in.getIdPublicacion()==null || in.getIdPublicacion().longValue()==0)return new OutHashtagPubDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));
		HuPandoraHashtagPublicacionDto consulta = new HuPandoraHashtagPublicacionDto();
		consulta.setIdHashtag(in.getIdHashtag());
		
		/**
		 * VALIDA QUE EL HASHTAG EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, consulta,  HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSERT_SERVICE;
		String updtPerfil = "";

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraHashtagPublicacionDto, HuPandoraHashtagPublicacionDto> propertyMap = modelMapper.createTypeMap(HuPandoraHashtagPublicacionDto.class, HuPandoraHashtagPublicacionDto.class);
		propertyMap.setProvider(p -> hashtags[0]);			
		HuPandoraHashtagPublicacionDto hashtagNew =  modelMapper.map(in, HuPandoraHashtagPublicacionDto.class);
		hashtagNew.setStatus("A");
		try {
			updtPerfil = template.postForObject(servicio, hashtagNew, String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!updtPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			if(updtPerfil.contains("HUMAN.FK_ID_PUBLICACION")) {
				out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(updtPerfil);
				return out;
			}
		}
		
		out.setError(false);
		out.setHashPub(hashtagNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutHashtagPubDto listHashTag(HuPandoraHashtagPublicacionDto in) {
		OutHashtagPubDto out = new OutHashtagPubDto();
		List<HuPandoraHashtagPublicacionDto> hashtag = new ArrayList<HuPandoraHashtagPublicacionDto>();
			
		/**
		 * RECUPERA EL HASHTAG DE BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, in, HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("hashtagPandoraImpl listhashtag error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_MSJ);
			return out;
		}	
		hashtag = Arrays.asList(hashtags);
		
		out.setError(false);
		out.setHashtagPub(hashtag);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutHashtagPubDto listAllHashTag() {
		OutHashtagPubDto out = new OutHashtagPubDto();
		List<HuPandoraHashtagPublicacionDto> hashtag = new ArrayList<HuPandoraHashtagPublicacionDto>();
		
		/**
		 * RECUPERA EL HASHTAG DE BD
		 */
		String servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELALL_SERVICE;
		try {
			hashtags = template.getForObject(servicio, HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("hashtagPandoraImpl listAllhashtag error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(hashtags==null || hashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_MSJ);
			return out;
		}	
		hashtag = Arrays.asList(hashtags);
		
		out.setError(false);
		out.setHashtagPub(hashtag);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
}