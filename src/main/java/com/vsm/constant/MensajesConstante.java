package com.vsm.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("MsjConstantes")
public class MensajesConstante {
	public static String ERROR_EXECUTE_SELECT_CODE; 
	@Value("${ERROR_BD003_COD}")	
	public void ERROR_BD003_COD(String ERROR_BD003_COD) {
		ERROR_EXECUTE_SELECT_CODE = ERROR_BD003_COD;
    }
	public static String ERROR_EXECUTE_SELECT_MSJ; 
	@Value("${ERROR_BD003_MSJ}")	
	public void ERROR_BD003_MSJ(String ERROR_BD003_MSJ) {
		ERROR_EXECUTE_SELECT_MSJ = ERROR_BD003_MSJ;
    }
	
	public static String ERROR_EXECUTE_INSERT_CODE; 
	@Value("${ERROR_BD004_COD}")	
	public void ERROR_BD004_COD(String ERROR_BD004_COD) {
		ERROR_EXECUTE_INSERT_CODE = ERROR_BD004_COD;
    }
	public static String ERROR_EXECUTE_INSERT_MSJ; 
	@Value("${ERROR_BD004_MSJ}")	
	public void ERROR_BD004_MSJ(String ERROR_BD004_MSJ) {
		ERROR_EXECUTE_INSERT_MSJ = ERROR_BD004_MSJ;
    }
	
	public static String ERROR_GENERAL;
	@Value("${GET_COD_ERROR_EG000}")
    public void GET_COD_ERROR_EG000(String GET_COD_ERROR_EG000) {
		ERROR_GENERAL = GET_COD_ERROR_EG000;
    }	
	public static String DESCRIPCION_ERROR_GENERAL;
	@Value("${GET_DESC_ERROR_EG000}")
    public void GET_DESC_ERROR_EG000(String GET_DESC_ERROR_EG000) {
		DESCRIPCION_ERROR_GENERAL = GET_DESC_ERROR_EG000;
    }
	
	public static String SUCCES_CODE; 
	@Value("${ERROR_EP000_COD}")	
	public void ERROR_EP000_COD(String ERROR_EP000_COD) {
		SUCCES_CODE = ERROR_EP000_COD;
    }	
	public static String SUCCES_MSJ; 
	@Value("${ERROR_EP000_MSJ}")	
	public void ERROR_EP000_MSJ(String ERROR_EP000_MSJ) {
		SUCCES_MSJ = ERROR_EP000_MSJ;
    }
	
	public static String ERROR_USR_NO_DATA_CODE; 
	@Value("${ERROR_EP001_COD}")	
	public void ERROR_EP001_COD(String ERROR_EP001_COD) {
		ERROR_USR_NO_DATA_CODE = ERROR_EP001_COD;
    }	
	public static String ERROR_USR_NO_DATA_MSJ; 
	@Value("${ERROR_EP001_MSJ}")	
	public void ERROR_EP001_MSJ(String ERROR_EP001_MSJ) {
		ERROR_USR_NO_DATA_MSJ = ERROR_EP001_MSJ;
    }
	
	public static String ERROR_PASSWORD_CODE; 
	@Value("${ERROR_EP002_COD}")	
	public void ERROR_EP002_COD(String ERROR_EP002_COD) {
		ERROR_PASSWORD_CODE = ERROR_EP002_COD;
    }	
	public static String ERROR_PASSWORD_MSJ; 
	@Value("${ERROR_EP002_MSJ}")	
	public void ERROR_EP002_MSJ(String ERROR_EP002_MSJ) {
		ERROR_PASSWORD_MSJ = ERROR_EP002_MSJ;
    }
	
	public static String ERROR_MANDATORIO_NULL_CODE; 
	@Value("${ERROR_EP003_COD}")	
	public void ERROR_EP003_COD(String ERROR_EP003_COD) {
		ERROR_MANDATORIO_NULL_CODE = ERROR_EP003_COD;
    }	
	public static String ERROR_MANDATORIO_NULL_MSJ; 
	@Value("${ERROR_EP003_MSJ}")	
	public void ERROR_EP003_MSJ(String ERROR_EP003_MSJ) {
		ERROR_MANDATORIO_NULL_MSJ = ERROR_EP003_MSJ;
    }
	
