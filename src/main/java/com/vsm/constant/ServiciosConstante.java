package com.vsm.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("SvcConstantes")
public class ServiciosConstante {
	public static String VERSION_DEV = "version en desarrollo - 0.0.9";
		
	public static String PARAMETRO_HUMAN_SERVICE;
	@Value("${GET_PARAMETRO_HUMAN}")
	public void GET_PARAMETRO_HUMAN(String GET_PARAMETRO_HUMAN) {
		PARAMETRO_HUMAN_SERVICE = GET_PARAMETRO_HUMAN;
    }
	
	public static String PAN_COMPANIA_INSERT_SERVICE;
	@Value("${PAN_COMPANIA_INSERT}")
	public void PAN_COMPANIA_INSERT(String PAN_COMPANIA_INSERT) {
		PAN_COMPANIA_INSERT_SERVICE = PAN_COMPANIA_INSERT;
    }
	
	public static String PAN_IDIOMA_INSERT_SERVICE;
	@Value("${PAN_IDIOMA_INSERT}")
	public void PAN_IDIOMA_INSERT(String PAN_IDIOMA_INSERT) {
		PAN_IDIOMA_INSERT_SERVICE = PAN_IDIOMA_INSERT;
    }
	
	public static String PAN_USR_INSERT_SERVICE;
	@Value("${PAN_USR_INSERT}")
	public void PAN_USR_INSERT(String PAN_USR_INSERT) {
		PAN_USR_INSERT_SERVICE = PAN_USR_INSERT;
    }

	public static String PAN_USR_BYUSR_SELECT_SERVICE;
	@Value("${PAN_USR_BYUSR_SELECT}")
	public void PAN_USR_BYUSR_SELECT(String PAN_USR_BYUSR_SELECT) {
		PAN_USR_BYUSR_SELECT_SERVICE = PAN_USR_BYUSR_SELECT;
    }
	
	public static String PAN_USR_BYMAIL_SELECT_SERVICE;
	@Value("${PAN_USR_BYMAIL_SELECT}")
	public void PAN_USR_BYMAIL_SELECT(String PAN_USR_BYMAIL_SELECT) {
		PAN_USR_BYMAIL_SELECT_SERVICE = PAN_USR_BYMAIL_SELECT;
    }
	
	public static String PAN_IDIOMA_SELECT_SERVICE;
	@Value("${PAN_IDIOMA_SELECT}")
	public void PAN_IDIOMA_SELECT(String PAN_IDIOMA_SELECT) {
		PAN_IDIOMA_SELECT_SERVICE = PAN_IDIOMA_SELECT;
    }
	
	public static String PAN_COMPANIA_SELECT_SERVICE;
	@Value("${PAN_COMPANIA_SELECT}")
	public void PAN_COMPANIA_SELECT(String PAN_COMPANIA_SELECT) {
		PAN_COMPANIA_SELECT_SERVICE = PAN_COMPANIA_SELECT;
    }
	
	public static String PAN_USR_UPDATE_SERVICE;
	@Value("${PAN_USR_UPDATE}")
	public void PAN_USR_UPDATE(String PAN_USR_UPDATE) {
		PAN_USR_UPDATE_SERVICE = PAN_USR_UPDATE;
    }
	
	public static String PAN_IDIOMA_SELECT_ALL_SERVICE;
	@Value("${PAN_IDIOMA_SELECT_ALL}")
	public void PAN_IDIOMA_SELECT_ALL(String PAN_IDIOMA_SELECT_ALL) {
		PAN_IDIOMA_SELECT_ALL_SERVICE = PAN_IDIOMA_SELECT_ALL;
    }
	
	public static String PAN_COMPANIA_SELECT_ALL_SERVICE;
	@Value("${PAN_COMPANIA_SELECT_ALL}")
	public void PAN_COMPANIA_SELECT_ALL(String PAN_COMPANIA_SELECT_ALL) {
		PAN_COMPANIA_SELECT_ALL_SERVICE = PAN_COMPANIA_SELECT_ALL;
    }
	
	public static String PAN_PERFILP_SELALL_SERVICE;
	@Value("${PAN_PERFILP_SELALL}")
	public void PAN_PERFILP_SELALL(String PAN_PERFILP_SELALL) {
		PAN_PERFILP_SELALL_SERVICE = PAN_PERFILP_SELALL;
    }
	
	public static String PAN_PERFILP_SELECT_SERVICE;
	@Value("${PAN_PERFILP_SELECT}")
	public void PAN_PERFILP_SELECT(String PAN_PERFILP_SELECT) {
		PAN_PERFILP_SELECT_SERVICE = PAN_PERFILP_SELECT;
    }
	
