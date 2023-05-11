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

import com.vsm.dto.InPerfilDto;
import com.vsm.dto.OutPerfilDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;
import com.vsm.service.PndPerfilService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Perfil")
/**
 * @Api(tags = "PerfilPandora")
 * @author rcaraveo
 *
 */
public class PndPerfilController {
	private static final Logger LOG = LogManager.getLogger(PndPerfilController.class.getName());
	
	@Autowired
	PndPerfilService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Perfil")
	 * @param HuPandoraPerfilPuestoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("Alta")
	public OutPerfilDto savePerfilPandora(@RequestBody HuPandoraPerfilPuestoDto in) {
		LOG.info("savePerfilPandora Perfil: " + in.getNombrePerfil() +	 " " + in.getAliasPerfil() + " BEGIN: " + LocalDateTime.now());
		return service.save(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el Perfil")
	 * @param HuPandoraUsuarioDto
	 * @return OutPerfilDto
	 */
	@PutMapping()
	public OutPerfilDto updtPerfilPandora(@RequestBody HuPandoraPerfilPuestoDto in) {
		LOG.info("updtPerfilPandora Perfil: " + in.getIdPerfilPuesto() + " BEGIN: " + LocalDateTime.now());
		return service.update(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Consulta")
	public OutPerfilDto listPerfilPandora(@RequestBody HuPandoraPerfilPuestoDto in) {
		LOG.info("listPerfilPandora Perfil BEGIN: " + LocalDateTime.now());		
		return service.listPerfil(in);		
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("ListAll")
	public OutPerfilDto listAllPerfilPandora() {
		LOG.info("listAllPerfilPandora BEGIN: " + LocalDateTime.now());
		return service.listAllPerfil();
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Perfil y el hashtag")
	 * @param InPerfilDto
	 * @return OutPublicacionDto
	 */
	@PostMapping("/Hashtag/Alta")
	public OutPerfilDto savePubHashPandora(@RequestBody InPerfilDto in) {
		LOG.info("savePublicacionPandora Publicacion: " + in.getPerfil().getNombrePerfil() + " BEGIN: " + LocalDateTime.now());
		return service.save(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para modificar el Perfil y el hashtag")
	 * @param InPerfilDto
	 * @return OutPublicacionDto
	 */
	@PutMapping("/Hashtag")
	public OutPerfilDto updatePubHashPandora(@RequestBody InPerfilDto in) {
		LOG.info("updatePubHashPandora Publicacion: " + in.getPerfil().getIdPerfilPuesto() + " BEGIN: " + LocalDateTime.now());
		return service.update(in);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el Perfil")
	 * @param HuPandoraPerfilPuestoDto
	 * @return OutPerfilDto
	 */
	@PostMapping("Alta/Masiva")
	public OutPerfilDto savePerfilAllPandora(@RequestBody InPerfilDto in) {
		LOG.info("PndPerfilController savePerfilAllPandora BEGIN: " + LocalDateTime.now());
		return service.saveAll(in);
	}
}