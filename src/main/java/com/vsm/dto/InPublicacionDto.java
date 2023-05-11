package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;
import com.vsm.lib.dto.human.HuPandoraPublicacionDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InPublicacionDto {
	HuPandoraPublicacionDto publicacion;
	List<HuPandoraHashtagPublicacionDto> pubHashtag;
	
	long numCia;
}