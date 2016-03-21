package it.sinergis.datacatalogue.persistence.services.util;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;

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
	 */
	public String buildTextMessage(UUID uuid,Long rowId) {
		StringBuilder sb = new StringBuilder();
		sb.append("'http://");
		sb.append(mailPropertyReader.getValue(Constants.HOST_NAME));
		sb.append(":");
		sb.append(mailPropertyReader.getValue(Constants.PORT_NUMBER));
		sb.append("/gsc-datacatalogue/datacatalogservlet?actionName=verifymail&uuid=");
		sb.append(uuid);
		sb.append("&id=");
		sb.append(rowId);
		
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

	        InternetAddress from = new InternetAddress(mailPropertyReader.getValue(Constants.SENDER_ADDRESS));
	        Properties prop = new Properties();
	        prop.put(Constants.MAIL_SMTP_HOST,mailPropertyReader.getValue(Constants.MAIL_SMTP_HOST));
	        prop.put(Constants.MAIL_SMTP_PORT,mailPropertyReader.getValue(Constants.MAIL_SMTP_PORT));
	        
	        Session mailSession = Session.getDefaultInstance(prop);
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
            Transport.send(msg);           
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
