package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraIdiomaDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutIdiomaDto {
	boolean error;
	String codigo; 
	String message;
	List<HuPandoraIdiomaDto> idioma;
	
	public OutIdiomaDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}