	public static String PAN_PERFILP_INSERT_SERVICE;
	@Value("${PAN_PERFILP_INSERT}")
	public void PAN_PERFILP_INSERT(String PAN_PERFILP_INSERT) {
		PAN_PERFILP_INSERT_SERVICE = PAN_PERFILP_INSERT;
    }
	
	public static String PAN_HASHTAG_SELALL_SERVICE;
	@Value("${PAN_HASHTAG_SELALL}")
	public void PAN_HASHTAG_SELALL(String PAN_HASHTAG_SELALL) {
		PAN_HASHTAG_SELALL_SERVICE = PAN_HASHTAG_SELALL;
    }
	
	public static String PAN_HASHTAG_SELECT_SERVICE;
	@Value("${PAN_HASHTAG_SELECT}")
	public void PAN_HASHTAG_SELECT(String PAN_HASHTAG_SELECT) {
		PAN_HASHTAG_SELECT_SERVICE = PAN_HASHTAG_SELECT;
    }
	
	public static String PAN_HASHTAG_INSERT_SERVICE;
	@Value("${PAN_HASHTAG_INSERT}")
	public void PAN_HASHTAG_INSERT(String PAN_HASHTAG_INSERT) {
		PAN_HASHTAG_INSERT_SERVICE = PAN_HASHTAG_INSERT;
    }
	
	public static String PAN_PUBLICACION_SELALL_SERVICE;
	@Value("${PAN_PUBLICACION_SELALL}")
	public void PAN_PUBLICACION_SELALL(String PAN_PUBLICACION_SELALL) {
		PAN_PUBLICACION_SELALL_SERVICE = PAN_PUBLICACION_SELALL;
    }
	
	public static String PAN_PUBLICACION_SELECT_SERVICE;
	@Value("${PAN_PUBLICACION_SELECT}")
	public void PAN_PUBLICACION_SELECT(String PAN_PUBLICACION_SELECT) {
		PAN_PUBLICACION_SELECT_SERVICE = PAN_PUBLICACION_SELECT;
    }
	
	public static String PAN_PUBLICACION_INSERT_SERVICE;
	@Value("${PAN_PUBLICACION_INSERT}")
	public void PAN_PUBLICACION_INSERT(String PAN_PUBLICACION_INSERT) {
		PAN_PUBLICACION_INSERT_SERVICE = PAN_PUBLICACION_INSERT;
    }
	
	public static String PAN_HASHTAG_PUB_SELALL_SERVICE;
	@Value("${PAN_HASHTAG_PUB_SELALL}")
	public void PAN_HASHTAG_PUB_SELALL(String PAN_HASHTAG_PUB_SELALL) {
		PAN_HASHTAG_PUB_SELALL_SERVICE = PAN_HASHTAG_PUB_SELALL;
    }
	
	public static String PAN_HASHTAG_PUB_SELECT_SERVICE;
	@Value("${PAN_HASHTAG_PUB_SELECT}")
	public void PAN_HASHTAG_PUB_SELECT(String PAN_HASHTAG_PUB_SELECT) {
		PAN_HASHTAG_PUB_SELECT_SERVICE = PAN_HASHTAG_PUB_SELECT;
    }
	
	public static String PAN_HASHTAG_PUB_INSERT_SERVICE;
	@Value("${PAN_HASHTAG_PUB_INSERT}")
	public void PAN_HASHTAG_PUB_INSERT(String PAN_HASHTAG_PUB_INSERT) {
		PAN_HASHTAG_PUB_INSERT_SERVICE = PAN_HASHTAG_PUB_INSERT;
    }
	
	public static String PAN_PERFIL_HASHTAG_SELECT_SERVICE;
	@Value("${PAN_PERFIL_HASHTAG_SELECT}")
	public void PAN_PERFIL_HASHTAG_SELECT(String PAN_PERFIL_HASHTAG_SELECT) {
		PAN_PERFIL_HASHTAG_SELECT_SERVICE = PAN_PERFIL_HASHTAG_SELECT;
    }
	
	public static String PAN_PUBLICACION_HASHTAG_SELECT_SERVICE;
	@Value("${PAN_PUBLICACION_HASHTAG_SELECT}")
	public void PAN_PUBLICACION_HASHTAG_SELECT(String PAN_PUBLICACION_HASHTAG_SELECT) {
		PAN_PUBLICACION_HASHTAG_SELECT_SERVICE = PAN_PUBLICACION_HASHTAG_SELECT;
    }
	
	public static String PAN_HASHTAG_PUB_INSALL_SERVICE;
	@Value("${PAN_HASHTAG_PUB_INSALL}")
	public void PAN_HASHTAG_PUB_INSALL(String PAN_HASHTAG_PUB_INSALL) {
		PAN_HASHTAG_PUB_INSALL_SERVICE = PAN_HASHTAG_PUB_INSALL;
    }
	
