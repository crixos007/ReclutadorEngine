package com.vsm.dto;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OutWsPandoraDto {
	String codigo;	
	List<CurriculumsResp> sCv;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public List<CurriculumsResp> getsCv() {
		return sCv;
	}
	public void setsCv(List<CurriculumsResp> sCv) {
		this.sCv = sCv;
	}	
}