package com.vsm.service;

import com.vsm.dto.OutIdiomaDto;

public interface PndIdiomaService {
	OutIdiomaDto listIdioma();
	OutIdiomaDto listIdioma(long id, String desc);
}