package com.vsm.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.opencsv.CSVReader;
import com.vsm.constant.MensajesConstante;
import com.vsm.constant.ServiciosConstante;
import com.vsm.dto.InPerfilDto;
import com.vsm.dto.OutPerfilDto;
import com.vsm.lib.dto.human.HuPandoraHashtagDto;
import com.vsm.lib.dto.human.HuPandoraPerfilPuestoDto;
import com.vsm.lib.dto.human.HuPndPerfilHastagDto;
import com.vsm.lib.utilitys.Utils;
import com.vsm.service.PndPerfilService;
import com.vsm.util.Utility;

@Service("perfilPandoraService")
public class PndPerfilImpl implements PndPerfilService {
	private static final Logger LOG = LogManager.getLogger(PndPerfilImpl.class.getName());
	Utils utils = new Utils();
	HuPandoraPerfilPuestoDto[] perfiles;
	HuPndPerfilHastagDto[] perfilesHash;
	File file;
	
	@Autowired
	RestTemplate template;

	@Autowired
	Utility utilService;
	
	@Override
	public OutPerfilDto save(HuPandoraPerfilPuestoDto huPandoraPerfil) {
		OutPerfilDto out = new OutPerfilDto();
		huPandoraPerfil.setFechaMov(LocalDateTime.now());
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(huPandoraPerfil.getAliasPerfil()==null||huPandoraPerfil.getAliasPerfil().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ALIAS PERFIL"));
		if(huPandoraPerfil.getNombrePerfil()==null||huPandoraPerfil.getNombrePerfil().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE PERFIL"));
		if(huPandoraPerfil.getNumCia()==null||huPandoraPerfil.getNumCia().longValue()==0)return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NUMERO COMPANIA"));
		if(huPandoraPerfil.getStatus()==null||huPandoraPerfil.getStatus().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		
		/**
		 * PERSISTE EL Perfil EN HU_PANDORA_PERFIL_PUESTO
		 */
		String servicio = ServiciosConstante.PAN_PERFILP_INSERT_SERVICE;
		String insertPerfil = "";
		try {
			insertPerfil = template.postForObject(servicio, huPandoraPerfil, String.class);
		}catch (Exception e) {
			LOG.error("PerfilPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(insertPerfil);
			return out;
		}
		
		out.setError(false);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPerfilDto update(HuPandoraPerfilPuestoDto huPandoraPerfil) {
		OutPerfilDto out = new OutPerfilDto();
		huPandoraPerfil.setFechaMov(LocalDateTime.now());
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */		
		if(huPandoraPerfil.getIdPerfilPuesto()==0)return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL PUESTO"));
		HuPandoraPerfilPuestoDto consulta = new HuPandoraPerfilPuestoDto();
		consulta.setIdPerfilPuesto(huPandoraPerfil.getIdPerfilPuesto());
		
		/**
		 * VALIDA QUE EL Perfil EXISTA EN BD
		 */
		String servicio = ServiciosConstante.PAN_PERFILP_SELECT_SERVICE;
		try {
			perfiles = template.postForObject(servicio, consulta,  HuPandoraPerfilPuestoDto[].class);
		}catch (Exception e) {
			LOG.error("PerfilPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(perfiles==null || perfiles.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
			return out;
		}
		
		/**
		 * PERSISTE EL Perfil EN HU_PANDORA_PERFIL_PUESTO
		 */
		servicio = ServiciosConstante.PAN_PERFILP_INSERT_SERVICE;
		String updtPerfil = "";

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		TypeMap<HuPandoraPerfilPuestoDto, HuPandoraPerfilPuestoDto> propertyMap = modelMapper.createTypeMap(HuPandoraPerfilPuestoDto.class, HuPandoraPerfilPuestoDto.class);
		propertyMap.setProvider(p -> perfiles[0]);			
		HuPandoraPerfilPuestoDto perfilNew =  modelMapper.map(huPandoraPerfil, HuPandoraPerfilPuestoDto.class);		
		try {
			updtPerfil = template.postForObject(servicio, perfilNew, String.class);
		}catch (Exception e) {
			LOG.error("PerfilPandoraImpl update error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!updtPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(updtPerfil);
			return out;
		}
		
		out.setError(false);
		out.setPerfil(perfilNew);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutPerfilDto listPerfil(HuPandoraPerfilPuestoDto huPandoraPerfil) {
		OutPerfilDto out = new OutPerfilDto();
		List<HuPndPerfilHastagDto> perfil = new ArrayList<HuPndPerfilHastagDto>();
			
		/**
		 * RECUPERA EL PERFIL DE BD
		 */
		String servicio = ServiciosConstante.PAN_PERFIL_HASHTAG_SELECT_SERVICE;
		try {
			perfilesHash = template.postForObject(servicio, huPandoraPerfil, HuPndPerfilHastagDto[].class);
		}catch (Exception e) {
			LOG.error("perfilPandoraImpl listperfil error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(perfilesHash==null || perfilesHash.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
			return out;
		}	
		perfil = Arrays.asList(perfilesHash);
		
		out.setError(false);
		out.setPerfilHashtag(perfil);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPerfilDto listAllPerfil() {
		OutPerfilDto out = new OutPerfilDto();
		List<HuPandoraPerfilPuestoDto> perfil = new ArrayList<HuPandoraPerfilPuestoDto>();
		
		/**
		 * RECUPERA EL PERFIL DE BD
		 */
		String servicio = ServiciosConstante.PAN_PERFILP_SELALL_SERVICE;
		try {
			perfiles = template.getForObject(servicio, HuPandoraPerfilPuestoDto[].class);
		}catch (Exception e) {
			LOG.error("perfilPandoraImpl listAllperfil error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(perfiles==null || perfiles.length==0) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_PERFIL_NO_EXISTE_CODE);
			out.setMessage(MensajesConstante.ERROR_PERFIL_NO_EXISTE_MSJ);
			return out;
		}	
		perfil = Arrays.asList(perfiles);
		
		out.setError(false);
		out.setPerfiles(perfil);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPerfilDto save(InPerfilDto in) {
		OutPerfilDto out = new OutPerfilDto();
		HuPandoraPerfilPuestoDto huPandoraPerfil = in.getPerfil();
		huPandoraPerfil.setFechaMov(LocalDateTime.now());
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(huPandoraPerfil.getAliasPerfil()==null||huPandoraPerfil.getAliasPerfil().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ALIAS PERFIL"));
		if(huPandoraPerfil.getNombrePerfil()==null||huPandoraPerfil.getNombrePerfil().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE PERFIL"));
		if(huPandoraPerfil.getNumCia()==null||huPandoraPerfil.getNumCia().longValue()==0)return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NUMERO COMPANIA"));
		if(huPandoraPerfil.getStatus()==null||huPandoraPerfil.getStatus().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		for(HuPandoraHashtagDto x : in.getPerfilHashtag()) {
			if(x.getNombre()==null||x.getNombre().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
			if(x.getStatus()==null||x.getStatus().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		}
		
		/**
		 * PERSISTE EL Perfil EN HU_PANDORA_PERFIL_PUESTO
		 */
		String servicio = ServiciosConstante.PAN_PERFILP_INSERT_SERVICE;
		String insertPerfil = "";
		try {
			insertPerfil = template.postForObject(servicio, huPandoraPerfil, String.class);
		}catch (Exception e) {
			LOG.error("PerfilPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(insertPerfil);
			return out;
		}

		/**
		 * RECUPERA EL PERFIL DE BD
		 */
		servicio = ServiciosConstante.PAN_PERFIL_HASHTAG_SELECT_SERVICE;
		try {
			perfilesHash = template.postForObject(servicio, huPandoraPerfil, HuPndPerfilHastagDto[].class);
		}catch (Exception e) {
			LOG.error("perfilPandoraImpl listperfil error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(perfilesHash==null || perfilesHash.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}	
		long idPerfil = perfilesHash[0].getIdPerfilPuesto();
		
		/**
		 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
		 */
		for(HuPandoraHashtagDto x : in.getPerfilHashtag()) {
			x.setIdPerfilPuesto(idPerfil);
		}
		servicio = ServiciosConstante.PAN_HASHTAG_INSALL_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, in.getPerfilHashtag(), String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPerfilImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertHstg.equalsIgnoreCase("save")) {
			out.setError(true);
			if(insertHstg.contains("HUMAN.FK_ID_PERFIL_PUESTO")) {
				out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(insertHstg);
				return out;
			}
		}
		
		out.setError(false);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}

	@Override
	public OutPerfilDto update(InPerfilDto in) {
		OutPerfilDto out = new OutPerfilDto();
		HuPandoraPerfilPuestoDto huPandoraPerfil = in.getPerfil();
		huPandoraPerfil.setFechaMov(LocalDateTime.now());
		
		/**
		 * VALICAIONES DE LOS DATOS DE ENTRADA
		 */
		if(huPandoraPerfil.getIdPerfilPuesto()==0)return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "ID PERFIL"));
		for(HuPandoraHashtagDto x : in.getPerfilHashtag()) {
			if(x.getNombre()==null||x.getNombre().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "NOMBRE"));
			if(x.getStatus()==null||x.getStatus().isBlank())return new OutPerfilDto(true,MensajesConstante.ERROR_MANDATORIO_NULL_CODE,MensajesConstante.ERROR_MANDATORIO_NULL_MSJ.replace("{nombreCampo}", "STATUS"));
		}

		/**
		 * RECUPERA EL PERFIL DE BD
		 */
		String servicio = ServiciosConstante.PAN_PERFIL_HASHTAG_SELECT_SERVICE;
		HuPandoraPerfilPuestoDto search = new HuPandoraPerfilPuestoDto();
		search.setIdPerfilPuesto(huPandoraPerfil.getIdPerfilPuesto());
		try {
			perfilesHash = template.postForObject(servicio, search, HuPndPerfilHastagDto[].class);
		}catch (Exception e) {
			LOG.error("perfilPandoraImpl listperfil error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
			return out;
		}				
		if(perfilesHash==null || perfilesHash.length==0) {
			out.setError(true);			
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}	
		long idPerfil = perfilesHash[0].getIdPerfilPuesto();
				
		/**
		 * PERSISTE EL PERFIL EN HU_PANDORA_PERFIL_PUESTO
		 */
		servicio = ServiciosConstante.PAN_PERFILP_INSERT_SERVICE;
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);	
		HuPandoraPerfilPuestoDto perfil =  modelMapper.map(perfilesHash[0], HuPandoraPerfilPuestoDto.class);
		TypeMap<HuPandoraPerfilPuestoDto, HuPandoraPerfilPuestoDto> propertyMap = modelMapper.createTypeMap(HuPandoraPerfilPuestoDto.class, HuPandoraPerfilPuestoDto.class);
		propertyMap.setProvider(p -> perfil);			
		HuPandoraPerfilPuestoDto perfilNew =  modelMapper.map(huPandoraPerfil, HuPandoraPerfilPuestoDto.class);
		String insertPerfil = "";
		try {
			insertPerfil = template.postForObject(servicio, perfilNew, String.class);
		}catch (Exception e) {
			LOG.error("PerfilPandoraImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertPerfil.equalsIgnoreCase("save")) {
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(insertPerfil);
			return out;
		}
		
		//ELIMINA LOS HASHTAGS ACTUALES
		if(perfilesHash==null || perfilesHash.length==0) {
			LOG.info("PndPublicacionImpl update error tace: " + MensajesConstante.ERROR_HASHTAG_PUB_NO_EXISTE_CODE);
		}else {
			List<HuPandoraHashtagDto> hashtagP = new ArrayList<HuPandoraHashtagDto>(perfilesHash[0].getHuPandoraHashtag());
			servicio = ServiciosConstante.PAN_HASHTAG_DELALL_SERVICE;
			String saveHashPub = "";
			try {
				saveHashPub = template.postForObject(servicio, hashtagP, String.class);
			}catch (Exception e) {
				LOG.error("PndPublicacionImpl update error tace: " + e.getMessage());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
			if(!saveHashPub.equalsIgnoreCase("save")) {
				LOG.error("PndPublicacionImpl update error tace: " + saveHashPub);
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}
		}
		
		/**
		 * PERSISTE EL HASHTAG EN HU_PANDORA_HASHTAG
		 */
		for(HuPandoraHashtagDto x : in.getPerfilHashtag()) {
			x.setIdPerfilPuesto(idPerfil);
		}
		servicio = ServiciosConstante.PAN_HASHTAG_INSALL_SERVICE;
		String insertHstg = "";
		try {
			insertHstg = template.postForObject(servicio, in.getPerfilHashtag(), String.class);
		}catch (Exception e) {
			LOG.error("PndHashtagPerfilImpl save error tace: " + e.getStackTrace());
			out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
			out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
			return out;
		}
		if(!insertHstg.equalsIgnoreCase("save")) {
			out.setError(true);
			if(insertHstg.contains("HUMAN.FK_ID_PERFIL_PUESTO")) {
				out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
				out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
				return out;
			}else {
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
				out.setMessage(insertHstg);
				return out;
			}
		}
		
		out.setError(false);
		out.setCodigo(MensajesConstante.SUCCES_CODE);
		out.setMessage(MensajesConstante.SUCCES_MSJ);
		return out; 
	}
	
	@Override
	public OutPerfilDto saveAll(InPerfilDto in) {
		OutPerfilDto out = new OutPerfilDto();
		List<InPerfilDto> inPerfilDto = new ArrayList<InPerfilDto>();		
		String fileName = "";
		String mensajeError = "";
		
		//CONVIERTE EL B64 A ARCHIVO
		String nombreArchivo = "";
    	Date date = new Date();   		
    	nombreArchivo = date.getTime() + (int)(Math.random()*30+1) + ".csv";    	
    	
    	//CREA EL ARCHIVO PDF
    	try {
	    	fileName = ServiciosConstante.GET_PANDORA_FILE_SYSTEM.replace("/{cia}/CURRICULUMS", "") + "/" + nombreArchivo;	    	
	    	file = new File(fileName);
    	} catch (Exception e) {
        	LOG.error("Error al desencriptar CV: " + e.getMessage());
        	file.delete();
        	out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_GENERAL);
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			file.delete();
			return out;
    	}
    	
    	//DESNCRIPTA CV BASE64 A PDF FILE
        try (FileOutputStream fos = new FileOutputStream(file); ) {
        	byte[] decoder = Base64.getDecoder().decode(in.getArchivoPerfiles());
        	fos.write(decoder);
        } catch (Exception e) {
        	LOG.error("Error al desencriptar CV: " + e.getMessage());        
        	out.setError(true);
			out.setCodigo(MensajesConstante.ERROR_GENERAL);
			out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
			file.delete();
        	return out;
        }
		
        //RECORRE EL ARCHIVO Y CREA UNA LISTA DEOBJETOS
        int iRow = 1;   
        try {	       
		    InputStream inp = new FileInputStream(file);
	        Workbook wb = WorkbookFactory.create(inp);
	        Sheet sh = wb.getSheetAt(0);
	        Row row = sh.getRow(iRow);	        
	        while(row!=null) {  
	        	try {
		            if(!row.getCell(0).getStringCellValue().toUpperCase().startsWith("NOMBRE DEL PERFIL")) {
		            	if(row.getCell(0).getStringCellValue().isBlank()||row.getCell(1).getStringCellValue().isBlank()) {
		        			throw new Exception("EL ARCHIVO NO CUMPLE CON LA ESTRUCTURA");
		        		}
	    	        	InPerfilDto reg = new InPerfilDto();
	    	        	HuPandoraPerfilPuestoDto x = new HuPandoraPerfilPuestoDto();
	    	        	x.setNombrePerfil(row.getCell(0).getStringCellValue());
	    	        	x.setAliasPerfil(row.getCell(1).getStringCellValue());
	    	        	x.setFechaMov(LocalDateTime.now());
	    	        	x.setNumCia(new BigDecimal(in.getNumCia()));
	    	        	x.setStatus("A");
	    	        	reg.setPerfil(x);
	    	        	String[] hashtags = row.getCell(2).getStringCellValue().split("#");
	    	        	List<HuPandoraHashtagDto> hashList = new ArrayList<HuPandoraHashtagDto>();
	    	        	for (String y : hashtags) {
	    	        		HuPandoraHashtagDto z = new HuPandoraHashtagDto();
	    	        		if(y!=null && !y.isBlank()) {
		    	        		z.setNombre("#" + y);
		    	        		z.setStatus("A");
		    	        		hashList.add(z);
	    	        		}
	    	        	}
	    	        	reg.setPerfilHashtag(hashList);
	    	        	inPerfilDto.add(reg);
		        	}
		            iRow++;  
		            row = sh.getRow(iRow);
		        }catch (Exception exc) {
		        	LOG.error("Error al leer el archivo XLSX: " + exc.getMessage());
		        	mensajeError += "El registro de la fila:" + (iRow+1) + " contiene errores y no se pude procesar favor de validarlo | ";
		        	iRow++;
		        	row = sh.getRow(iRow);
				}
	        }	        
        } catch (Exception e) {
        	LOG.error("Error al importar el archivo no es CSV: " + e.getMessage());
        	if(e.getMessage().contains("Your InputStream was neither an OLE2 stream")) {
        		try {
	    	        CSVReader csvReader = new CSVReader(new FileReader(fileName));
	    	        String[] fila = null;
	    	        while((fila = csvReader.readNext()) != null) {
	    	        	try {
		    	        	if(!fila[0].toUpperCase().startsWith("NOMBRE DEL PERFIL")) {
		    	        		if(fila[0].isBlank()||fila[1].isBlank()) {
		    	        			throw new Exception("EL ARCHIVO NO CUMPLE CON LA ESTRUCTURA");
		    	        		}
			    	        	InPerfilDto reg = new InPerfilDto();
			    	        	HuPandoraPerfilPuestoDto x = new HuPandoraPerfilPuestoDto();
			    	        	x.setNombrePerfil(fila[0]);
			    	        	x.setAliasPerfil(fila[1]);
			    	        	x.setFechaMov(LocalDateTime.now());
			    	        	x.setNumCia(new BigDecimal(in.getNumCia()));
			    	        	x.setStatus("A");
			    	        	reg.setPerfil(x);
			    	        	String[] hashtags = fila[2].split("#");
			    	        	List<HuPandoraHashtagDto> hashList = new ArrayList<HuPandoraHashtagDto>();
			    	        	for (String y : hashtags) {
			    	        		HuPandoraHashtagDto z = new HuPandoraHashtagDto();
			    	        		if(y!=null && !y.isBlank()) {
				    	        		z.setNombre("#" + y);
				    	        		z.setStatus("A");
				    	        		hashList.add(z);
			    	        		}
			    	        	}
			    	        	reg.setPerfilHashtag(hashList);
			    	        	inPerfilDto.add(reg);
			    	        	iRow++; 
		    	        	}
	    	        	} catch (Exception ex) {
	    	            	LOG.error("Error al leer el archivo CSV: " + ex.getMessage());
	    		        	mensajeError += "El registro de la fila:" + (iRow+1) + " contiene errores y no se pude procesar favor de validarlo | ";
	    		        	iRow++;
	    	            }
	    	        }
	    	        csvReader.close();
	    	        file.delete();
	            } catch (Exception ex) {
	            	LOG.error("Error al procesar el archivo CSV: " + ex.getMessage());
	            	file.delete();
		        	out.setError(true);
					out.setCodigo(MensajesConstante.ERROR_GENERAL);
					out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
		        	return out;
	            }		
        	}else {
        		LOG.error("Error al procesar el archivo XLSX: " + e.getMessage());
        		file.delete();
	        	out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_GENERAL);
				out.setMessage(MensajesConstante.DESCRIPCION_ERROR_GENERAL);
	        	return out;
        	}
        }
        file.delete();
        
        for(InPerfilDto a : inPerfilDto) {
			HuPandoraPerfilPuestoDto huPandoraPerfil = a.getPerfil();
			huPandoraPerfil.setFechaMov(LocalDateTime.now());
			
			/**
			 * PERSISTE EL Perfil EN HU_PANDORA_PERFIL_PUESTO
			 */
			String servicio = ServiciosConstante.PAN_PERFILP_INSERT_SERVICE;
			String insertPerfil = "";
			try {
				insertPerfil = template.postForObject(servicio, huPandoraPerfil, String.class);
			}catch (Exception e) {
				LOG.error("PerfilPandoraImpl save error tace: " + e.getStackTrace());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
				return out;
			}
			if(!insertPerfil.equalsIgnoreCase("save")) {
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
				out.setMessage(insertPerfil);
				return out;
			}
	
			/**
			 * RECUPERA EL PERFIL DE BD
			 */
			servicio = ServiciosConstante.PAN_PERFIL_HASHTAG_SELECT_SERVICE;
			try {
				perfilesHash = template.postForObject(servicio, huPandoraPerfil, HuPndPerfilHastagDto[].class);
			}catch (Exception e) {
				LOG.error("perfilPandoraImpl listperfil error tace: " + e.getStackTrace());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_SELECT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_SELECT_MSJ);
				return out;
			}				
			if(perfilesHash==null || perfilesHash.length==0) {
				out.setError(true);			
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
				return out;
			}	
			long idPerfil = perfilesHash[0].getIdPerfilPuesto();
			
			/**
			 * PERSISTE EL Hashtag EN HU_PANDORA_HASHTAG
			 */
			for(HuPandoraHashtagDto x : a.getPerfilHashtag()) {
				x.setIdPerfilPuesto(idPerfil);
			}
			servicio = ServiciosConstante.PAN_HASHTAG_INSALL_SERVICE;
			String insertHstg = "";
			try {
				insertHstg = template.postForObject(servicio, a.getPerfilHashtag(), String.class);
			}catch (Exception e) {
				LOG.error("PndHashtagPerfilImpl save error tace: " + e.getStackTrace());
				out.setError(true);
				out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);
				out.setMessage(MensajesConstante.ERROR_EXECUTE_INSERT_MSJ);
				return out;
			}
			if(!insertHstg.equalsIgnoreCase("save")) {
				out.setError(true);
				if(insertHstg.contains("HUMAN.FK_ID_PERFIL_PUESTO")) {
					out.setCodigo(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_CODE);			
					out.setMessage(MensajesConstante.ERROR_PUBLICACION_NO_EXISTE_MSJ);
					return out;
				}else {
					out.setCodigo(MensajesConstante.ERROR_EXECUTE_INSERT_CODE);			
					out.setMessage(insertHstg);
					return out;
				}
			}
        }
		
        if(mensajeError.isBlank()) {
        	out.setError(false);
        	out.setMessage(MensajesConstante.SUCCES_MSJ);
        	out.setCodigo(MensajesConstante.SUCCES_CODE);
        }else {
        	out.setError(true);
        	out.setMessage(MensajesConstante.ERROR_CARGA_MASIVA_CODE);
        	mensajeError = mensajeError.substring(0, mensajeError.length()-2);
        	out.setMessage(mensajeError);        	
        }						
		return out; 
	}
}