	public static String ERROR_DATO_INCORRECTO_CODE; 
	@Value("${ERROR_EP004_COD}")	
	public void ERROR_EP004_COD(String ERROR_EP004_COD) {
		ERROR_DATO_INCORRECTO_CODE = ERROR_EP004_COD;
    }	
	public static String ERROR_DATO_INCORRECTO_MSJ; 
	@Value("${ERROR_EP004_MSJ}")	
	public void ERROR_EP004_MSJ(String ERROR_EP004_MSJ) {
		ERROR_DATO_INCORRECTO_MSJ = ERROR_EP004_MSJ;
    }
	
	public static String ERROR_USER_EXISTE_CODE; 
	@Value("${ERROR_EP005_COD}")	
	public void ERROR_EP005_COD(String ERROR_EP005_COD) {
		ERROR_USER_EXISTE_CODE = ERROR_EP005_COD;
    }	
	public static String ERROR_USER_EXISTE_MSJ; 
	@Value("${ERROR_EP005_MSJ}")	
	public void ERROR_EP005_MSJ(String ERROR_EP005_MSJ) {
		ERROR_USER_EXISTE_MSJ = ERROR_EP005_MSJ;
    }
	
	public static String ERROR_USER_NO_EXISTE_CODE; 
	@Value("${ERROR_EP006_COD}")	
	public void ERROR_EP006_COD(String ERROR_EP006_COD) {
		ERROR_USER_NO_EXISTE_CODE = ERROR_EP006_COD;
    }	
	public static String ERROR_USER_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP006_MSJ}")	
	public void ERROR_EP006_MSJ(String ERROR_EP006_MSJ) {
		ERROR_USER_NO_EXISTE_MSJ = ERROR_EP006_MSJ;
    }
	
	public static String ERROR_CIA_NO_EXISTE_CODE; 
	@Value("${ERROR_EP007_COD}")	
	public void ERROR_EP007_COD(String ERROR_EP007_COD) {
		ERROR_CIA_NO_EXISTE_CODE = ERROR_EP007_COD;
    }	
	public static String ERROR_CIA_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP007_MSJ}")	
	public void ERROR_EP007_MSJ(String ERROR_EP007_MSJ) {
		ERROR_CIA_NO_EXISTE_MSJ = ERROR_EP007_MSJ;
    }
	
	public static String ERROR_IDIOMA_NO_EXISTE_CODE; 
	@Value("${ERROR_EP008_COD}")	
	public void ERROR_EP008_COD(String ERROR_EP008_COD) {
		ERROR_IDIOMA_NO_EXISTE_CODE = ERROR_EP008_COD;
    }	
	public static String ERROR_IDIOMA_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP008_MSJ}")	
	public void ERROR_EP008_MSJ(String ERROR_EP008_MSJ) {
		ERROR_IDIOMA_NO_EXISTE_MSJ = ERROR_EP008_MSJ;
    }
	
	public static String ERROR_HASHTAG_NO_EXISTE_CODE; 
	@Value("${ERROR_EP009_COD}")	
	public void ERROR_EP009_COD(String ERROR_EP009_COD) {
		ERROR_HASHTAG_NO_EXISTE_CODE = ERROR_EP009_COD;
    }	
	public static String ERROR_HASHTAG_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP009_MSJ}")	
	public void ERROR_EP009_MSJ(String ERROR_EP009_MSJ) {
		ERROR_HASHTAG_NO_EXISTE_MSJ = ERROR_EP009_MSJ;
    }
	
	public static String ERROR_PERFIL_NO_EXISTE_CODE; 
	@Value("${ERROR_EP010_COD}")	
	public void ERROR_EP010_COD(String ERROR_EP010_COD) {
		ERROR_PERFIL_NO_EXISTE_CODE = ERROR_EP010_COD;
    }	
	public static String ERROR_PERFIL_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP010_MSJ}")	
	public void ERROR_EP010_MSJ(String ERROR_EP010_MSJ) {
		ERROR_PERFIL_NO_EXISTE_MSJ = ERROR_EP010_MSJ;
    }
	
	public static String ERROR_HASHTAG_PUB_NO_EXISTE_CODE; 
	@Value("${ERROR_EP011_COD}")	
	public void ERROR_EP011_COD(String ERROR_EP011_COD) {
		ERROR_HASHTAG_PUB_NO_EXISTE_CODE = ERROR_EP011_COD;
    }	
	public static String ERROR_HASHTAG_PUB_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP011_MSJ}")	
	public void ERROR_EP011_MSJ(String ERROR_EP011_MSJ) {
		ERROR_HASHTAG_PUB_NO_EXISTE_MSJ = ERROR_EP011_MSJ;
    }
	
	public static String ERROR_PUBLICACION_NO_EXISTE_CODE; 
	@Value("${ERROR_EP012_COD}")	
	public void ERROR_EP012_COD(String ERROR_EP012_COD) {
		ERROR_PUBLICACION_NO_EXISTE_CODE = ERROR_EP012_COD;
    }	
	public static String ERROR_PUBLICACION_NO_EXISTE_MSJ; 
	@Value("${ERROR_EP012_MSJ}")	
	public void ERROR_EP012_MSJ(String ERROR_EP012_MSJ) {
		ERROR_PUBLICACION_NO_EXISTE_MSJ = ERROR_EP012_MSJ;
    }
	
	public static String ERROR_NOMBRE_NO_FOUND_CODE; 
	@Value("${ERROR_EP013_COD}")	
	public void ERROR_EP013_COD(String ERROR_EP013_COD) {
		ERROR_NOMBRE_NO_FOUND_CODE = ERROR_EP013_COD;
    }	
	public static String ERROR_NOMBRE_NO_FOUND_MSJ; 
	@Value("${ERROR_EP013_MSJ}")	
	public void ERROR_EP013_MSJ(String ERROR_EP013_MSJ) {
		ERROR_NOMBRE_NO_FOUND_MSJ = ERROR_EP013_MSJ;
    }
	
	public static String ERROR_CANDIDATO_NO_FOUND_CODE; 
	@Value("${ERROR_EP014_COD}")	
	public void ERROR_EP014_COD(String ERROR_EP014_COD) {
		ERROR_CANDIDATO_NO_FOUND_CODE = ERROR_EP014_COD;
    }	
	public static String ERROR_CANDIDATO_NO_FOUND_MSJ; 
	@Value("${ERROR_EP014_MSJ}")	
	public void ERROR_EP014_MSJ(String ERROR_EP014_MSJ) {
		ERROR_CANDIDATO_NO_FOUND_MSJ = ERROR_EP014_MSJ;
    }
	
	public static String ERROR_WS_PANDORA_CODE; 
	@Value("${ERROR_EP015_COD}")	
	public void ERROR_EP015_COD(String ERROR_EP015_COD) {
		ERROR_WS_PANDORA_CODE = ERROR_EP015_COD;
    }	
	public static String ERROR_WS_PANDORA_MSJ; 
	@Value("${ERROR_EP015_MSJ}")	
	public void ERROR_EP015_MSJ(String ERROR_EP015_MSJ) {
		ERROR_WS_PANDORA_MSJ = ERROR_EP015_MSJ;
    }
	
	public static String GET_FOTO_IMG_DEFAULT; 
	@Value("${FOTO_IMG_DEFAULT}")	
	public void FOTO_IMG_DEFAULT(String FOTO_IMG_DEFAULT) {
		GET_FOTO_IMG_DEFAULT = FOTO_IMG_DEFAULT;
    }
	
	public static String ERROR_CARGA_MASIVA_CODE; 
	@Value("${ERROR_EP016_COD}")	
	public void ERROR_EP016_COD(String ERROR_EP016_COD) {
		ERROR_CARGA_MASIVA_CODE = ERROR_EP016_COD;
    }
}