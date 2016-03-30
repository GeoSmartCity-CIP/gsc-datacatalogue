package it.sinergis.datacatalogue.persistence.services.util;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.common.PropertyReader;
import it.sinergis.datacatalogue.exception.DCException;

public class MailUtils {
	
	/** Logger. */
	private static Logger logger;
	
	/** Mail property reader. */
	private PropertyReader mailPropertyReader;
	
	public MailUtils() {
		logger = Logger.getLogger(this.getClass());
		mailPropertyReader = new PropertyReader("mail.properties");
	}
	
	/**
	 * Builds the registration confirmation text.
	 * 
	 * @param uuid
	 * @param rowId
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String buildTextMessage(UUID uuid,Long rowId) throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		sb.append("'http://");
		sb.append(mailPropertyReader.getValue(Constants.HOST_NAME));
		sb.append(":");
		sb.append(mailPropertyReader.getValue(Constants.PORT_NUMBER));
		sb.append("/gsc-datacatalogue/datacatalogservlet?actionName=verifymail&request=");
//		sb.append("/gsc-datacatalogue/datacatalogservlet?actionName=verifymail&uuid=");
//		sb.append(uuid);
//		sb.append("&id=");
//		sb.append(rowId);
		
		ObjectNode rootNode =  JsonNodeFactory.instance.objectNode();
		rootNode.put(Constants.ID,rowId);
		rootNode.put(Constants.UUID,uuid.toString());
		ObjectMapper om = new ObjectMapper();
		String jsonRequest = om.writeValueAsString(rootNode);

		sb.append(jsonRequest);
		
		String link = sb.toString();
		logger.debug("generated verification link = "+link);
		
		sb = new StringBuilder();
		
		sb.append("To complete registration click the following link: <a href=");
		sb.append(link);
		sb.append("'>Complete registration</a>");

		return sb.toString();
				
	}
	
	/**
	 * Builds the mail message.
	 * 
	 * @param multipart
	 * @param subject
	 * @param text
	 * @param address
	 * @return
	 * @throws DCException
	 */
	public MimeMessage buildMimeMessage(Multipart multipart, String subject, String text, String address) throws DCException {
        
		try {

	        InternetAddress from = new InternetAddress(mailPropertyReader.getValue(Constants.SENDER_MAIL_ADDRESS));
	        Properties prop = new Properties();

	        Session mailSession = null;
	        prop.put(Constants.MAIL_SMTP_HOST,mailPropertyReader.getValue(Constants.MAIL_SMTP_HOST));
    		prop.put(Constants.MAIL_SMTP_PORT,mailPropertyReader.getValue(Constants.MAIL_SMTP_PORT));
    		prop.put(Constants.SMTP_AUTH, "true");
    		prop.put("mail.smtp.starttls.enable", "true");
    		prop.put("mail.smtp.socketFactory.port", mailPropertyReader.getValue(Constants.MAIL_SMTP_PORT));
			prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			prop.put("mail.smtp.socketFactory.fallback", "false");
			mailSession = Session.getInstance(prop,new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailPropertyReader.getValue(Constants.SENDER_MAIL_ADDRESS),mailPropertyReader.getValue(Constants.SENDER_MAIL_PASSWORD));
				}
			});
	        //prop.put(Constants.MAIL_SMTP_HOST,mailPropertyReader.getValue(Constants.MAIL_SMTP_HOST));
	        //prop.put(Constants.MAIL_SMTP_PORT,mailPropertyReader.getValue(Constants.MAIL_SMTP_PORT));
	        
	        //Session mailSession = Session.getDefaultInstance(prop);
	        MimeMessage msg = new MimeMessage(mailSession);
	        
	        msg.setSubject(subject);
	        msg.setFrom(from);
	        msg.setSender(from);
	        msg.setSentDate(new Date());
	        
	        msg.addRecipient(RecipientType.TO, new InternetAddress(address));
	        
	        BodyPart bodyPartText = new MimeBodyPart();
	        
	        ((MimeBodyPart) bodyPartText).setText(text,"UTF-8", "html");
	        multipart.addBodyPart(bodyPartText);
	        
	        return msg;
		} catch (Exception e) {
			logger.error("Error building mime message", e);
            throw new DCException(Constants.ER01);
		}
    }
	
	/**
	 * Sends a mail.
	 * 
	 * @param subject
	 * @param text
	 * @param address
	 * @throws DCException
	 */
	public void sendMail(String subject, String text, String address)
            throws DCException {
        try {  
            Multipart multipart = new MimeMultipart();
            MimeMessage msg = buildMimeMessage(multipart, subject, text, address);        
            msg.setContent(multipart);            
            //Transport.send(msg);    
            Transport.send(msg,msg.getRecipients(Message.RecipientType.TO));
//            Transport.send(msg,msg.getRecipients(Message.RecipientType.TO),mailPropertyReader.getValue(Constants.SENDER_MAIL_ADDRESS), mailPropertyReader.getValue(Constants.SENDER_MAIL_PASSWORD));
        }
        catch (DCException dce) {
            throw dce;
        } catch(javax.mail.SendFailedException e) {
        	if(e.getMessage().equalsIgnoreCase("Invalid addresses")) {
        		logger.error("Registration unsuccessful: the specified email address does not exist.", e);
                throw new DCException(Constants.ER17);
        	}
			logger.error("registration unsuccessful: error while sending verification mail.", e);
            throw new DCException(Constants.ER18);
		}
        catch (Exception e) {
            logger.error("Error sending mail", e);
            throw new DCException(Constants.ER01);
        }
    }
}
