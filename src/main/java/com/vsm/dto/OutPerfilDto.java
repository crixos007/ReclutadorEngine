package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;
import com.vsm.lib.dto.human.HuPndPerfilHastagDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutPerfilDto {
	boolean error;
	String codigo; 
	String message;
	HuPandoraPerfilPuestoDto perfil;
	List<HuPandoraPerfilPuestoDto> perfiles;
	List<HuPndPerfilHastagDto> perfilHashtag;
	
	public OutPerfilDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}