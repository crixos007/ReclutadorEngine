package com.vsm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.OutIdiomaDto;
import com.vsm.lib.dto.human.HuPandoraIdiomaDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndIdiomaService;
import com.vsm.util.Utility;

@Service("idiomaPandoraService")
public class PndIdiomaImpl implements PndIdiomaService {
	private static final Logger LOG = LogManager.getLogger(PndIdiomaImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraIdiomaDto idioma;
	HuPandoraIdiomaDto[] idiomas;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutIdiomaDto listIdioma(long id, String desc) {
		OutIdiomaDto out = new OutIdiomaDto();
		List<HuPandoraIdiomaDto> idiomaList = new ArrayList<HuPandoraIdiomaDto>();
			
		/**
		 * RECUPERA LA COMAPANIA DE BD
		 */
		String servicio = ServiciosConstante.PAN_IDIOMA_SELECT_SERVICE + "id=" + id + "&desc=" + desc;
		try {
			idioma = template.getForObject(servicio, HuPandoraIdiomaDto.class);
		}catch (Exception e) {
			LOG.error("IdiomaPandoraImpl listIdioma error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(idioma==null || idioma.getDescripcion().isBlank()) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_IDIOMA_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_IDIOMA_NO_EXISTE_MSJ);
			return out;
		}	
		idiomaList.add(idioma);
		
		out.setError(false);
		out.setIdioma(idiomaList);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutIdiomaDto listIdioma() {
		OutIdiomaDto out = new OutIdiomaDto();
		List<HuPandoraIdiomaDto> idiomaList = new ArrayList<HuPandoraIdiomaDto>();
		
		/**
		 * RECUPERA LA COMAPANIA DE BD
		 */
		String servicio = ServiciosConstante.PAN_IDIOMA_SELECT_ALL_SERVICE;
		try {
			idiomas = template.getForObject(servicio, HuPandoraIdiomaDto[].class);
		}catch (Exception e) {
			LOG.error("IdiomaPandoraImpl listIdioma error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(idiomas==null || idiomas.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_IDIOMA_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_IDIOMA_NO_EXISTE_MSJ);
			return out;
		}	
		idiomaList = Arrays.asList(idiomas);
		
		out.setError(false);
		out.setIdioma(idiomaList);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
}