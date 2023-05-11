package com.vsm.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.CurriculumsResp;
import com.vsm.dto.Email;
import com.vsm.dto.InCandidatoDto;
import com.vsm.dto.InWsPandoraDto;
import com.vsm.dto.OutCandidatoDto;
import com.vsm.dto.OutWsPandoraDto;
import com.vsm.lib.dto.human.HuPandoraCandidatoDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.lib.dto.human.HuPandoraNombresValidoDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;
import com.vsm.lib.dto.human.HuPandoraPublicacionDto;
import com.vsm.lib.dto.human.HuPndPubHashtagDto;
import com.vsm.lib.dto.wrapper.ConsultaArchivoResponseDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndCandidatoService;
import com.vsm.util.Utility;

@Service("CandidatoPandoraService")
public class PndCandidatoImpl implements PndCandidatoService {
	private static final Logger LOG = LogManager.getLogger(PndCandidatoImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraHashtagDto[] hashtags;
	HuPandoraNombresValidoDto[] nombresResp;
	HuPandoraCandidatoDto[] candidatosResp;
	com.vsm.dto.HuPandoraCandidatoDto[] candidatosResp2;
	HuPndPubHashtagDto[] pubhashtags;
	OutWsPandoraDto wsAddPosResp;
	HuPandoraPerfilPuestoDto[] perfiles;
	ConsultaArchivoResponseDto respMail;
	HuPandoraPublicacionDto savePubResp;
	com.vsm.dto.HuPandoraCandidatoDto candidatoResp;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;

	@Override
	public OutCandidatoDto getCv(long idPublicacion, long idImport) {
		OutCandidatoDto out = new OutCandidatoDto();
			
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */	
		com.vsm.dto.HuPandoraCandidatoDto in = new com.vsm.dto.HuPandoraCandidatoDto();		
		if(idPublicacion==0 && idImport>0)in.setIdImportacion(String.valueOf(idImport));
		if(idImport==0 && idPublicacion>0)in.setIdPublicacion(new BigDecimal(idPublicacion));

		/**
		 * RECUPERA LOS CV DE BD
		 */
		String servicio = ServiciosConstante.PAN_CANDIDATOS_NOCV_SELECT_SERVICE;
		try {
			candidatosResp2 = template.postForObject(servicio, in, com.vsm.dto.HuPandoraCandidatoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl getCv error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(candidatosResp2==null || candidatosResp2.length==0) {
			LOG.error("PndCandidatoImpl getCv error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		List<com.vsm.dto.HuPandoraCandidatoDto> candidatosList = Arrays.asList(candidatosResp2);
		
		out.setError(false);
		out.setCandidatoslist(candidatosList);
		out.setTotalCv(candidatosList.size());
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out;
	}

	@Override
	public OutCandidatoDto baja(long idCandidato, long idPublicacion, long numCia) {
		HuPandoraCandidatoDto in = new HuPandoraCandidatoDto();
		in.setIdCandidato(idCandidato);	
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */	
		if(idCandidato==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "CANDIDATO"));
		
		/**
		 * ELIMINA AL CANDIGATO DE BD
		 */
		String servicio = ServiciosConstante.PAN_CANDIDATOS_DELETE_SERVICE;
		String delServicio = "";
		try {
			delServicio = template.postForObject(servicio, in, String.class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl baja error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(!delServicio.equalsIgnoreCase("save")) {
			LOG.error("PndCandidatoImpl baja error tace: " + delServicio);
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		
		//ELIMINA CANDIDATO DE PANDORA
		Formatter fmt = new Formatter();
		String posicion = idPublicacion + "_" + fmt.format("%04d", numCia);      
		fmt.close();
		InWsPandoraDto inDelCv = new InWsPandoraDto();
		inDelCv.setIdPosicion(posicion);
		inDelCv.setIdCandidato(String.valueOf(idCandidato));		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", ServiciosConstante.GET_PANDORA_TOKEN);
		HttpEntity< InWsPandoraDto > entity = new HttpEntity<>(inDelCv, headers);		
		servicio = ServiciosConstante.PANDORA_WS_DELCVS_SERVICE;
		try {
			wsAddPosResp = template.postForObject(servicio, entity, OutWsPandoraDto.class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddCurriculum"));
		}
		if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
			LOG.error("PndCandidatoImpl analizaCvsPandora error code: " + wsAddPosResp.getCodigo());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddCurriculum"));
		}
		
		return new OutCandidatoDto(false, MensajesConstante.SUCCES_CODE, MensajesConstante.SUCCES_MSJ);
	}

	@Override
	public OutCandidatoDto selCandidatoPandora(InCandidatoDto in) {
		List<HuPandoraCandidatoDto> candidatosUpdt = new ArrayList<HuPandoraCandidatoDto>(); 
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getIdPublicacion()==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));		
		if(in.getCandidatos()==null||in.getCandidatos().isEmpty())return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "CANDIDATOS"));
						
		/**
		 * RECUPERA LOS CV DE BD
		 */
		for(Long z : in.getCandidatos()) {
			String servicio = ServiciosConstante.PAN_CANDIDATOS_SELECT_SERVICE;
			HuPandoraCandidatoDto serch = new HuPandoraCandidatoDto();
			serch.setIdCandidato(z);
			try {
				candidatosResp = template.postForObject(servicio, serch, HuPandoraCandidatoDto[].class);
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl selCandidatoPandora error tace: " + e.getMessage());
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			}				
			if(candidatosResp==null || candidatosResp.length==0) {
				LOG.error("PndCandidatoImpl selCandidatoPandora error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
				return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ + z);
			}
			List<HuPandoraCandidatoDto> candidatosList = Arrays.asList(candidatosResp);		
			candidatosUpdt.addAll(candidatosList);
		}
		
		if(candidatosUpdt==null || candidatosUpdt.isEmpty()) {
			LOG.error("PndCandidatoImpl selCandidatoPandora error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		
		//SE PERSISTE EL CANDIDATO
		for(HuPandoraCandidatoDto a : candidatosUpdt) {
			a.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
			a.setCvSeleccionado(in.isDeselecciona()?"N":"S");
		}
		String servicio = ServiciosConstante.PAN_CANDIDATOS_INSALL_SERVICE;
		String insCandidato = "";
		try {
			insCandidato = template.postForObject(servicio, candidatosUpdt, String.class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl selCandidatoPandora error tace: " + e.getMessage());
			if(e.getMessage().contains("HUMAN.FK_PUBLICACION")) {
				return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			}
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}
		if(!insCandidato.equalsIgnoreCase("save")) {
			LOG.error("PndCandidatoImpl selCandidatoPandora error tace: " + insCandidato);
			if(insCandidato.contains("HUMAN.FK_PUBLICACION")) {
				return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			}
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}
		return new OutCandidatoDto(false, MensajesConstante.SUCCES_CODE, MensajesConstante.SUCCES_MSJ);
	}

	@Override
	public OutCandidatoDto analizaCvsPandora(InCandidatoDto in) throws Exception {
		try {
			OutCandidatoDto out = new OutCandidatoDto();
			
			/**
			 * VALICAIONES DE LOS DATOS DE ENTRADA
			 */
			if(in.getIdPublicacion()==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));		
			//if(in.getCandidatos()==null||in.getCandidatos().isEmpty())return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "CANDIDATOS"));
			
			/**
			 * RECUPERA LA PUBLICACION DE BD
			 */
			String servicio = ServiciosConstante.PAN_PUBLICACION_HASHTAG_SELECT_SERVICE;
			HuPndPubHashtagDto inPub = new HuPndPubHashtagDto();
			inPub.setIdPublicacion(in.getIdPublicacion());
			try {
				pubhashtags = template.postForObject(servicio, inPub, HuPndPubHashtagDto[].class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl analizaCvsPandora error tace: " + e.getMessage());
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			}				
			if(pubhashtags==null || pubhashtags.length==0) {
				LOG.error("PndPublicacionImpl analizaCvsPandora error tace: sin coincidencia en HU_PANDORA_PUBLICACION");
				return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
			}	
			String idPublicacion = String.valueOf(pubhashtags[0].getIdPublicacion());
			
			/**
			 * PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO
			 */
			servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
			HuPandoraPublicacionDto pubNew = new HuPandoraPublicacionDto();
			pubNew.setAnalizando("S");
			pubNew.setFechaMov(LocalDateTime.now());
			pubNew.setIdPerfilPuesto(pubhashtags[0].getIdPerfilPuesto());		
			pubNew.setIdPublicacion(pubhashtags[0].getIdPublicacion());		
			pubNew.setNombrePubilcacion(pubhashtags[0].getNombrePubilcacion());		
			pubNew.setNumVacantes(pubhashtags[0].getNumVacantes());		
			pubNew.setStatus(pubhashtags[0].getStatus());		
			try {
				savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			}
			if(savePubResp==null) {
				LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
			}
						
			/**
			 * RECUPERA EL PERFIL DE BD
			 */
			servicio = ServiciosConstante.PAN_PERFILP_SELECT_SERVICE;
			HuPandoraPerfilPuestoDto huPandoraPerfil = new HuPandoraPerfilPuestoDto();
			huPandoraPerfil.setIdPerfilPuesto(pubhashtags[0].getIdPerfilPuesto().longValue());
			try {
				perfiles = template.postForObject(servicio, huPandoraPerfil, HuPandoraPerfilPuestoDto[].class);
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getStackTrace());
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			}				
			if(perfiles==null || perfiles.length==0) {
				out.setError(true);		
				return new OutCandidatoDto(true, MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE, MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
			}
			Formatter fmt = new Formatter();
			String posicion = idPublicacion + "_" + fmt.format("%04d", perfiles[0].getNumCia().longValue());      
			fmt.close();
			
			/**
			 * OBTIENE LOS CV DE LOS SERVICIOS WEB DE PANDORA
			 */		
			InWsPandoraDto inGetCv = new InWsPandoraDto();
			inGetCv.setIdPosicion(posicion);
			inGetCv.setPaginacion(-1);
			servicio = ServiciosConstante.PANDORA_WS_GETCVS_SERVICE;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", ServiciosConstante.GET_PANDORA_TOKEN);
			HttpEntity< InWsPandoraDto > entity = new HttpEntity<>(inGetCv, headers);
			try {
				wsAddPosResp = template.postForObject(servicio, entity, OutWsPandoraDto.class);
				LOG.info("PndCandidatoImpl analizaCvsPandora Pandora: " + servicio + " input: " + inGetCv.toString());
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getMessage());
				//PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO			 
				servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
				pubNew.setAnalizando("N");		
				try {
					savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);				
				}catch (Exception ex) {
					LOG.error("PndPublicacionImpl update error tace: " + ex.getMessage());
				}
				if(savePubResp==null) {
					LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
				}
				return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
			}
			boolean execute = true;
			int retry = 0;		
			int paginacion = 1;
			int sumaOrdenamiento = 0;
			List<CurriculumsResp> cvs = new ArrayList<CurriculumsResp>();
			List<HuPandoraCandidatoDto> candidatoUpt = new ArrayList<HuPandoraCandidatoDto>();
			while(execute) {
				inGetCv.setIdPosicion(posicion);
				inGetCv.setPaginacion(paginacion);
				HttpEntity< InWsPandoraDto > entityList = new HttpEntity<>(inGetCv, headers);
				try {
					wsAddPosResp = template.postForObject(servicio, entityList, OutWsPandoraDto.class);
					LOG.info("PndCandidatoImpl analizaCvsPandora Pandora: " + servicio + " input: " + inGetCv.toString());
				}catch (Exception e) {
					LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getMessage());				
					//PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO			 
					servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
					pubNew.setAnalizando("N");		
					try {
						savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
					}catch (Exception ex) {
						LOG.error("PndPublicacionImpl update error tace: " + ex.getMessage());
					}
					if(savePubResp==null) {
						LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
					}
					return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
				}
				if(wsAddPosResp.getCodigo().equalsIgnoreCase("-1")) {
					LOG.error("PndCandidatoImpl analizaCvsPandora error code: " + wsAddPosResp.getCodigo());
					retry ++;
					if(retry>29) {
						//PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO			 
						servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
						pubNew.setAnalizando("N");		
						try {
							savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
						}catch (Exception ex) {
							LOG.error("PndPublicacionImpl update error tace: " + ex.getMessage());
						}
						if(savePubResp==null) {
							LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
						}
						return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
					}
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e1) {
						LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e1.getMessage());
					}
					continue;
				}			
				if(wsAddPosResp.getCodigo().equalsIgnoreCase("-1") || wsAddPosResp.getCodigo().equalsIgnoreCase("2")) {
					execute = false;
					continue;
				}
				if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
					//PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO		
					LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + wsAddPosResp.getCodigo());
					servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
					pubNew.setAnalizando("N");		
					try {
						savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
					}catch (Exception ex) {
						LOG.error("PndPublicacionImpl update error tace: " + ex.getMessage());
					}
					if(savePubResp==null) {
						LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
					}
					return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));			
				}	
				try {
					for(CurriculumsResp x : wsAddPosResp.getsCv()) {
						CurriculumsResp y = new CurriculumsResp();
						y.setCv(x.getCv());
						y.setOrden(x.getOrden()+1+sumaOrdenamiento);
						if(x.getIdCandidato().contains("_")) {
							String[] idtemp = x.getIdCandidato().split("_");
							y.setIdCandidato(idtemp[0]);
						}else {
							y.setIdCandidato(x.getIdCandidato());
						}									
						HuPandoraCandidatoDto updt = new HuPandoraCandidatoDto();
						updt.setIdCandidato(Long.parseLong(y.getIdCandidato()));
						updt.setOrden(x.getOrden()+1+sumaOrdenamiento);
						candidatoUpt.add(updt);
						cvs.add(y);
					}
				}catch (Exception e) {
					//PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO		
					LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getMessage());
					servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
					pubNew.setAnalizando("N");		
					try {
						savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
					}catch (Exception ex) {
						LOG.error("PndPublicacionImpl update error tace: " + ex.getMessage());
					}
					if(savePubResp==null) {
						LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
					}
					return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
				}
				sumaOrdenamiento += 20;
				paginacion ++;			
			}
	
			/**
			 * PERSISTE LA PUBLICACION EN HU_PANDORA_PERFIL_PUESTO CON STATUS ANALIZANDO
			 */
			servicio = ServiciosConstante.PAN_PUBLICACION_INSERT_SERVICE;
			pubNew.setAnalizando("N");	
			pubNew.setStatus("D");
			try {
				savePubResp = template.postForObject(servicio, pubNew, HuPandoraPublicacionDto.class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
			}
			if(savePubResp==null) {
				LOG.error("PndPublicacionImpl update error tace: NO SE PUDO ACTUALIZAR STATUS EN BD");
			}
			
			//SE PERSISTE EL CANDIDATO
			servicio = ServiciosConstante.PAN_CANDIDATOS_ORDEN_UPT_SERVICE;
			String insCandidato = "";
			try {
				HttpEntity<List<HuPandoraCandidatoDto> > request = new HttpEntity<>(candidatoUpt);
				ResponseEntity<String> response  = template.exchange(servicio,HttpMethod.PUT,request, String.class);
				insCandidato = response.getBody();
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl selCandidatoPandora error tace: " + e.getMessage());
				if(e.getMessage().contains("HUMAN.FK_PUBLICACION")) {
					return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				}
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			}
			if(!insCandidato.equalsIgnoreCase("save")) {
				LOG.error("PndCandidatoImpl selCandidatoPandora error tace: " + insCandidato);
				if(insCandidato.contains("HUMAN.FK_PUBLICACION")) {
					return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				}
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			}
				
			out.setError(false);
			out.setCvsResp(cvs);
			out.setCodigo(MensajesConstante.SUCCES_CODE);
			out.setMessage(MensajesConstante.SUCCES_MSJ);		
			return out;		
		}catch (Exception e) {
			throw new Exception(this.getClass().getName() + " : " + e.getLocalizedMessage());
		}
	}

	@Override
	public OutCandidatoDto notificaCvsPandora(InCandidatoDto in) {
		OutCandidatoDto out = new OutCandidatoDto();
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getIdPublicacion()==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PUBLICACION"));			
		if(in.getEmails()==null||in.getEmails().isEmpty())return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "EMAIL"));		

		/**
		 * RECUPERA LA PUBLICACION DE BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_HASHTAG_SELECT_SERVICE;
		HuPndPubHashtagDto pub = new HuPndPubHashtagDto();
		pub.setIdPublicacion(in.getIdPublicacion());
		try {
			pubhashtags = template.postForObject(servicio, pub, HuPndPubHashtagDto[].class);
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
		String nombrePublicaion = pubhashtags[0].getNombrePubilcacion();
		
		/**
		 * RECUPERA LOS CV DE BD
		 */
		servicio = ServiciosConstante.PAN_CANDIDATOS_SELECT_SERVICE;
		HuPandoraCandidatoDto inCandidato = new HuPandoraCandidatoDto();
		inCandidato.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
		try {
			candidatosResp = template.postForObject(servicio, inCandidato, HuPandoraCandidatoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl notificaCvsPandora error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(candidatosResp==null || candidatosResp.length==0) {
			LOG.error("PndCandidatoImpl notificaCvsPandora error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		List<HuPandoraCandidatoDto> candidatosList = Arrays.asList(candidatosResp);
		List<HuPandoraCandidatoDto> candidatosSel = new ArrayList<HuPandoraCandidatoDto>();
		for(HuPandoraCandidatoDto x : candidatosList) {
			if(x.getOrden()>0)candidatosSel.add(x);			
		}
		
		//ENVIA EL CORREO CON LOS CV'S
		List<String> lstDestinatarios = new ArrayList<String>(in.getEmails());
		Email email = new Email();
		email.setAsunto("Curriculums seleccionados Pandora");
		email.setBodyHtml(true);		
		candidatosSel.sort(Comparator.comparingLong(s ->s.getOrden()));				
		String cueropHtml = utilService.getBodyCandidatosHtml(in.getNombreUsuario(), nombrePublicaion, candidatosSel, in.getNumCia(), in.getIdPublicacion());
		email.setCuerpoMail(cueropHtml);
		email.setDestinatarios(lstDestinatarios);
		servicio = ServiciosConstante.SEND_MAIL_VSM_SERVICE;            
        template.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
        try {
        	respMail = template.postForObject(servicio ,email, ConsultaArchivoResponseDto.class);
        }catch (Exception e) {
			LOG.error("LoginImpl notificaCvsPandora errortrace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_GENERAL, MensajesConstante.DESCRIPCION_ERROR_GENERAL);
		}
        
        out.setError(false);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);		
		return out;
	}

	@Override
	public OutCandidatoDto updtCandidato(InCandidatoDto in) {		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(in.getIdCandidato()==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID CANDIDATO"));
		if(in.getNombreCandidato()==null&&!in.getNombreCandidato().isBlank())return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE CANDIDATO"));
		
		/**
		 * RECUPERA LOS NOMBRES DE BD
		 */
		String servicio = ServiciosConstante.PAN_NOM_VAL_SELALL_SERVICE;
		try {
			nombresResp = template.getForObject(servicio, HuPandoraNombresValidoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl updtCandidato error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(nombresResp==null || nombresResp.length==0) {
			LOG.error("PndCandidatoImpl updtCandidato error tace: sin conincidencias en HU_PANDORA_NOMBRES_VALIDOS");
		}
		List<HuPandoraNombresValidoDto> nombresList = Arrays.asList(nombresResp);
		
		/**
		 * PERSISTE LOS NOMBRES DE BD
		 */
		List<HuPandoraNombresValidoDto> nomList = new ArrayList<HuPandoraNombresValidoDto>();
		List<String> nombres = new ArrayList<String>();
		String insNombres = "";
		servicio = ServiciosConstante.PAN_NOM_VAL_INSALL_SERVICE;
		StringTokenizer nombre  = new StringTokenizer(in.getNombreCandidato());
		while (nombre.hasMoreTokens()) {			
			String palabra = (String)nombre.nextToken();
			String nom = utilService.clearAccents(palabra);
			nombres.add(nom);
		}
		
		//COMPARA LAS PALABRAS DEL CV CON LA MATRIS DE NOMBRES
		for(String x : nombres) {				
			List<HuPandoraNombresValidoDto> nombresTemp = nombresList.stream().filter(p->p.getNombre().equalsIgnoreCase(x)).collect(Collectors.toList());
			if(nombresTemp==null || nombresTemp.size()==0) {
				HuPandoraNombresValidoDto y = new HuPandoraNombresValidoDto();
				y.setNombre(x);
				nomList.add(y);
			}				
		}	
		if(nomList!=null && !nomList.isEmpty()) {
			try {
				insNombres = template.postForObject(servicio, nomList, String.class);
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl updtCandidato error tace: " + e.getMessage());
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			}
			if(insNombres==null||!insNombres.equalsIgnoreCase("save")) {
				LOG.error("PndCandidatoImpl updtCandidato error tace: " + insNombres);
				return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);				
			}
		}
		
		/**
		 * RECUPERA EL CANDIDATO DE BD
		 */
		servicio = ServiciosConstante.PAN_CANDIDATOS_SELECT_SERVICE;
		HuPandoraCandidatoDto select = new HuPandoraCandidatoDto();
		select.setIdCandidato(in.getIdCandidato());
		try {
			candidatosResp = template.postForObject(servicio, select, HuPandoraCandidatoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl updtCandidato error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(candidatosResp==null || candidatosResp.length==0) {
			LOG.error("PndCandidatoImpl updtCandidato error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		HuPandoraCandidatoDto insert = candidatosResp[0];
        
		/**
		 * PERSISTE EL CANDIDATO EN HU_PANDORA_CANDIDATO
		 */
		servicio = ServiciosConstante.PAN_CANDIDATOS_INSERT_SERVICE;
		insert.setNombreCandidato(in.getNombreCandidato());
		String insCandidato = "";
		try {
			insCandidato = template.postForObject(servicio, insert, String.class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl updtCandidato error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}
		if(!insCandidato.equalsIgnoreCase("save")) {
			LOG.error("PndCandidatoImpl importaCv error tace: " + insCandidato);
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_INSERT_CODE, MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
		}		
		return new OutCandidatoDto(false, MensajesConstante.SUCCES_CODE, MensajesConstante.SUCCES_MSJ);
	}
	
	@Override
	public OutCandidatoDto getAnalisisCvsPandora(InCandidatoDto in) {
		OutCandidatoDto out = new OutCandidatoDto();
		
		/**
		 * RECUPERA LA PUBLICACION DE BD
		 */
		String servicio = ServiciosConstante.PAN_PUBLICACION_HASHTAG_SELECT_SERVICE;
		HuPndPubHashtagDto inPub = new HuPndPubHashtagDto();
		inPub.setIdPublicacion(in.getIdPublicacion());
		try {
			pubhashtags = template.postForObject(servicio, inPub, HuPndPubHashtagDto[].class);
		}catch (Exception e) {
			LOG.error("PndPublicacionImpl analizaCvsPandora error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(pubhashtags==null || pubhashtags.length==0) {
			LOG.error("PndPublicacionImpl analizaCvsPandora error tace: sin coincidencia en HU_PANDORA_PUBLICACION");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE, MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
		}	
		String idPublicacion = String.valueOf(pubhashtags[0].getIdPublicacion());
				
		/**
		 * RECUPERA EL PERFIL DE BD
		 */
		servicio = ServiciosConstante.PAN_PERFILP_SELECT_SERVICE;
		HuPandoraPerfilPuestoDto huPandoraPerfil = new HuPandoraPerfilPuestoDto();
		huPandoraPerfil.setIdPerfilPuesto(pubhashtags[0].getIdPerfilPuesto().longValue());
		try {
			perfiles = template.postForObject(servicio, huPandoraPerfil, HuPandoraPerfilPuestoDto[].class);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getStackTrace());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(perfiles==null || perfiles.length==0) {
			out.setError(true);		
			return new OutCandidatoDto(true, MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE, MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
		}
		String numCia = perfiles[0].getNumCia().toString();                         
		template.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
		
		/**
		 * OBTIENE LOS CV DE LOS SERVICIOS WEB DE PANDORA
		 */
		InWsPandoraDto inGetCv = new InWsPandoraDto();
		inGetCv.setIdPosicion(idPublicacion + "_" + numCia);
		inGetCv.setPaginacion(in.getPagina());
		servicio = ServiciosConstante.PANDORA_WS_GETCVS_SERVICE;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", ServiciosConstante.GET_PANDORA_TOKEN);
		HttpEntity< InWsPandoraDto > entity = new HttpEntity<>(inGetCv, headers);		
		boolean execute = true;
		int retry = 0;
		while(execute) {
			inGetCv.setIdPosicion(idPublicacion + "_" + numCia);
			inGetCv.setPaginacion(0);			
			try {
				wsAddPosResp = template.postForObject(servicio, entity, OutWsPandoraDto.class);
			}catch (Exception e) {
				LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e.getMessage());				
				retry ++;
				if(retry>10)return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e1.getMessage());
				}
				continue;
			}
			if(wsAddPosResp.getCodigo().equalsIgnoreCase("-1")) {
				LOG.error("PndCandidatoImpl analizaCvsPandora error code: " + wsAddPosResp.getCodigo());
				retry ++;
				if(retry>10)return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "GetCurriculums"));
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					LOG.error("PndCandidatoImpl analizaCvsPandora error tace: " + e1.getMessage());
				}
				continue;
			}
			if(!wsAddPosResp.getCodigo().equalsIgnoreCase("-1")) {
				execute = false;
			}
		}
		
		List<com.vsm.dto.HuPandoraCandidatoDto> cvs = new ArrayList<com.vsm.dto.HuPandoraCandidatoDto>();
		if(wsAddPosResp.getsCv()!=null) {			
			for(CurriculumsResp x : wsAddPosResp.getsCv()) {
				com.vsm.dto.HuPandoraCandidatoDto y = new com.vsm.dto.HuPandoraCandidatoDto();				
				y.setOrden(x.getOrden()+1);
				if(x.getIdCandidato().contains("_")) {
					String[] idtemp = x.getIdCandidato().split("_");
					y.setIdCandidato(Long.parseLong(idtemp[0]));
				}else {
					y.setIdCandidato(Long.parseLong(x.getIdCandidato()));
				}
				
				/**
				 * RECUPERA LOS CV DE BD
				 */
				servicio = ServiciosConstante.PAN_CANDIDATOS_SELECT_SERVICE;
				com.vsm.dto.HuPandoraCandidatoDto select = new com.vsm.dto.HuPandoraCandidatoDto();
				select.setIdCandidato(y.getIdCandidato());
				try {
					candidatosResp2 = template.postForObject(servicio, select, com.vsm.dto.HuPandoraCandidatoDto[].class);
				}catch (Exception e) {
					LOG.error("PndCandidatoImpl getCv error tace: " + e.getMessage());
					return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				}				
				if(candidatosResp2==null || candidatosResp2.length==0) {
					LOG.error("PndCandidatoImpl getCv error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
					return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
				}
				
				y.setFoto(candidatosResp2[0].getFoto());
				y.setIdPublicacion(candidatosResp2[0].getIdPublicacion());
				y.setNombreCandidato(candidatosResp2[0].getNombreCandidato());
				y.setNombreCv(candidatosResp2[0].getNombreCv());
				cvs.add(y);
			}
		}
		
		out.setError(false);
		out.setCandidatoslist(cvs);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);		
		return out;
	}
	
	@Override
	public OutCandidatoDto getNextIdImport() {
		OutCandidatoDto out = new OutCandidatoDto();
		Date date = new Date();		
    	long IdImport = date.getTime() + (int)(Math.random()*30+1);		
		out.setError(false);
		out.setNextIdImport(IdImport);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out;
	}
	
	@Override
	public OutCandidatoDto importarCv(InCandidatoDto in) throws Exception {
		try {
			OutCandidatoDto out = new OutCandidatoDto(false, MensajesConstante.SUCCES_CODE, MensajesConstante.SUCCES_MSJ);
			List<HuPandoraCandidatoDto> listOut = new ArrayList<HuPandoraCandidatoDto>();
	
			try {
				in.setCv(in.getCv().replace("data:application/pdf;base64,", ""));	
				String regex = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+{3,15}";
				Pattern pattern = Pattern.compile(regex);		
				List<String> palabras = new ArrayList<String>();
				String nombreCandidato = "";
				String firstName = "";
				String cv = "";
				String nombreCv = "";
				String foto = "";	
				
				/**
				 * VALICAIONES DE LOS DATOS DE ENTRADA
				 */	
				if(in.getCv()==null||in.getCv().isBlank())throw new Exception(MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "CURRICULUMS"));
				
				if(in.getNombreCandidato()==null || in.getNombreCandidato().isBlank()) {
					HashMap<String,String> cvProcesado = utilService.cvProcesado(in.getCv(), in.getNombreArchivo(), in.getNumCia(), in.getIdPublicacion());
					if(cvProcesado==null){
						throw new Exception("El cv no es valido y no puede ser procesado");
					}
					cv = cvProcesado.get("texto");
					nombreCv = cvProcesado.get("filename");
					foto = cvProcesado.get("foto");		
					StringTokenizer string  = new StringTokenizer(cv);
					while (string.hasMoreTokens()) {			
						String palabra = (String)string.nextToken();
						Matcher m = pattern.matcher(palabra);
						if(m.matches()) {
							palabras.add(utilService.clearAccents(palabra));
						}
					}
					List<String> nombreSinDuplicados = palabras.stream().distinct().collect(Collectors.toList());
					
					/**
					 * RECUPERA LOS NOMBRES DE BD
					 */
					String servicio = ServiciosConstante.PAN_NOM_VAL_SELALL_SERVICE;
					try {
						nombresResp = template.getForObject(servicio, HuPandoraNombresValidoDto[].class);
					}catch (Exception e) {
						LOG.error("PndCandidatoImpl importaCv error tace: " + e.getMessage());
						throw new Exception(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
					}				
					if(nombresResp==null || nombresResp.length==0) {
						LOG.error("PndCandidatoImpl importaCv error tace: sin conincidencias en HU_PANDORA_NOMBRES_VALIDOS");
					}
					List<HuPandoraNombresValidoDto> nombresList = Arrays.asList(nombresResp);
					
					//COMPARA LAS PALABRAS DEL CV CON LA MATRIS DE NOMBRES
					int contador = 0;
					for(String x : nombreSinDuplicados) {				
						List<HuPandoraNombresValidoDto> nombresTemp = nombresList.stream().filter(p->p.getNombre().equalsIgnoreCase(x)).collect(Collectors.toList());
						if(nombresTemp!=null && nombresTemp.size()>0 && contador < 2) {
							nombreCandidato = nombreCandidato + " " + x; 
							contador ++;
							if(contador <= 1) {
								firstName = firstName + x;
							}
						}				
					}
					nombreCandidato = nombreCandidato.trim();
					if(nombreCandidato.isBlank()) {
						throw new Exception(MensajesConstante.ERROR_NOMBRE_NO_FOUND_MSJ);
					}
				}else {		
					/**
					 * PERSISTE LOS NOMBRES DE BD
					 */
					List<HuPandoraNombresValidoDto> nomList = new ArrayList<HuPandoraNombresValidoDto>();
					String insNombres = "";
					String servicio = ServiciosConstante.PAN_NOM_VAL_INSALL_SERVICE;
					StringTokenizer nombre  = new StringTokenizer(in.getNombreCandidato());
					while (nombre.hasMoreTokens()) {			
						String palabra = (String)nombre.nextToken();
						Matcher m = pattern.matcher(palabra);
						if(m.matches()) {
							HuPandoraNombresValidoDto nom = new HuPandoraNombresValidoDto();
							nom.setNombre(utilService.clearAccents(palabra));
							nomList.add(nom);
						}
					}
					try {
						insNombres = template.postForObject(servicio, nomList, String.class);
					}catch (Exception e) {
						LOG.error("PndCandidatoImpl importaCv error tace: " + e.getMessage());
						throw new Exception(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
					}
					if(insNombres==null||insNombres.equalsIgnoreCase("save")) {
						LOG.error("PndCandidatoImpl importaCv error tace: " + insNombres);
						throw new Exception(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);				
					}
					nombreCandidato = in.getNombreCandidato().trim();
				}	
		        
		        //CREA EL OBJETO CANDIDATO
		        HuPandoraCandidatoDto insert = new HuPandoraCandidatoDto();
		        insert.setFoto(foto!=null&&!foto.isBlank()?foto:MensajesConstante.GET_FOTO_IMG_DEFAULT);
		        insert.setNombreCandidato(nombreCandidato);
		        insert.setNombreCv(nombreCv);
		        insert.setStatus("A");
		        insert.setIdPublicacion(new BigDecimal(in.getIdPublicacion()));
		        insert.setIdImportacion(String.valueOf(in.getIdImportacion()));
		        
		        /**
				 * PERSISTE EL CANDIDATO EN HU_PANDORA_CANDIDATO
				 */
				String servicio = ServiciosConstante.PAN_CANDIDATOS_INSDTO_SERVICE;
				HuPandoraCandidatoDto insCandidato = new HuPandoraCandidatoDto();
				try {
					insCandidato = template.postForObject(servicio, insert, HuPandoraCandidatoDto.class);
				}catch (Exception e) {
					LOG.error("PndCandidatoImpl importaCv error tace: " + e.getMessage());
					throw new Exception(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
				}
				
				/**
				 * ENVIA LOS CV A LOS SERVICIOS WEB DE PANDORA
				 */
				Formatter fmt = new Formatter();			
				template.getInterceptors().add(new BasicAuthenticationInterceptor("Human", "T!2eDkTwX4MAhsnZSBfppX*VzVukNS"));
				InWsPandoraDto inAddCv = new InWsPandoraDto();
				inAddCv.setIdPosicion(in.getIdPublicacion() + "_" + fmt.format("%04d", in.getNumCia()));
				inAddCv.setIdCandidato(String.valueOf(insCandidato.getIdCandidato()));
				inAddCv.setCv(in.getCv());
				servicio = ServiciosConstante.PANDORA_WS_ADDCV_SERVICE;
				try {
					wsAddPosResp = template.postForObject(servicio, inAddCv, OutWsPandoraDto.class);
					LOG.info("importarCv Pandora: " + servicio + " input: " + inAddCv.toString());
					fmt.close();			
				}catch (Exception e) {
					LOG.error("PndCandidatoImpl importaCv error tace: " + e.getMessage());
					return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddCurriculum"));
				}
				if(!wsAddPosResp.getCodigo().equalsIgnoreCase("0")) {
					LOG.error("PndCandidatoImpl importaCv error code: " + wsAddPosResp.getCodigo());
					return new OutCandidatoDto(true, MensajesConstante.ERROR_WS_PANDORA_CODE, MensajesConstante.ERROR_WS_PANDORA_MSJ.replace("{nombreWs}", "AddCurriculum"));
				}
				
				insCandidato.setCv(null);
				listOut.add(insCandidato);
				out.setCvProcesados(out.getCvProcesados() + 1);				
			}catch (Exception e) {
				out.setMessage(out.getMessage() + e.getMessage() + " en el cv " + in.getNombreArchivo()!=null?in.getNombreArchivo():" ");
				out.setCvError(out.getCvError() + 1);
			}		
			out.setCandidatos(listOut);			
			return out;
		}catch (Exception e) {
			throw new Exception(this.getClass().getName() + " : " + e.getLocalizedMessage());
		}
	}
	
	@Override
	public OutCandidatoDto getCvFileSystem(long idCandidato, long ciaNum) {
		OutCandidatoDto out = new OutCandidatoDto();
			
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */	
		if(idCandidato==0)return new OutCandidatoDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID CANDIDATO"));
		com.vsm.dto.HuPandoraCandidatoDto in = new com.vsm.dto.HuPandoraCandidatoDto();		
		in.setIdCandidato(idCandidato);

		/**
		 * RECUPERA LOS CV DE BD
		 */
		String servicio = ServiciosConstante.PAN_CANDIDATOS_NOCV_SELECT_SERVICE;
		HuPandoraCandidatoDto candidato = new HuPandoraCandidatoDto();
		try {
			HuPandoraCandidatoDto[] candidatoResp = template.postForObject(servicio, in, HuPandoraCandidatoDto[].class);
			candidato = candidatoResp[0];
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl getCvFileSystem error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_EXECUTE_SELECT_CODE, MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
		}				
		if(candidato==null || candidato.getIdCandidato()==0) {
			LOG.error("PndCandidatoImpl getCvFileSystem error tace: sin conincidencias en HU_PANDORA_CANDIDATOS");
			return new OutCandidatoDto(true, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_CODE, MensajesConstante.ERROR_CANDIDATO_NO_FOUND_MSJ);
		}
		File file = new File(ServiciosConstante.GET_PANDORA_FILE_SYSTEM.replace("{cia}", String.valueOf(ciaNum)) + "/" + candidato.getIdPublicacion() + "/" + candidato.getNombreCv());
		String cv = "";
		try {
			cv = utilService.encodeFileToBase64(file);
		}catch (Exception e) {
			LOG.error("PndCandidatoImpl getCvFileSystem error tace: " + e.getMessage());
			return new OutCandidatoDto(true, MensajesConstante.ERROR_GENERAL, MensajesConstante.DESCRIPCION_ERROR_GENERAL);
		}
		candidato.setCv(cv);
		
    	out.setError(false);
		out.setCandidato(candidato);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out;
	}
}