	public static String PAN_HASHTAG_INSALL_SERVICE;
	@Value("${PAN_HASHTAG_INSALL}")
	public void PAN_HASHTAG_INSALL(String PAN_HASHTAG_INSALL) {
		PAN_HASHTAG_INSALL_SERVICE = PAN_HASHTAG_INSALL;
    }
	
	public static String PAN_HASHTAG_PUB_DELALL_SERVICE;
	@Value("${PAN_HASHTAG_PUB_DELALL}")
	public void PAN_HASHTAG_PUB_DELALL(String PAN_HASHTAG_PUB_DELALL) {
		PAN_HASHTAG_PUB_DELALL_SERVICE = PAN_HASHTAG_PUB_DELALL;
    }
	
	public static String PAN_HASHTAG_DELALL_SERVICE;
	@Value("${PAN_HASHTAG_DELALL}")
	public void PAN_HASHTAG_DELALL(String PAN_HASHTAG_DELALL) {
		PAN_HASHTAG_DELALL_SERVICE = PAN_HASHTAG_DELALL;
    }
	
	public static String PAN_PERFIL_PUB_SELECT_SERVICE;
	@Value("${PAN_PERFIL_PUB_SELECT}")
	public void PAN_PERFIL_PUB_SELECT(String PAN_PERFIL_PUB_SELECT) {
		PAN_PERFIL_PUB_SELECT_SERVICE = PAN_PERFIL_PUB_SELECT;
    }
	
	public static String PAN_PUBLICACION_INSALL_SERVICE;
	@Value("${PAN_PUBLICACION_INSALL}")
	public void PAN_PUBLICACION_INSALL(String PAN_PUBLICACION_INSALL) {
		PAN_PUBLICACION_INSALL_SERVICE = PAN_PUBLICACION_INSALL;
    }
	
	public static String PAN_NOM_VAL_SELALL_SERVICE;
	@Value("${PAN_NOM_VAL_SELALL}")
	public void PAN_NOM_VAL_SELALL(String PAN_NOM_VAL_SELALL) {
		PAN_NOM_VAL_SELALL_SERVICE = PAN_NOM_VAL_SELALL;
    }
	
	public static String PAN_NOM_VAL_INSERT_SERVICE;
	@Value("${PAN_NOM_VAL_INSERT}")
	public void PAN_NOM_VAL_INSERT(String PAN_NOM_VAL_INSERT) {
		PAN_NOM_VAL_INSERT_SERVICE = PAN_NOM_VAL_INSERT;
    }
	
	public static String PAN_NOM_VAL_INSALL_SERVICE;
	@Value("${PAN_NOM_VAL_INSALL}")
	public void PAN_NOM_VAL_INSALL(String PAN_NOM_VAL_INSALL) {
		PAN_NOM_VAL_INSALL_SERVICE = PAN_NOM_VAL_INSALL;
    }
	
	public static String PAN_CANDIDATOS_INSERT_SERVICE;
	@Value("${PAN_CANDIDATOS_INSERT}")
	public void PAN_CANDIDATOS_INSERT(String PAN_CANDIDATOS_INSERT) {
		PAN_CANDIDATOS_INSERT_SERVICE = PAN_CANDIDATOS_INSERT;
    }
	
	public static String PAN_CANDIDATOS_INSDTO_SERVICE;
	@Value("${PAN_CANDIDATOS_INSDTO}")
	public void PAN_CANDIDATOS_INSDTO(String PAN_CANDIDATOS_INSDTO) {
		PAN_CANDIDATOS_INSDTO_SERVICE = PAN_CANDIDATOS_INSDTO;
    }
	
	public static String PAN_CANDIDATOS_INSALL_SERVICE;
	@Value("${PAN_CANDIDATOS_INSALL}")
	public void PAN_CANDIDATOS_INSALL(String PAN_CANDIDATOS_INSALL) {
		PAN_CANDIDATOS_INSALL_SERVICE = PAN_CANDIDATOS_INSALL;
    }
	
	public static String PAN_CANDIDATOS_SELECT_SERVICE;
	@Value("${PAN_CANDIDATOS_SELECT}")
	public void PAN_CANDIDATOS_SELECT(String PAN_CANDIDATOS_SELECT) {
		PAN_CANDIDATOS_SELECT_SERVICE = PAN_CANDIDATOS_SELECT;
    }
	
	public static String PAN_CANDIDATOS_NOCV_SELECT_SERVICE;
	@Value("${PAN_CANDIDATOS_NOCV_SELECT}")
	public void PAN_CANDIDATOS_NOCV_SELECT(String PAN_CANDIDATOS_NOCV_SELECT) {
		PAN_CANDIDATOS_NOCV_SELECT_SERVICE = PAN_CANDIDATOS_NOCV_SELECT;
    }
	
