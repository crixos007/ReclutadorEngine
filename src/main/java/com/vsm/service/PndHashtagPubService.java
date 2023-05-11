package com.vsm.service;

import com.vsm.dto.OutHashtagPubDto;
import com.vsm.lib.dto.human.HuPandoraHashtagPublicacionDto;

public interface PndHashtagPubService {
	OutHashtagPubDto save(HuPandoraHashtagPublicacionDto in);
	OutHashtagPubDto update(HuPandoraHashtagPublicacionDto in);
	OutHashtagPubDto baja(HuPandoraHashtagPublicacionDto in);
	OutHashtagPubDto listHashTag(HuPandoraHashtagPublicacionDto in);
	OutHashtagPubDto listAllHashTag();
}