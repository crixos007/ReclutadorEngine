package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraCompaniaDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutCompaniaDto {
	boolean error;
	String codigo; 
	String message;
	List<HuPandoraCompaniaDto> compania;
	
	public OutCompaniaDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}