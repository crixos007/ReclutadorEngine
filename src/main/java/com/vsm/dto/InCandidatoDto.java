package com.vsm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InCandidatoDto {
	String cv;	
	String nombreArchivo;
	
	String nombreCandidato;
	
	long idPublicacion;
	List<Long> candidatos;
	List<String> emails;	
	
	long idCandidato;
	
	long numCia;
	
	long pagina;
	
	String nombreUsuario;
	
	List<InCandidatoDto> candidatosCv;
	
	long orden;
	
	boolean deselecciona;
	
	long idImportacion;
	
	String idTraza;
}