package com.vsm.controller;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.vsm.constant.MensajesConstante;
import com.vsm.dto.InCandidatoDto;
import com.vsm.dto.OutCandidatoDto;
import com.vsm.service.PndCandidatoService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Candidato")
/**
 * @Api(tags = "CandidatoPandora")
 * @author rcaraveo
 *
 */
public class PndCandidatoController {
	private static final Logger LOG = LogManager.getLogger(PndCandidatoController.class.getName());
	
	@Autowired
	PndCandidatoService service;
	
	/**
	 * @ApiOperation(value = "Servicio para importar el CV del Candigato")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("ImportarCv")
	public ResponseEntity<OutCandidatoDto> importaCvPandora(@RequestBody InCandidatoDto in) {		
		Date date = new Date();		
    	String idTraza = (int)(Math.random()*30+1) + date.getTime() + "_" + in.getIdPublicacion() + "_PAN ";	
    	OutCandidatoDto out = new OutCandidatoDto();
		
		//LOGICA DEL SERVICIO
		try {	
			try {
				out = service.importarCv(in);
			}catch (Exception e) {
				LOG.error(idTraza +" ERROR MSG: " + e.getLocalizedMessage());
				throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE);
			}
			return new ResponseEntity<OutCandidatoDto>(out,HttpStatus.OK);
		}
		catch(HttpStatusCodeException exception) {
			LOG.error(idTraza +" ERROR MSG: " + exception.getLocalizedMessage());
			HttpHeaders headers=new HttpHeaders();
			headers.add("error", MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			headers.add("errorDesc", exception.getResponseBodyAsString());			
			out.setCodigo(MensajesConstante.ERROR_GENERAL);	
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			out.setError(true);
			return new ResponseEntity<OutCandidatoDto>(out,headers,exception.getStatusCode());
		}
	}
	
	/**
	 * @ApiOperation(value = "Servicio para obtener el CV del Candigato")
	 * @param numCia
	 * @param numCandidato
	 * @return OutPerfilDto
	 */
	@GetMapping("ConsultaCv")
	public OutCandidatoDto getPandoraCv(@RequestParam(value = "idPub", required = false, defaultValue = "0") long idPub,
										@RequestParam(value = "idImport", required = false, defaultValue = "0") long idImport) {
		LOG.info("getPandoraCv BEGIN: " + LocalDateTime.now());
		return service.getCv(idPub, idImport);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para eliminar el Candidato")
	 * @param numCandidato
	 * @return OutCandidatoDto
	 */
	@DeleteMapping()
	public OutCandidatoDto bajaCandidatoPandora(@RequestParam(value = "numCandidato", required = true) long numCandidato,
												@RequestParam(value = "numPublicaion", required = true) long numPublicaion,
												@RequestParam(value = "numCia", required = true) long numCia) {
		LOG.info("bajaCandidatoPandora Candidato: " + numCandidato + " BEGIN: " + LocalDateTime.now());
		return service.baja(numCandidato,numPublicaion,numCia);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para seleccionar el CV del Candigato")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("SeleccionaCandidato")
	public OutCandidatoDto selCandidatoPandora(@RequestBody InCandidatoDto in) {
		LOG.info("selCandidatoPandora publicaion: " + in.getIdPublicacion() + " BEGIN: " + LocalDateTime.now());
		return service.selCandidatoPandora(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para analizar los CV de la Publicacion")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("AnalizaCandidato")
	public ResponseEntity<OutCandidatoDto> analizaCvsPandora(@RequestBody InCandidatoDto in) {
		Date date = new Date();		
    	String idTraza = (int)(Math.random()*30+1) + date.getTime() + "_" + in.getIdPublicacion() + "_PAN ";	
    	OutCandidatoDto out = new OutCandidatoDto();
		
		//LOGICA DEL SERVICIO
		try {	
			try {
				out = service.analizaCvsPandora(in);
			}catch (Exception e) {
				LOG.error(idTraza +" ERROR MSG: " + e.getLocalizedMessage());
				throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE);
			}
			return new ResponseEntity<OutCandidatoDto>(out,HttpStatus.OK);
		}
		catch(HttpStatusCodeException exception) {
			LOG.error(idTraza +" ERROR MSG: " + exception.getLocalizedMessage());
			HttpHeaders headers=new HttpHeaders();
			headers.add("error", MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			headers.add("errorDesc", exception.getResponseBodyAsString());			
			out.setCodigo(MensajesConstante.ERROR_GENERAL);	
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			out.setError(true);
			return new ResponseEntity<OutCandidatoDto>(out,headers,exception.getStatusCode());
		}
	}
	
	/**
	 * @ApiOperation(value = "Servicio para notificar los CV de la Publicacion")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("Notifica")
	public OutCandidatoDto notificaCvsPandora(@RequestBody InCandidatoDto in) {
		LOG.info("analizaCvsPandora publicaion: " + in.getIdPublicacion() + " BEGIN: " + LocalDateTime.now());
		return service.notificaCvsPandora(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para modificar el nombre del CV del Candigato")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PutMapping()
	public OutCandidatoDto updtCandidato(@RequestBody InCandidatoDto in) {
		LOG.info("updtCandidato BEGIN: " + LocalDateTime.now());
		return service.updtCandidato(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para analizar los CV de la Publicacion")
	 * @param InCandidatoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("GetAnalisisCandidato")
	public OutCandidatoDto getAnalisisCvsPandora(@RequestBody InCandidatoDto in) {
		LOG.info("getAnalisisCvsPandora publicaion: " + in.getIdPublicacion() + " BEGIN: " + LocalDateTime.now());
		return service.getAnalisisCvsPandora(in);
	}

	/**
	 * @ApiOperation(value = "Servicio para analizar los CV de la Publicacion")
	 * @param 
	 * @return OutPerfilDto
	 */
	@GetMapping("GetNextIdImport")
	public OutCandidatoDto getNextIdImport() {
		LOG.info("getNextIdImport publicaion BEGIN: " + LocalDateTime.now());
		return service.getNextIdImport();
	}
	
	/**
	 * @ApiOperation(value = "Servicio para eliminar el Candidato")
	 * @param numCandidato
	 * @return OutCandidatoDto
	 */
	@GetMapping("Consulta")
	public OutCandidatoDto getCandidatoPandora(@RequestParam(value = "numCandidato", required = true) long numCandidato,
											   @RequestParam(value = "numCia", required = true) long numCia) {
		LOG.info("bajaCandidatoPandora Candidato: " + numCandidato + " BEGIN: " + LocalDateTime.now());
		return service.getCvFileSystem(numCandidato,numCia);
	}
}