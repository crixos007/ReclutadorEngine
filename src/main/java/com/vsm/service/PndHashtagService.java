package com.vsm.service;

import com.vsm.dto.OutHashtagDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;

public interface PndHashtagService {
	OutHashtagDto save(HuPandoraHashtagDto huPandoraHashtag);
	OutHashtagDto update(HuPandoraHashtagDto huPandoraHashtag);
	OutHashtagDto listHashTag(HuPandoraHashtagDto huPandoraHashtag);
	OutHashtagDto listAllHashTag();	
}