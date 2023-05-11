package com.vsm.controller;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsm.dto.OutCompaniaDto;
import com.vsm.service.PndCompaniaService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Compania")
/**
 * @Api(tags = "CompaniaPandora")
 * @author rcaraveo
 *
 */
public class PndCompaniaController {
	private static final Logger LOG = LogManager.getLogger(PndCompaniaController.class.getName());
	
	@Autowired
	PndCompaniaService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("Consulta")
	public OutCompaniaDto listCompaniaPandora(@RequestParam(value = "numCia", required = false, defaultValue = "0") long numCia, 
  		   								 	  @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre) {
		LOG.info("listCompaniaPandora para la cia: " + numCia + "-" + nombre + " BEGIN: " + LocalDateTime.now());
		if(numCia==0&&nombre.isBlank()) {
			return service.listCompania();
		}else {
			return service.listCompania(numCia, nombre);
		}
	}
}