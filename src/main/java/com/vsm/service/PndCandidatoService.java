package com.vsm.service;

import com.vsm.dto.InCandidatoDto;
import com.vsm.dto.OutCandidatoDto;

public interface PndCandidatoService {
	OutCandidatoDto selCandidatoPandora(InCandidatoDto in);
	OutCandidatoDto analizaCvsPandora(InCandidatoDto in) throws Exception;
	OutCandidatoDto notificaCvsPandora(InCandidatoDto in);
	OutCandidatoDto updtCandidato(InCandidatoDto in);
	OutCandidatoDto getAnalisisCvsPandora(InCandidatoDto in);
	OutCandidatoDto baja(long idCandidato, long idPublicacion, long numCia);
	OutCandidatoDto getNextIdImport();
	OutCandidatoDto getCv(long idPub, long idImport);
	OutCandidatoDto importarCv(InCandidatoDto in) throws Exception;
	OutCandidatoDto getCvFileSystem(long idCandidato, long numCia);
}