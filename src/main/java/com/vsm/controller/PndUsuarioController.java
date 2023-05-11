package com.vsm.controller;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsm.dto.OutUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioINSDto;
import com.vsm.service.PndUsuarioService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Usuario")
/**
 * @Api(tags = "UsuarioPandora")
 * @author rcaraveo
 *
 */
public class PndUsuarioController {
	private static final Logger LOG = LogManager.getLogger(PndUsuarioController.class.getName());
	
	@Autowired
	PndUsuarioService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Alta")
	public OutUsuarioDto saveUsrPandora(@RequestBody HuPandoraUsuarioINSDto huPandoraUsr) {
		LOG.info("saveUsrPandora para usuario: " + huPandoraUsr.getNombre() + " " + huPandoraUsr.getApellidoPat() + 
				 " " + huPandoraUsr.getApellidoMat() + " BEGIN: " + LocalDateTime.now());
		return service.save(huPandoraUsr);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para moficar el usario")
	 * @param HuPandoraUsuarioDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Alctualizar")
	public OutUsuarioDto updtUsrPandora(@RequestBody HuPandoraUsuarioDto huPandoraUsr) {
		LOG.info("updtUsrPandora para usuario: " + huPandoraUsr.getNombre() + " " + huPandoraUsr.getApellidoPat() + 
				 " " + huPandoraUsr.getApellidoMat() + " BEGIN: " + LocalDateTime.now());
		return service.update(huPandoraUsr);
	}
	
	/**
	 * @ApiOperation(value = "Servicio para recuperar el password del usario")
	 * @param HuPandoraUsuarioDto
	 * @return OutUsuarioDto
	 */
	@PostMapping("Recupera")
	public OutUsuarioDto recuperaUsrPandora(@RequestBody HuPandoraUsuarioDto huPandoraUsr) {
		LOG.info("recuperaUsrPandora para usuario: " + huPandoraUsr.getNombre() + " " + huPandoraUsr.getApellidoPat() + 
				 " " + huPandoraUsr.getApellidoMat() + " BEGIN: " + LocalDateTime.now());
		return service.getPassword(huPandoraUsr);
	}
}