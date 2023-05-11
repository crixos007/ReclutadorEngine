package com.vsm.service;

import com.vsm.dto.OutTokenDto;

public interface UserService {
	OutTokenDto getJWTToken(String user, String cia, String getBy, String idTraza) throws Exception;
}