package com.vsm.dto;

import com.vsm.lib.dto.human.HuPandoraUsuarioDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutUsuarioDto {
	boolean error;
	String codigo; 
	String message;
	HuPandoraUsuarioDto usuario;
	
	public OutUsuarioDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}	
}