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
import org.springframework.web.bind.annotation.RestController;

import com.vsm.dto.OutHashtagPubDto;
import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;
import com.vsm.service.PndHashtagPubService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/HashtagPublicacion")
/**
 * @Api(tags = "HashtagPubPandora")
 * @author rcaraveo
 *
 */
public class PndHashtagPubController {
	private static final Logger LOG = LogManager.getLogger(PndHashtagPubController.class.getName());
	
	@Autowired
	PndHashtagPubService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Hashtag Publicacion")
	 * @param HuPandoraHashtagPublicacionDto
	 * @return OutHashtagPubDto
	 */
	@PostMapping("Alta")
	public OutHashtagPubDto saveHshTgPubPandora(@RequestBody HuPandoraHashtagPublicacionDto in) {
		LOG.info("saveHshTgPubPandora Hashtag Publicacion: " + in.getIdPublicacion() +	 " " + in.getNombre() + " BEGIN: " + LocalDateTime.now());
		return service.save(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Hashtag Publicacion")
	 * @param HuPandoraHashtagPublicacionDto
	 * @return OutHashtagPubDto
	 */
	@PutMapping()
	public OutHashtagPubDto updtHshTgPubPandora(@RequestBody HuPandoraHashtagPublicacionDto in) {
		LOG.info("updtHshTgPubPandora Hashtag Publicacion: " + in.getIdPublicacion() +	 " " + in.getNombre() + " BEGIN: " + LocalDateTime.now());
		return service.update(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Hashtag Publicacion")
	 * @param HuPandoraHashtagPublicacionDto
	 * @return OutHashtagPubDto
	 */
	@DeleteMapping()
	public OutHashtagPubDto bajaHshTgPubPandora(@RequestBody HuPandoraHashtagPublicacionDto in) {
		LOG.info("bajaHshTgPubPandora Hashtag Publicacion: " + in.getIdPublicacion() +	 " " + in.getNombre() + " BEGIN: " + LocalDateTime.now());
		return service.baja(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Hashtag Publicacion")
	 * @param HuPandoraHashtagPublicacionDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Consulta")
	public OutHashtagPubDto listHashTagPandora(@RequestBody HuPandoraHashtagPublicacionDto in) {
		LOG.info("listHashTagPandora Hashtag Publicacion: " + in.getIdPublicacion() +	 " " + in.getNombre() + " BEGIN: " + LocalDateTime.now());		
		return service.listHashTag(in);		
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Hashtag Publicacion")
	 * @param HuPandoraHashtagPublicacionDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("ListAll")
	public OutHashtagPubDto listAllHashTagPandora() {
		LOG.info("listAllHashTagPandora BEGIN: " + LocalDateTime.now());
		return service.listAllHashTag();
	}
}