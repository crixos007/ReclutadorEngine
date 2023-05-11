package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutHashtagPubDto {
	boolean error;
	String codigo; 
	String message;
	HuPandoraHashtagPublicacionDto hashPub;
	List<HuPandoraHashtagPublicacionDto> hashtagPub;
	
	public OutHashtagPubDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}