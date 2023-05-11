package com.vsm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InWsPandoraDto {
	String cv;
	String idCandidato;
	String idPosicion;
	
	String descripcion;
	List<String> hashtag;

	String numCia;
	long paginacion;
	
	@Override
	public String toString() {
		return "InWsPandoraDto [idCandidato=" + idCandidato + ", idPosicion=" + idPosicion
				+ ", descripcion=" + descripcion + ", hashtag=" + hashtag + ", numCia=" + numCia + ", paginacion="
				+ paginacion + "]";
	}	
}