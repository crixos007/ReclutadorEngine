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
import com.vsm.dto.OutCompaniaDto;
import com.vsm.lib.dto.human.HuPandoraCompaniaDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndCompaniaService;
import com.vsm.util.Utility;

@Service("ciaPandoraService")
public class PndCompaniaImpl implements PndCompaniaService {
	private static final Logger LOG = LogManager.getLogger(PndCompaniaImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraCompaniaDto cia;
	HuPandoraCompaniaDto[] cias;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutCompaniaDto listCompania(long numCia, String nombre) {
		OutCompaniaDto out = new OutCompaniaDto();
		List<HuPandoraCompaniaDto> compania = new ArrayList<HuPandoraCompaniaDto>();
			
		/**
		 * RECUPERA LA COMAPANIA DE BD
		 */
		String servicio = ServiciosConstante.PAN_COMPANIA_SELECT_SERVICE + "numCia=" + numCia + "&nombre=" + nombre;
		try {
			cia = template.getForObject(servicio, HuPandoraCompaniaDto.class);
		}catch (Exception e) {
			LOG.error("CompaniaPandoraImpl listCompania error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(cia==null || cia.getNombre().isBlank()) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_CIA_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_CIA_NO_EXISTE_MSJ);
			return out;
		}	
		compania.add(cia);
		
		out.setError(false);
		out.setCompania(compania);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutCompaniaDto listCompania() {
		OutCompaniaDto out = new OutCompaniaDto();
		List<HuPandoraCompaniaDto> compania = new ArrayList<HuPandoraCompaniaDto>();
		
		/**
		 * RECUPERA LA COMAPANIA DE BD
		 */
		String servicio = ServiciosConstante.PAN_COMPANIA_SELECT_ALL_SERVICE;
		try {
			cias = template.getForObject(servicio, HuPandoraCompaniaDto[].class);
		}catch (Exception e) {
			LOG.error("CompaniaPandoraImpl listCompania error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(cias==null || cias.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_CIA_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_CIA_NO_EXISTE_MSJ);
			return out;
		}	
		compania = Arrays.asList(cias);
		
		out.setError(false);
		out.setCompania(compania);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
}