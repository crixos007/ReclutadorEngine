package com.vsm.service;

import com.vsm.dto.InPerfilDto;
import com.vsm.dto.OutPerfilDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;

public interface PndPerfilService {
	OutPerfilDto save(HuPandoraPerfilPuestoDto in);
	OutPerfilDto update(HuPandoraPerfilPuestoDto in);
	OutPerfilDto listPerfil(HuPandoraPerfilPuestoDto in);
	OutPerfilDto listAllPerfil();
	OutPerfilDto save(InPerfilDto in);
	OutPerfilDto update(InPerfilDto in);
	OutPerfilDto saveAll(InPerfilDto in);
}