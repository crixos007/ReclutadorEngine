package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraPublicacionDto;
import com.vsm.lib.dto.human.HuPndPubHashtagDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutPublicacionDto {
	boolean error;
	String codigo; 
	String message;
	HuPandoraPublicacionDto pub;
	List<HuPandoraPublicacionDto> publicacion;
	List<HuPndPubHashtagDto> pubHash;
	List<HuPndPubHashtagDto> pubHashInactivas;
	
	public OutPublicacionDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}