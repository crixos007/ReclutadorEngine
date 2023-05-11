package com.vsm.service;

import com.vsm.dto.OutUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioDto;
import com.vsm.lib.dto.human.HuPandoraUsuarioINSDto;

public interface PndUsuarioService {
	OutUsuarioDto save(HuPandoraUsuarioINSDto huPandoraUsr);
	OutUsuarioDto update(HuPandoraUsuarioDto huPandoraUsr);
	OutUsuarioDto getPassword(HuPandoraUsuarioDto huPandoraUsr);
}