package com.vsm.controller;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsm.dto.OutHashtagDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.service.PndHashtagService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Hashtag")
/**
 * @Api(tags = "HashtagPandora")
 * @author rcaraveo
 *
 */
public class PndHashtagController {
	private static final Logger LOG = LogManager.getLogger(PndHashtagController.class.getName());
	
	@Autowired
	PndHashtagService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Hashtag")
	 * @param HuPandoraHashtagDto
	 * @return OutHashtagDto
	 */
	@PostMapping("Alta")
	public OutHashtagDto saveHshTgPandora(@RequestBody HuPandoraHashtagDto huPandoraHashtag) {
		LOG.info("saveHshTgPandora Hashtag: " + huPandoraHashtag.getIdPerfilPuesto() +	 " " + huPandoraHashtag.getNombre() + " BEGIN: " + LocalDateTime.now());
		return service.save(huPandoraHashtag);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Hashtag")
	 * @param HuPandoraUsuarioDto
	 * @return OutHashtagDto
	 */
	@PutMapping()
	public OutHashtagDto updtHshTgPandora(@RequestBody HuPandoraHashtagDto huPandoraHashtag) {
		LOG.info("updtHshTgPandora Hashtag: " + huPandoraHashtag.getIdPerfilPuesto() +	 " " + huPandoraHashtag.getNombre() + " BEGIN: " + LocalDateTime.now());
		return service.update(huPandoraHashtag);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Consulta")
	public OutHashtagDto listHashTagPandora(@RequestBody HuPandoraHashtagDto huPandoraHashtag) {
		LOG.info("listHashTagPandora Hashtag: " + huPandoraHashtag.getIdPerfilPuesto() +	 " " + huPandoraHashtag.getNombre() + " BEGIN: " + LocalDateTime.now());		
		return service.listHashTag(huPandoraHashtag);		
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("ListAll")
	public OutHashtagDto listAllHashTagPandora() {
		LOG.info("listAllHashTagPandora BEGIN: " + LocalDateTime.now());
		return service.listAllHashTag();
	}
}