	public static String PAN_CANDIDATOS_DELETE_SERVICE;
	@Value("${PAN_CANDIDATOS_DELETE}")
	public void PAN_CANDIDATOS_DELETE(String PAN_CANDIDATOS_DELETE) {
		PAN_CANDIDATOS_DELETE_SERVICE = PAN_CANDIDATOS_DELETE;
    }
	
	public static String PAN_CANDIDATOS_SELALL_SERVICE;
	@Value("${PAN_CANDIDATOS_SELALL}")
	public void PAN_CANDIDATOS_SELALL(String PAN_CANDIDATOS_SELALL) {
		PAN_CANDIDATOS_SELALL_SERVICE = PAN_CANDIDATOS_SELALL;
    }
	
	public static String PANDORA_WS_ADDPOSICION_SERVICE;
	@Value("${PANDORA_WS_ADDPOSICION}")
	public void PANDORA_WS_ADDPOSICION(String PANDORA_WS_ADDPOSICION) {
		PANDORA_WS_ADDPOSICION_SERVICE = PANDORA_WS_ADDPOSICION;
    }
	
	public static String PANDORA_WS_ADDCV_SERVICE;
	@Value("${PANDORA_WS_ADDCV}")
	public void PANDORA_WS_ADDCV(String PANDORA_WS_ADDCV) {
		PANDORA_WS_ADDCV_SERVICE = PANDORA_WS_ADDCV;
    }
	
	public static String PANDORA_WS_GETCVS_SERVICE;
	@Value("${PANDORA_WS_GETCVS}")
	public void PANDORA_WS_GETCVS(String PANDORA_WS_GETCVS) {
		PANDORA_WS_GETCVS_SERVICE = PANDORA_WS_GETCVS;
    }
	
	public static String PANDORA_WS_DELCVS_SERVICE;
	@Value("${PANDORA_WS_DELCVS}")
	public void PANDORA_WS_DELCVS(String PANDORA_WS_DELCVS) {
		PANDORA_WS_DELCVS_SERVICE = PANDORA_WS_DELCVS;
    }
	
	public static String SEND_MAIL_VSM_SERVICE; 
	@Value("${SEND_MAIL_VSM}")
    public void SEND_MAIL_VSM(String SEND_MAIL_VSM) {
		SEND_MAIL_VSM_SERVICE = SEND_MAIL_VSM;
    }
	
	public static String PAN_PUBLICACION_HASHTAG_SELALL_SERVICE; 
	@Value("${PAN_PUBLICACION_HASHTAG_SELALL}")
    public void PAN_PUBLICACION_HASHTAG_SELALL(String PAN_PUBLICACION_HASHTAG_SELALL) {
		PAN_PUBLICACION_HASHTAG_SELALL_SERVICE = PAN_PUBLICACION_HASHTAG_SELALL;
    }		
	
	public static String GET_PANDORA_FILE_SYSTEM; 
	@Value("${PANDORA_FILE_SYSTEM}")	
	public void PANDORA_FILE_SYSTEM(String PANDORA_FILE_SYSTEM) {
		GET_PANDORA_FILE_SYSTEM = PANDORA_FILE_SYSTEM;
    }
	
	public static String GET_PANDORA_PDF_RESOLVER; 
	@Value("${PANDORA_PDF_RESOLVER}")	
	public void PANDORA_PDF_RESOLVER(String PANDORA_PDF_RESOLVER) {
		GET_PANDORA_PDF_RESOLVER = PANDORA_PDF_RESOLVER;
    }
	
	public static String GET_PANDORA_TOKEN; 
	@Value("${BASIC_PANDORA_AUTH}")	
	public void BASIC_PANDORA_AUTH(String BASIC_PANDORA_AUTH) {
		GET_PANDORA_TOKEN = BASIC_PANDORA_AUTH;
    }
	
	public static String GET_PAN_LAST_IMPORT_CANDIDATO; 
	@Value("${PAN_LAST_IMPORT_CANDIDATO}")	
	public void PAN_LAST_IMPORT_CANDIDATO(String PAN_LAST_IMPORT_CANDIDATO) {
		GET_PAN_LAST_IMPORT_CANDIDATO = PAN_LAST_IMPORT_CANDIDATO;
    }

	public static String PAN_CANDIDATOS_ORDEN_UPT_SERVICE;
	@Value("${PAN_CANDIDATOS_ORDEN_UPT}")
	public void PAN_CANDIDATOS_ORDEN_UPT(String PAN_CANDIDATOS_ORDEN_UPT) {
		PAN_CANDIDATOS_ORDEN_UPT_SERVICE = PAN_CANDIDATOS_ORDEN_UPT;
    }	
}