package com.vsm.dto;

import java.util.List;

import com.vsm.lib.dto.human.HuPandoraCandidatoDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutCandidatoDto {
	boolean error;
	String codigo; 
	String message;
	
	String cv;
	
	List<String> cvs;
	
	List<CurriculumsResp> cvsResp;
	
	List<HuPandoraCandidatoDto> candidatos;
	
	List<com.vsm.dto.HuPandoraCandidatoDto> candidatoslist;
	
	HuPandoraCandidatoDto candidato;	
	
	long totalCv;
	long cvProcesados;
	long cvError;
	
	long nextIdImport;
	
	public OutCandidatoDto(boolean error, String codigo, String message) {
		super();
		this.error = error;
		this.codigo = codigo;
		this.message = message;
	}
}