package com.vsm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurriculumsResp {
	String cv;
	String hash;
	String idCandidato;
    long   orden;
}
