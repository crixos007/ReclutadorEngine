package com.vsm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Email {	
	String asunto;
	boolean bodyHtml;
	String cuerpoMail;
	List<String> destinatarios;
}