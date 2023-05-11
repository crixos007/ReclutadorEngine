package com.vsm.service;

import com.vsm.dto.InPublicacionDto;
import com.vsm.dto.OutPublicacionDto;
import com.vsm.lib.dto.human.HuPandoraPublicacionDto;
import com.vsm.lib.dto.human.HuPndPubHashtagDto;

public interface PndPublicacionService {
	OutPublicacionDto update(HuPandoraPublicacionDto huPandoraPublicacion);
	OutPublicacionDto baja(HuPandoraPublicacionDto huPandoraPublicacion);
	//OutPublicacionDto listPublicacion(HuPandoraPublicacionDto huPandoraPublicacion);
	OutPublicacionDto save(InPublicacionDto huPandoraPubHashtag);
	OutPublicacionDto update(InPublicacionDto huPandoraPubHashtag);
	OutPublicacionDto depuraPublicacion(long numCia);
	OutPublicacionDto listAllPublicacion(boolean byStatus);
	OutPublicacionDto save(HuPandoraPublicacionDto in, long numCia);
	OutPublicacionDto listPublicacion(HuPndPubHashtagDto in);
}