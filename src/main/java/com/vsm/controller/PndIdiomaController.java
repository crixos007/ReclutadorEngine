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

import com.vsm.dto.OutIdiomaDto;
import com.vsm.service.PndIdiomaService;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/Idioma")
/**
 * @Api(tags = "IdiomaPandora")
 * @author rcaraveo
 *
 */
public class PndIdiomaController {
	private static final Logger LOG = LogManager.getLogger(PndIdiomaController.class.getName());
	
	@Autowired
	PndIdiomaService service;
	
	/**
	 * @ApiOperation(value = "Servicio para persistir el usario")
	 * @param HuPandoraUsuarioINSDto
	 * @return OutUsuarioDto
	 */
	@GetMapping("Consulta")
	public OutIdiomaDto listIdiomaPandora(@RequestParam(value = "id", required = false, defaultValue = "0") long id, 
  		   								  @RequestParam(value = "desc", required = false, defaultValue = "") String desc) {
		LOG.info("listIdiomaPandora para la cia: " + id + "-" + desc + " BEGIN: " + LocalDateTime.now());
		if(id==0&&desc.isBlank()) {
			return service.listIdioma();
		}else {
			return service.listIdioma(id, desc);
		}
	}
}