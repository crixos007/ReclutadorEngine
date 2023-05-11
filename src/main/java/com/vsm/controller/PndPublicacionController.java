package com.vsm.controller;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsm.dto.InPublicacionDto;
import com.vsm.dto.OutPublicacionDto;
import com.vsm.lib.dto.human.HuPandoraPublicacionDto;
import com.vsm.lib.dto.human.HuPndPubHashtagDto;
import com.vsm.service.PndPublicacionService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Publicacion")
/**
 * @Api(tags = "PuPandora")
 * @author rcaraveo
 *
 */
public class PndPublicacionController {
	private static final Logger LOG = LogManager.getLogger(PndPublicacionController.class.getName());
	
	@Autowired
	PndPublicacionService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Publicacion")
	 * @param HuPandoraPubilcacionDto
	 * @return OutPublicacionDto
	 */
	@PostMapping("Alta")
	public OutPublicacionDto savePublicacionPandora(@RequestBody HuPandoraPublicacionDto huPandoraPublicacion,
													@RequestParam(value = "numCia", required = true) long numCia) {
		LOG.info("savePublicacionPandora Publicacion: " + huPandoraPublicacion.getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());
		return service.save(huPandoraPublicacion, numCia);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Publicacion")
	 * @param HuPandoraUsuarioDto
	 * @return OutPublicacionDto
	 */
	@PutMapping()
	public OutPublicacionDto updtPublicacionPandora(@RequestBody HuPandoraPublicacionDto huPandoraPublicacion) {
		LOG.info("updtPublicacionPandora Publicacion: " + huPandoraPublicacion.getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());
		return service.update(huPandoraPublicacion);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Publicacion")
	 * @param HuPandoraUsuarioDto
	 * @return OutPublicacionDto
	 */
	@DeleteMapping()
	public OutPublicacionDto bajaPublicacionPandora(@RequestBody HuPandoraPublicacionDto huPandoraPublicacion) {
		LOG.info("updtPublicacionPandora Publicacion: " + huPandoraPublicacion.getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());
		return service.baja(huPandoraPublicacion);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para consultar la publicacion")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Consulta")
	public OutPublicacionDto listPublicacionPandora(@RequestBody HuPndPubHashtagDto huPandoraPublicacion) {
		LOG.info("listPublicacionPandora Publicacion: " + huPandoraPublicacion.getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());		
		return service.listPublicacion(huPandoraPublicacion);		
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("ListAll")
	public OutPublicacionDto listAllPublicacionPandora(@RequestParam(value = "byStatus", required = true, defaultValue = "false") boolean byStatus) {
		LOG.info("listAllPublicacionPandora BEGIN: " + LocalDateTime.now());
		return service.listAllPublicacion(byStatus);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir la Publicacion y el hashtag")
	 * @param InPublicacionDto
	 * @return OutPublicacionDto
	 */
	@PostMapping("/Hashtag/Alta")
	public OutPublicacionDto savePubHashPandora(@RequestBody InPublicacionDto huPandoraPubHashtag) {
		LOG.info("savePubHashPandora Publicacion: " + huPandoraPubHashtag.getPublicacion().getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());
		return service.save(huPandoraPubHashtag);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir la Publicacion y el hashtag")
	 * @param InPublicacionDto
	 * @return OutPublicacionDto
	 */
	@PutMapping("/Hashtag")
	public OutPublicacionDto updatePubHashPandora(@RequestBody InPublicacionDto huPandoraPubHashtag) {
		LOG.info("updatePubHashPandora Publicacion: " + huPandoraPubHashtag.getPublicacion().getNombrePubilcacion() +	 " BEGIN: " + LocalDateTime.now());
		return service.update(huPandoraPubHashtag);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir la Publicacion y el hashtag")
	 * @param InPublicacionDto
	 * @return OutPublicacionDto
	 */
	@PutMapping("/Depura")
	public OutPublicacionDto depuraPubPandora(@RequestParam(value = "numCia", required = true) long numCia) {
		LOG.info("depuraPubPandora Publicacion para la compania: " + numCia +	 " BEGIN: " + LocalDateTime.now());
		return service.depuraPublicacion(numCia);
	}
}