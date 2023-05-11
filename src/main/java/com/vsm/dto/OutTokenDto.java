package com.vsm.dto;

import com.vsm.lib.dto.human.HuPandoraUsuarioDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutTokenDto {
	String token;
	boolean error;
	String codigo; 
	String message;
	HuPandoraUsuarioDto usuario;
}