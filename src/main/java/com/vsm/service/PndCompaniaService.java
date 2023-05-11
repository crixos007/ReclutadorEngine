package com.vsm.service;

import com.vsm.dto.OutCompaniaDto;

public interface PndCompaniaService {
	OutCompaniaDto listCompania(long numCia, String nombre);
	OutCompaniaDto listCompania();
}