package com.vsm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsm.constant.ServiciosConstante;
import com.vsm.lib.dto.human.HuPandoraCandidatoDto;

@Service("utilityService")
public class Utility {
	private static final Logger LOG = LogManager.getLogger(Utility.class.getName());
	final String claveSecreta = "vsm_ms-pandora"; 
		
	@Autowired
	ComprimeImg comprimeService;
	
    public String encriptar(String datos) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = this.crearClave(claveSecreta);        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] datosEncriptar = datos.getBytes("UTF-8");
        byte[] bytesEncriptados = cipher.doFinal(datosEncriptar);
        String encriptado = Base64.getEncoder().encodeToString(bytesEncriptados);

        return encriptado;
    }

    public String desencriptar(String datosEncriptados) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = this.crearClave(claveSecreta);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);        
        byte[] bytesEncriptados = Base64.getDecoder().decode(datosEncriptados);
        byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
        String datos = new String(datosDesencriptados);        
        return datos;
    }
    
    private SecretKeySpec crearClave(String clave) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] claveEncriptacion = clave.getBytes("UTF-8");        
        MessageDigest sha = MessageDigest.getInstance("SHA-1");        
        claveEncriptacion = sha.digest(claveEncriptacion);
        claveEncriptacion = Arrays.copyOf(claveEncriptacion, 16);        
        SecretKeySpec secretKey = new SecretKeySpec(claveEncriptacion, "AES");
        return secretKey;
    }
    
    public HashMap<String,String> cvProcesado(String cvPdf, String nombreArc, long ciaNum, long idPub) throws Exception {
    	HashMap<String,String> out = new HashMap<String,String>();
    	String nombreArchivo = nombreArc.toUpperCase();
    	
    	//CREA EL DIRECTORIO    	
        File directorio = new File(ServiciosConstante.GET_PANDORA_FILE_SYSTEM.replace("{cia}", String.valueOf(ciaNum)) + "/" + idPub);
        if (!directorio.exists()) {
            if (!directorio.mkdirs()) {                
                LOG.error("Error al crear directorio");
            	return null;
            }
        }        
    	
    	//CREA EL ARCHIVO PDF
        String fileName = "";    	
    	fileName = ServiciosConstante.GET_PANDORA_FILE_SYSTEM.replace("{cia}", String.valueOf(ciaNum)) + "/" + idPub + "/" + nombreArchivo;	    	
    	if(!fileName.toUpperCase().endsWith(".PDF"))return null;	    	
    	File file = new File(fileName);
    	if (file.exists()) {
    		fileName = ServiciosConstante.GET_PANDORA_FILE_SYSTEM.replace("{cia}", String.valueOf(ciaNum)) + "/" + idPub + "/" + nombreArchivo.replace(".pdf", + (int)(Math.random()*300+1) + ".pdf");
    		file = new File(fileName);
    	}
    	LOG.info("CV: " + file.getPath());
    	out.put("filename", file.getName());
    	
    	//DESNCRIPTA CV BASE64 A PDF FILE
        try (FileOutputStream fos = new FileOutputStream(file); ) {
        	byte[] decoder = Base64.getDecoder().decode(cvPdf);
        	fos.write(decoder);
        } catch (Exception e) {
        	LOG.error("Error al desencriptar CV: " + e.getMessage());
        	file.delete();
        	return null;
        }
    	
        //OBTIENEN EL TEXTO DEL PDF
        String texto = "";
    	try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);
            texto = textStripper.getText(document);
        } catch (Exception e) {
        	LOG.error("Error al desencriptar CV: " + e.getMessage());
        	file.delete();
        	return null;
        } 
    	out.put("texto", texto);
    	    	
    	//OBTIENE LA FOTO
    	File fileOut = new File(fileName.replace(".PDF", ".png"));
        //OBTIENEN LA IMAGEN DEL PDF
        String encodedBase64 = "";
        try (PDDocument document = PDDocument.load(file)) {
            PDPageTree pages = document.getPages();
            for (PDPage pdfpage : pages) {
                PDResources pdResources = pdfpage.getResources();
                for (COSName cname : pdResources.getXObjectNames()) {
                    PDXObject object = pdResources.getXObject(cname);
                    if (object instanceof PDImageXObject) {                        
                        ImageIO.write(((PDImageXObject) object).getImage(), "png", fileOut);
                        comprimeService.copyImage(fileOut.getPath(),fileOut.getPath());
                        FileInputStream fileInputStreamReader = new FileInputStream(fileOut);
                        byte[] bytes = new byte[(int)fileOut.length()];                        
                        fileInputStreamReader.read(bytes);
                        String encodedBase64Actual = Base64.getEncoder().encodeToString(bytes);
                        if(encodedBase64Actual==null || encodedBase64Actual.length()>encodedBase64.length()) {
                        	encodedBase64 = encodedBase64Actual;
                        }
                        fileInputStreamReader.close();
                        //fileOut.delete();
                    }
                }
            }
        } catch (Exception e) {
        	LOG.error("Error al desencriptar CV: " + e.getMessage());
        	fileOut.delete();
        	return null;
        }  
        //fileOut.delete();
        out.put("foto", encodedBase64);    	
    	return out;
    }
    
    public String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
    
    public String clearAccents(String cadena) {
        String normalizado =null;
        if (cadena !=null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            normalizado = Normalizer.normalize(valor, Normalizer.Form.NFD);
            normalizado = normalizado.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            normalizado = Normalizer.normalize(normalizado, Normalizer.Form.NFC);
        }
        return normalizado;
    }
    
	public String getBodyRemenberPassHtml(String usuarioMail, String pasword) {		
		String text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
					  "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
					  "<head>" +
					  "	<meta name=\"viewport\" content=\"width=device-width\" />" +
					  "	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
					  "	<title>VenturesSoft</title>" +
					  "</head>" +
					  "<body style=\"margin:0px; background: #f8f8f8; \">" +
					  "	<div width=\"100%\" style=\"background: #f8f8f8; padding: 0px 0px; font-family:arial; line-height:28px; height:100%;  width: 100%; color: #514d6a;\">" +
					  "		<div style=\"max-width: 700px; padding:50px 0;  margin: 0px auto; font-size: 14px\">" + 
					  "			<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width: 100%; margin-bottom: 20px\">" +
					  "				<tbody>" +
					  "					<tr>" +
					  "						<td style=\"vertical-align: top; padding-bottom:30px;\" align=\"center\"> <img src=\"http://www.venturessoft.com.mx/wp-content/uploads/2016/10/VSM-Totem-620x116.png\" alt=\"admin Responsive web app kit\" style=\"border:none\" /> </td>" +
					  "					</tr>" +
					  "				</tbody>" +
					  "			</table>" +
					  "			<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width: 100%;\">" +
					  "				<tbody>" +
					  "					<tr>" +
					  "						<td style=\"background:#009999; padding:20px; color:#fff; text-align:center;\"> USTED SOLICITO EL PASSWORD PARA EL SIGUIENTE USUARIO </td>" +
					  "					</tr>" +
					  "				</tbody>" +
					  "			</table>" +
					  "			<div style=\"padding: 10px; background: #fff;\">" +
					  "				<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" style=\"width: 100%;\">" +
					  "					<tbody>" + 
					  "						<tr>" +
					  "							<td>" +
					  "								<p>Email usuario: <b>"+ usuarioMail + "."+"</b> <br> Password: <b>" + pasword + "</b></p>" +
					  "							</td>" +
					  "						</tr>" +
					  "					</tbody>" +
					  "				</table>" +
					  "			</div>" +
					  "			<div style=\"text-align: center; font-size: 12px; color: #b2b2b5; margin-top: 20px\">" +
					  "				<p> Información Propietaria de VenturesSoft de México S.A. de C.V. Todos los Derechos Reservados, 2016<br /> <a href=\"contacto@venturessoft.com.mx\" style=\"color: #b2b2b5; text-decoration: underline;\">Para mayor información mándanos un correo a: contacto@venturessoft.com.mx</a> </p>" +
					  "			</div>" +
					  "		</div>" +
					  "	</div>" +
					  "</body>" +
					  "</html>";		
		return text;
	}
	
	public String getBodyCandidatosHtml(String nombreUsuario, String nombrePublicacion, List<HuPandoraCandidatoDto> candidatosSel, long numCia, long idPub) {
		String tabla = "";
		
		for(HuPandoraCandidatoDto x : candidatosSel) {
			long orden = x.getOrden(); 
			tabla += "<tr>      " +
					 "                  <td align=\"center\" bgcolor=\"#ffffff\"      " +
					 "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
					 "                     " + orden + "      " +
					 "                  </td>      " +
					 "                       " +
					 "                  <td align=\"center\" bgcolor=\"#ffffff\"      " +
					 "                  style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
					 "                  " + x.getNombreCandidato() + "      " +
					 "               	</td>      " +
					 "                  <td align=\"center\" bgcolor=\"#ffffff\"      " +
					 "                  style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
					 "                  " + x.getNombreCv() + "      " +
					 "               	</td>      " +
					 "                  <td align=\"center\" bgcolor=\"#ffffff\"      " +
					 "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
					 "                     <a href=\"" + ServiciosConstante.GET_PANDORA_PDF_RESOLVER + "Pandora?pdfFile=" + x.getNombreCv() + "&ciaPandora=" + numCia + "&idPublicacion=" + idPub + "\"      " +
					 "                        target=\"_blank\">Ver Curriculum</a>      " +
					 "                  </td>      " +
					 "      " +
					 "               </tr>"; 
		}		
		
		String text = "<!DOCTYPE html>      " +
				      "<html>      " +
				      "      " +
				      "<head>      " +
				      "   <meta charset=\"utf-8\">      " +
				      "   <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">      " +
				      "   <title>Incidentes VenturesSoft</title>      " +
				      "   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">      " +
				      "   <style type=\"text/css\">      " +
				      "      /**    * Google webfonts. Recommended to include the .woff version for cross-client compatibility.    */      " +
				      "      @media screen {      " +
				      "         @font-face {      " +
				      "            font-family: 'Source Sans Pro';      " +
				      "            font-style: normal;      " +
				      "            font-weight: 400;      " +
				      "            src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');      " +
				      "         }      " +
				      "      " +
				      "         @font-face {      " +
				      "            font-family: 'Source Sans Pro';      " +
				      "            font-style: normal;      " +
				      "            font-weight: 700;      " +
				      "            src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');      " +
				      "         }      " +
				      "      }      " +
				      "      " +
				      "      img {      " +
				      "         border-radius: 50%;      " +
				      "         width: 50px;      " +
				      "      }      " +
				      "      " +
				      "      /**    * Avoid browser level font resizing.    * 1. Windows Mobile    * 2. iOS / OSX    */      " +
				      "      body,      " +
				      "      table,      " +
				      "      td,      " +
				      "      a {      " +
				      "         -ms-text-size-adjust: 100%;      " +
				      "         /* 1 */      " +
				      "         -webkit-text-size-adjust: 100%;      " +
				      "         /* 2 */      " +
				      "      }      " +
				      "      " +
				      "      /**    * Remove extra space added to tables and cells in Outlook.    */      " +
				      "      table,      " +
				      "      td {      " +
				      "         mso-table-rspace: 0pt;      " +
				      "         mso-table-lspace: 0pt;      " +
				      "      }      " +
				      "      " +
				      "      /**    * Better fluid images in Internet Explorer.    */      " +
				      "      img {      " +
				      "         -ms-interpolation-mode: bicubic;      " +
				      "      }      " +
				      "      " +
				      "      " +
				      "      /**    * Remove blue links for iOS devices.    */      " +
				      "      a[x-apple-data-detectors] {      " +
				      "         font-family: inherit !important;      " +
				      "         font-size: inherit !important;      " +
				      "         font-weight: inherit !important;      " +
				      "         line-height: inherit !important;      " +
				      "         color: inherit !important;      " +
				      "         text-decoration: none !important;      " +
				      "      }      " +
				      "      " +
				      "      /**    * Fix centering issues in Android 4.4.    */      " +
				      "      div[style*=\"margin: 16px 0;\"] {      " +
				      "         margin: 0 !important;      " +
				      "      }      " +
				      "      " +
				      "      body {      " +
				      "         width: 100% !important;      " +
				      "         height: 100% !important;      " +
				      "         padding: 0 !important;      " +
				      "         margin: 0 !important;      " +
				      "      }      " +
				      "      " +
				      "      /**    * Collapse table borders to avoid space between cells.    */      " +
				      "      table {      " +
				      "         border-collapse: collapse !important;      " +
				      "      }      " +
				      "      " +
				      "      a {      " +
				      "         color: #1a82e2;      " +
				      "      }      " +
				      "      " +
				      "      img {      " +
				      "         height: auto;      " +
				      "         line-height: 100%;      " +
				      "         text-decoration: none;      " +
				      "         border: 0;      " +
				      "         outline: none;      " +
				      "      }      " +
				      "   </style>      " +
				      "</head>      " +
				      "      " +
				      "<body style=\"background-color: #e9ecef;\">      " +
				      "   <div class=\"preheader\"      " +
				      "      style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 1; \">      " +
				      "   </div>      " +
				      "   <!-- /Brand logo-->      " +
				      "   <table border=\"0\" cellpadding=\"0\" cellspacing=\"10\" width=\"100%\">      " +
				      "      <tr>      " +
				      "         <td align=\"center\" bgcolor=\"#e9ecef\">      " +
				      "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 800px;\">      " +
				      "               <tr>      " +
				      "                  <td align=\"left\" bgcolor=\"#ffffff\"      " +
				      "                     style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">      " +
				      "                     <h1      " +
				      "                        style=\"color:rgb(0, 154, 154);margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">      " +
				      "                        Relación de Ordenamiento de Candidatos</h1>      " +
				      "                  </td>      " +
				      "               </tr>      " +
				      "            </table>      " +
				      "         </td>      " +
				      "      </tr>      " +
				      "      <tr>      " +
				      "         <td align=\"center\" bgcolor=\"#e9ecef\">      " +
				      "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 800px;\">      " +
				      "               <!-- start copy -->      " +
				      "               <tr>      " +
				      "                  <td align=\"left\" bgcolor=\"#ffffff\"      " +
				      "                     style=\"padding: 20px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">      " +
				      "                     <p style=\"margin: 0;\">¡ Hola " + nombreUsuario + " !</p>      " +
				      "                     <br>      " +
				      "                     <p style=\"margin: 0;\">A continuación encontrará la relación de los candidatos seleccionados      " +
				      "                        para la publicación:</b>      " +
				      "                     <p style=\"margin: 0;padding-top: 15px;\"><b> " + nombrePublicacion + "</b>      " +
				      "                  </td>      " +
				      "               </tr>      " +
				      "            </table>      " +
				      "            <table border=\"2\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 800px \">      " +
				      "      " +
				      "               <tr>      " +
				      "                  <th align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
				      "                     No.</th>      " +
				      "                  <th align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
				      "                     Candidato</th>      " +
				      "                  <th align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
				      "                     Curriculums</th>      " +
				      "                  <th align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                     style=\"padding: 5px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 15; line-height: 14px;\">      " +
				      "                  </th>      " + tabla +
				      "                     " +
				      "      </tr>      " +
				      "   </table>      " +
				      "   </td>      " +
				      "   </tr>      " +
				      "   <tr>      " +
				      "      <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">      " +
				      "         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">      " +
				      "            <tr>      " +
				      "               <td align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                  style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">      " +
				      "                  <p style=\"margin: 0;\">Información Propietaria de VenturesSoft de México S.A. de C.V.</p>      " +
				      "                  <p style=\"margin: 0;\">Copyright © 2022 VenturesSoft de México S.A de C.V., Todos los derechos      " +
				      "                     reservados.</p>      " +
				      "               </td>      " +
				      "            </tr>      " +
				      "            <tr>      " +
				      "               <td align=\"center\" bgcolor=\"#e9ecef\"      " +
				      "                  style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">      " +
				      "                  <p style=\"margin: 0;\">Para mayor información mándanos un correo:<a href=\"contacto@venturessoft.com.mx\"      " +
				      "                        target=\"_blank\">      " +
				      "                        <br>contacto@venturessoft.com.mx</a>      " +
				      "                  </p>      " +
				      "               </td>      " +
				      "            </tr>      " +
				      "         </table>      " +
				      "      </td>      " +
				      "   </tr>      " +
				      "   </table>      " +
				      "</body>      " +
				      "      " +
				      "</html>";		
		return text;
	}
}