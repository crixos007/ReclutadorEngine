package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraHashtagDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutHashtagDto {
	boolean error;
	String codigo; 
	String message;
	HuPandoraHashtagDto hashtag;
	List<HuPandoraHashtagDto> hashtags;
	
	public OutHashtagDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}