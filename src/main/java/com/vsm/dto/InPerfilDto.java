package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InPerfilDto {
	HuPandoraPerfilPuestoDto perfil;
	List<HuPandoraHashtagDto> perfilHashtag;
	
	long numCia;
	String archivoPerfiles;
}