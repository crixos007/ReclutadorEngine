package com.vsm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

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
import com.vsm.dto.InPublicacionDto;
import com.vsm.dto.InWsPandoraDto;
import com.vsm.dto.OutPublicacionDto;
import com.vsm.dto.OutWsPandoraDto;
import com.vsm.lib.dto.human.HuPandoraCandidatoDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;
import com.vsm.lib.dto.human.HuPandoraPublicacionDto;
import com.vsm.lib.dto.human.HuPndPerfilPubDto;
import com.vsm.lib.dto.human.HuPndPubHashtagDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndPublicacionService;
import com.vsm.util.Utility;

@Service("publicacionPandoraService")
public class PndPublicacionImpl implements PndPublicacionService {
	private static final Logger LOG = LogManager.getLogger(PndPublicacionImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraPublicacionDto[] publicaciones;
	HuPandoraHashtagDto[] hashtags;
	HuPndPubHashtagDto[] pubhashtags;
	HuPandoraHashtagPublicacionDto[] hashtagsPub;
	HuPndPerfilPubDto[] perfilPubs;
	HuPandoraCandidatoDto[] candidatosResp;
	HuPandoraPublicacionDto savePubResp;
	
	@Autowired
	RestTemplate template;
	@Autowired
	RestTemplate templatePnd;

	@Autowired
	Utility utilService;
	
	@Override
	public OutPublicacionDto save(HuPandoraPublicacionDto in, long numCia) {
		OutPublicacionDto out = new OutPublicacionDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getNombrePubilcacion()==null||in.getNombrePubilcacion().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE PUBLICACION"));		
		if(in.getIdPerfilPuesto()==null||in.getIdPerfilPuesto().longValue()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL PUESTO"));
		if(in.getNumVacantes()==null||in.getNumVacantes().longValue()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NUMERO VACANTES"));
		if(in.getStatus()==null||in.getStatus().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));

		/**
		 * PERSISTE LA PUBLICACION EN HU_PANDORA_PUBLICACION
		 */
		in.setFechaMov(LocalDateTime.now());
		String servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
		try {
			savePubResp = template.postForObject(servicio, in, HuPandoraPublicacionDto.class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl save error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(savePubResp == null || savePubResp.getIdPublicacion()==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;		
		}
		
		/**
		 * RECUPERA EL HASHTAG DE BD POR CADA REGISTRO DE PUBLICACION
		 */
		List<HuPandoraHashtagPublicacionDto> publicaiones = new ArrayList<HuPandoraHashtagPublicacionDto>();
		HuPandoraHashtagDto huPandoraHashtag = new HuPandoraHashtagDto();
		huPandoraHashtag.setIdPerfilPuesto(savePubResp.getIdPerfilPuesto().longValue());
		servicio = ServiciosConstante.PAN_HASHTAG_SELECT_SERVICE;
		try {
			hashtags = template.postForObject(servicio, huPandoraHashtag, HuPandoraHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("hashtagPandoraImpl listhashtag error tace: " + e.getMessage());
		}				
		if(hashtags==null || hashtags.length==0) {	
			LOG.info(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_CODE);
		}else {	
			for(HuPandoraHashtagDto x : hashtags) {
				HuPandoraHashtagPublicacionDto y = new HuPandoraHashtagPublicacionDto();
				y.setIdPublicacion(new BigDecimal(savePubResp.getIdPublicacion()));
				y.setNombre(x.getNombre());
				y.setStatus("A");
				publicaiones.add(y);				
			}
		}	
		
		/**
		 * PERSISTE EL HASHTAG EN HU_PANDORA_HASHTAG
		 */
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSALL_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, publicaiones, String.class);
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
		
		/**
		 * ENVIA EL PERFIL DEL PUESTO A LOS SERVICIOS WEB DE PANDORA
		 */
		templatePnd.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
		Formatter fmt = new Formatter();
		List<String> hashtagsPub = new ArrayList<String>();
		for(HuPandoraHashtagPublicacionDto x : publicaiones) {
			hashtagsPub.add(x.getNombre());
		}
		if(hashtagsPub.isEmpty())hashtagsPub.add("#"+in.getNombrePubilcacion());
		InWsPandoraDto inAddPos = new InWsPandoraDto();
		inAddPos.setDescripcion(in.getNombrePubilcacion());
		inAddPos.setHashtag(hashtagsPub);
		inAddPos.setIdPosicion(savePubResp.getIdPublicacion() + "_" + fmt.format("%04d", numCia));
		servicio = ServiciosConstante.PANDORA_WS_ADDPOSICION_SERVICE;
		try {
			OutWsPandoraDto wsAddPosResp = templatePnd.postForObject(servicio, inAddPos, OutWsPandoraDto.class);
			fmt.close();
			LOG.info("PndHashtagPubImpl save servicio pandora trace: " + servicio + " param: " + inAddPos.toString());
			if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
				LOG.error("PndHashtagPubImpl save error code: " + wsAddPosResp.getCodigo());
				return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
			}
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error code: " + e.getMessage());
			return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
		}		
		
		out.setError(false);
		out.setPub(savePubResp);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPublicacionDto update(HuPandoraPublicacionDto in) {
		OutPublicacionDto out = new OutPublicacionDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(in.getIdPublicacion()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));
		HuPandoraPublicacionDto consulta = new HuPandoraPublicacionDto();
		consulta.setIdPublicacion(in.getIdPublicacion());
		
		/**
		 * VALIDA QUE LA PUBLICACION EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_SELECT_SERVICE;
		try {
			publicaciones = template.postForObject(servicio, consulta,  HuPandoraPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(publicaciones==null || publicaciones.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE LA PUBLICACION EN HU_PANDORA_PUBLICACION
		 */
		servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraPublicacionDto, HuPandoraPublicacionDto> propertyMap = modelMapper.createTypeMap(HuPandoraPublicacionDto.class, HuPandoraPublicacionDto.class);
		propertyMap.setProvider(p -> publicaciones[0]);			
		HuPandoraPublicacionDto pubNew =  modelMapper.map(in, HuPandoraPublicacionDto.class);	
		pubNew.setFechaMov(LocalDateTime.now());
		try {
			savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(savePubResp==null) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		
		out.setError(false);
		out.setPub(pubNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutPublicacionDto baja(HuPandoraPublicacionDto in) {
		OutPublicacionDto out = new OutPublicacionDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(in.getIdPublicacion()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));
		HuPandoraPublicacionDto consulta = new HuPandoraPublicacionDto();
		consulta.setIdPublicacion(in.getIdPublicacion());
		
		/**
		 * VALIDA QUE LA PUBLICACION EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_SELECT_SERVICE;
		try {
			publicaciones = template.postForObject(servicio, consulta,  HuPandoraPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(publicaciones==null || publicaciones.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO
		 */
		servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraPublicacionDto, HuPandoraPublicacionDto> propertyMap = modelMapper.createTypeMap(HuPandoraPublicacionDto.class, HuPandoraPublicacionDto.class);
		propertyMap.setProvider(p -> publicaciones[0]);			
		HuPandoraPublicacionDto pubNew =  modelMapper.map(in, HuPandoraPublicacionDto.class);	
		pubNew.setStatus("E");
		pubNew.setFechaMov(LocalDateTime.now());
		try {
			savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(savePubResp==null) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		
		/**
		 * ELIMINA LOS HASHTAGS ACTUALES DE LA PUBLICACION
		 */
		HuPandoraHashtagPublicacionDto getHpo = new HuPandoraHashtagPublicacionDto();
		getHpo.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELECT_SERVICE;
		try {
			hashtagsPub = template.postForObject(servicio, in, HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}
		if(hashtagsPub==null || hashtagsPub.length==0) {
			LOG.info("PndPublicacionImpl update error tace: " + MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
		}else {
			List<HuPandoraHashtagPublicacionDto> hashtagP = Arrays.asList(hashtagsPub);
			servicio = ServiciosConstante.PAN_HASHTAG_PUB_DELALL_SERVICE;
			String saveHashPub = "";
			try {
				saveHashPub = template.postForObject(servicio, hashtagP, String.class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
			if(!saveHashPub.equalsIgnoreCase("save")) {
				LOG.error("PndPublicacionImpl update error tace: " + saveHashPub);
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
		}
		
		/**
		 * LIBERA LOS CANDIDATOS ACTUALES DE LA PUBLICACION
		 */
		servicio = ServiciosConstante.PAN_CANDIDATOS_SELECT_SERVICE;
		HuPandoraCandidatoDto getCandidato = new HuPandoraCandidatoDto();
		getCandidato.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
		try {
			candidatosResp = template.postForObject(servicio, getCandidato, HuPandoraCandidatoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl update error tace: " + e.getMessage());
			return new OutPublicacionDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(candidatosResp==null || candidatosResp.length==0) {
			LOG.error("PndCandidatoImpl update error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
		}else {
			List<HuPandoraCandidatoDto> candidatosUpdt = Arrays.asList(candidatosResp);
			for(HuPandoraCandidatoDto x : candidatosUpdt) {
				x.setIdPublicacion(null);
			}
			//SE PERSISTE EL CANDIDATO
			servicio = ServiciosConstante.PAN_CANDIDATOS_INSALL_SERVICE;
			String insCandidato = "";
			try {
				insCandidato = template.postForObject(servicio, candidatosUpdt, String.class);
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl update error tace: " + e.getMessage());
				return new OutPublicacionDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			}
			if(!insCandidato.equalsIgnoreCase("save")) {
				LOG.error("PndCandidatoImpl update error tace: " + insCandidato);
				return new OutPublicacionDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			}
		}
		
		out.setError(false);
		out.setPub(pubNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutPublicacionDto listPublicacion(HuPndPubHashtagDto in) {
		OutPublicacionDto out = new OutPublicacionDto();
		List<HuPndPubHashtagDto> publicacion = new ArrayList<HuPndPubHashtagDto>();
			
		/**
		 * RECUPERA LA PUBLICACION DE BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_HASHTAG_SELECT_SERVICE;
		try {
			pubhashtags = template.postForObject(servicio, in, HuPndPubHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl listpublicacion error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(pubhashtags==null || pubhashtags.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			return out;
		}	
		publicacion = Arrays.asList(pubhashtags);
		
		out.setError(false);
		out.setPubHash(publicacion);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPublicacionDto listAllPublicacion(boolean byStatus) {
		OutPublicacionDto out = new OutPublicacionDto();
		List<HuPndPubHashtagDto> publicacion = new ArrayList<HuPndPubHashtagDto>();
		
		/**
		 * RECUPERA LA PUBLICACION DE BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_HASHTAG_SELALL_SERVICE;
		try {
			pubhashtags = template.getForObject(servicio, HuPndPubHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl listAllpublicacion error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(pubhashtags==null || pubhashtags.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			return out;
		}	
		publicacion = Arrays.asList(pubhashtags);
		
		/**
		 * RECUPERA EL HASHTAG DE BD POR CADA REGISTRO DE PUBLICACION
		 */
		for(HuPndPubHashtagDto z : publicacion) {
			HuPandoraHashtagDto huPandoraHashtag = new HuPandoraHashtagDto();
			huPandoraHashtag.setIdPerfilPuesto(z.getIdPerfilPuesto().longValue());
			servicio = ServiciosConstante.PAN_HASHTAG_SELECT_SERVICE;
			try {
				hashtags = template.postForObject(servicio, huPandoraHashtag, HuPandoraHashtagDto[].class);
			}catch (Exception e) {
				LOG.error("hashtagPandoraImpl listhashtag error tace: " + e.getMessage());
			}				
			if(hashtags==null || hashtags.length==0) {	
				LOG.info(MensajesConstante.ERROR_HASHTAG_NO_EXISTE_CODE);
			}else {	
				for(HuPandoraHashtagDto x : hashtags) {
					HuPandoraHashtagPublicacionDto y = new HuPandoraHashtagPublicacionDto();
					y.setIdHashtag(x.getIdHashtag().longValue());
					y.setIdPublicacion(new BigDecimal(z.getIdPublicacion()));
					y.setNombre(x.getNombre());
					y.setStatus(x.getStatus());
					z.getHuPandoraHashtagPublicacions().add(y);
				}
			}
		}
		
		if(!byStatus) {
			out.setError(false);
			out.setPubHash(publicacion);
			out.setCodigo(MensajesConstante.SUCCES_CODE);
			out.setMessage(MensajesConstante.SUCCES_MSJ);
			return out;
		}
		
		List<HuPndPubHashtagDto> pubActivas = publicacion.stream().filter(p->!p.getStatus().equalsIgnoreCase("E")).collect(Collectors.toList());
		List<HuPndPubHashtagDto> pubInactivas = publicacion.stream().filter(p->p.getStatus().equalsIgnoreCase("E")).collect(Collectors.toList());
		out.setError(false);
		out.setPubHash(pubActivas);
		out.setPubHashInactivas(pubInactivas);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out;
	}

	@Override
	public OutPublicacionDto save(InPublicacionDto huPandoraPubHashtag) {
		OutPublicacionDto out = new OutPublicacionDto();
		HuPandoraPublicacionDto in = huPandoraPubHashtag.getPublicacion();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getNombrePubilcacion()==null||in.getNombrePubilcacion().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE PUBLICACION"));		
		if(in.getIdPerfilPuesto()==null||in.getIdPerfilPuesto().longValue()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL PUESTO"));
		if(in.getNumVacantes()==null||in.getNumVacantes().longValue()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NUMERO VACANTES"));
		if(in.getStatus()==null||in.getStatus().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		for(HuPandoraHashtagPublicacionDto x : huPandoraPubHashtag.getPubHashtag()) {
			if(x.getNombre()==null||x.getNombre().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
			if(x.getStatus()==null||x.getStatus().isBlank())return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		}
		
		/**
		 * PERSISTE LA PUBLICACION EN HU_PANDORA_PUBLICACION
		 */
		in.setFechaMov(LocalDateTime.now());
		String servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
		try {
			savePubResp = template.postForObject(servicio, in, HuPandoraPublicacionDto.class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl save error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(savePubResp == null || savePubResp.getIdPublicacion()==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;		
		}			
		long idPublicacion = savePubResp.getIdPublicacion();
		
		/**
		 * PERSISTE EL HASHTAG EN HU_PANDORA_HASHTAG
		 */
		for(HuPandoraHashtagPublicacionDto x : huPandoraPubHashtag.getPubHashtag()) {
			x.setIdPublicacion(new BigDecimal(idPublicacion));
		}
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSALL_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, huPandoraPubHashtag.getPubHashtag(), String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error tace: " + e.getMessage());
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
		
		/**
		 * ENVIA EL PERFIL DEL PUESTO A LOS SERVICIOS WEB DE PANDORA
		 */
		templatePnd.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
		Formatter fmt = new Formatter();		
		List<String> hashtagsPub = new ArrayList<String>();
		for(HuPandoraHashtagPublicacionDto x : huPandoraPubHashtag.getPubHashtag()) {
			hashtagsPub.add(x.getNombre());
		}
		if(hashtagsPub.isEmpty())hashtagsPub.add("#"+in.getNombrePubilcacion());
		InWsPandoraDto inAddPos = new InWsPandoraDto();
		inAddPos.setDescripcion(in.getNombrePubilcacion());
		inAddPos.setHashtag(hashtagsPub);
		inAddPos.setIdPosicion(savePubResp.getIdPublicacion() + "_" + fmt.format("%04d", huPandoraPubHashtag.getNumCia()));
		servicio = ServiciosConstante.PANDORA_WS_ADDPOSICION_SERVICE;
		try {
			OutWsPandoraDto wsAddPosResp = templatePnd.postForObject(servicio, inAddPos, OutWsPandoraDto.class);
			fmt.close();
			LOG.info("PndHashtagPubImpl save servicio pandora trace: " + servicio + " param: " + inAddPos.toString());
			if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
				LOG.error("PndHashtagPubImpl save error code: " + wsAddPosResp.getCodigo());
				return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
			}
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error code: " + e.getMessage());
			return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
		}		
		
		out.setError(false);
		out.setPub(savePubResp);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out;
	}

	@Override
	public OutPublicacionDto update(InPublicacionDto huPandoraPubHashtag) {
		OutPublicacionDto out = new OutPublicacionDto();
		HuPandoraPublicacionDto in = huPandoraPubHashtag.getPublicacion();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(in.getIdPublicacion()==0)return new OutPublicacionDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));
		HuPandoraPublicacionDto consulta = new HuPandoraPublicacionDto();
		consulta.setIdPublicacion(in.getIdPublicacion());
		
		/**
		 * VALIDA QUE LA PUBLICACION EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_SELECT_SERVICE;
		try {
			publicaciones = template.postForObject(servicio, consulta,  HuPandoraPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(publicaciones==null || publicaciones.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE LA PUBLICACION EN HU_PANDORA_PUBLICAION
		 */
		servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraPublicacionDto, HuPandoraPublicacionDto> propertyMap = modelMapper.createTypeMap(HuPandoraPublicacionDto.class, HuPandoraPublicacionDto.class);
		propertyMap.setProvider(p -> publicaciones[0]);			
		HuPandoraPublicacionDto pubNew =  modelMapper.map(in, HuPandoraPublicacionDto.class);
		pubNew.setFechaMov(LocalDateTime.now());
		pubNew.setAnalizando("M");
		try {
			savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(savePubResp==null) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		
		/**
		 * ELIMINA LOS HASHTAGS DE PUBLICACIONES ANTERIORES Y PERSISTE LOS ACTUALES DE LA PUBLICACION
		 */
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_SELECT_SERVICE;
		try {
			hashtagsPub = template.postForObject(servicio, in, HuPandoraHashtagPublicacionDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		
		//ELIMINA LOS HASHTAGS ACTUALES
		if(hashtagsPub==null || hashtagsPub.length==0) {
			LOG.info("PndPublicacionImpl update error tace: " + MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
		}else {
			List<HuPandoraHashtagPublicacionDto> hashtagP = Arrays.asList(hashtagsPub);
			servicio = ServiciosConstante.PAN_HASHTAG_PUB_DELALL_SERVICE;
			String saveHashPub = "";
			try {
				saveHashPub = template.postForObject(servicio, hashtagP, String.class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
			if(!saveHashPub.equalsIgnoreCase("save")) {
				LOG.error("PndPublicacionImpl update error tace: " + saveHashPub);
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
		}
		
		//PERSISTE LOS HASTAG NUEVOS
		for(HuPandoraHashtagPublicacionDto x : huPandoraPubHashtag.getPubHashtag()) {
			x.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
		}
		servicio = ServiciosConstante.PAN_HASHTAG_PUB_INSALL_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, huPandoraPubHashtag.getPubHashtag(), String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error tace: " + e.getMessage());
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
		
		/**
		 * ENVIA EL PERFIL DEL PUESTO A LOS SERVICIOS WEB DE PANDORA
		 */
		templatePnd.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
		Formatter fmt = new Formatter();
		List<String> hashtagsPub = new ArrayList<String>();
		for(HuPandoraHashtagPublicacionDto x : huPandoraPubHashtag.getPubHashtag()) {
			hashtagsPub.add(x.getNombre());
		}
		if(hashtagsPub.isEmpty())hashtagsPub.add("#"+in.getNombrePubilcacion());
		InWsPandoraDto inAddPos = new InWsPandoraDto();
		inAddPos.setDescripcion(savePubResp.getNombrePubilcacion());
		inAddPos.setHashtag(hashtagsPub);
		inAddPos.setIdPosicion(savePubResp.getIdPublicacion() + "_" + fmt.format("%04d", huPandoraPubHashtag.getNumCia()));
		servicio = ServiciosConstante.PANDORA_WS_ADDPOSICION_SERVICE;
		try {
			OutWsPandoraDto wsAddPosResp = templatePnd.postForObject(servicio, inAddPos, OutWsPandoraDto.class);
			fmt.close();
			LOG.info("PndHashtagPubImpl save servicio pandora trace: " + servicio + " param: " + inAddPos.toString());
			if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
				LOG.error("PndHashtagPubImpl save error code: " + wsAddPosResp.getCodigo());
				return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
			}
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl save error code: " + e.getMessage());
			return new OutPublicacionDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddIdPosicion"));
		}
		
		out.setError(false);
		out.setPub(pubNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPublicacionDto depuraPublicacion(long numCia) {
		HuPandoraPerfilPuestoDto in = new HuPandoraPerfilPuestoDto();
		in.setNumCia(new BigDecimal(numCia));
		
		/**
		 * RECUPERA LA PUBLICACION DE BD
		 */
		String servicio = ServiciosConstante.PAN_PERFIL_PUB_SELECT_SERVICE;
		try {
			perfilPubs = template.postForObject(servicio, in,HuPndPerfilPubDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl depuraPublicacion error tace: " + e.getMessage());
			return new OutPublicacionDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(perfilPubs==null || perfilPubs.length==0) {
			return new OutPublicacionDto(false, MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE, MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
		}	
		List<HuPndPerfilPubDto> perfilList = Arrays.asList(perfilPubs);
		List<HuPndPerfilPubDto> perfilListTemp = perfilList.stream().filter(p->p.getHuPandoraPublicacions() != null && 
																			   p.getHuPandoraPublicacions().size()>0).collect(Collectors.toList());
		if(perfilListTemp==null || perfilListTemp.size()==0) {
			return new OutPublicacionDto(false, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
		}
		
		/**
		 * SE CREAN LOS ARREGLOS DE LAS PUBLICACIONES A CAMBIAR ESTATUS
		 */
		LocalDateTime today = LocalDateTime.now();
		boolean upd = false;
		List<HuPandoraPublicacionDto> updatePubList = new ArrayList<HuPandoraPublicacionDto>(); 
		for(HuPndPerfilPubDto x : perfilListTemp) {
			for(HuPandoraPublicacionDto y : x.getHuPandoraPublicacions()) {
				if(y.getStatus().equalsIgnoreCase("C") && today.isAfter(y.getFechaMov().plusDays(5))) {
					y.setStatus("N");
					y.setFechaMov(LocalDateTime.now());
					upd = true;
				}
				else if(y.getStatus().equalsIgnoreCase("A") && today.isAfter(y.getFechaMov().plusDays(2))) {
					y.setStatus("S");
					y.setFechaMov(LocalDateTime.now());
					upd = true;
				}
				if(upd)updatePubList.add(y);
				upd=false;
			}
		}
		
		//PERSISTE LAS PUBLICACIONES CON LOS NUEVOS STATUS
		servicio = ServiciosConstante.PAN_PUBLICACION_INSALL_SERVICE;
		String updtPub = "";
		try {
			updtPub = template.postForObject(servicio, updatePubList, String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPubImpl depuraPublicacion error tace: " + e.getMessage());
			return new OutPublicacionDto(false, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}
		if(!updtPub.equalsIgnoreCase("save")) {
			return new OutPublicacionDto(false, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}			
		return new OutPublicacionDto(false, MensajesConstante.SUCCES_CODE, MensajesConstante.SUCCES_MSJ);
	}
}