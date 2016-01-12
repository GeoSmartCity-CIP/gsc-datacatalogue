package it.sinergis.datacatalogue.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.exception.DCException;

public class ServiceCommons {

	/** Logger. */
	private static Logger logger;
	
	/** Jackson object mapper. */
	ObjectMapper om;
	
	public ServiceCommons(){
		logger = Logger.getLogger(this.getClass());
		om = new ObjectMapper();
		//om.setPropertyNamingStrategy(new UpperCaseStrategy());
	}
	
	protected void checkJsonWellFormed(String jsonText) throws DCException {
		try {
			om.readTree(jsonText);
		} catch (JsonProcessingException e) {
			logger.error("Error: Json string is not well formed",e);
			throw new DCException("ER07");
		} catch (Exception e) {
			logger.error("Error checking if json is valid",e);
			DCException rpe = new DCException("ER01");
			logger.error("Datacatalog service: unhandled error "+rpe.returnErrorString());
			throw rpe;
		}
	}
	
	/**
	 * Creates the actual research query from the semplified input string given by the user.
	 * 
	 * @param text
	 * @return
	 */
	protected String createQuery(String text,String tableName,String columnName,String mode) {
		String query = "";
		if("delete".equalsIgnoreCase(mode)) {
			query += "delete from ";
		} else if("select".equalsIgnoreCase(mode)) {
			query += "select * from ";
		}
		query += tableName+" where ";
		
		try {
			String[] pieces = text.split("AND|OR");
			for(int i=0; i<pieces.length; i++) {
				int lastPieceElement = pieces[i].lastIndexOf("/");
				int firstBracketIndex = pieces[i].indexOf("(");

				String oldPiece = pieces[i];
				
				if(lastPieceElement != -1) {
					pieces[i] = pieces[i].substring(0,lastPieceElement).trim()+"->>"+pieces[i].substring(lastPieceElement+1).trim()+" ";
					if(firstBracketIndex != -1) {
						pieces[i] = " "+pieces[i].substring(0,firstBracketIndex+1).trim()+" "+columnName+"->"+pieces[i].substring(firstBracketIndex+1).trim()+" ";
					} else {
						pieces[i] = " "+columnName+"->"+pieces[i].trim()+" ";
					}
				} else {
					if(firstBracketIndex != -1) {
						pieces[i] = " "+pieces[i].substring(0,firstBracketIndex+1).trim()+" "+columnName+"->>"+pieces[i].substring(firstBracketIndex+1).trim()+" ";
					} else {
						pieces[i] = " "+columnName+"->>"+pieces[i].trim()+" ";
					}
				}
				pieces[i] = pieces[i].replace("/", "->");
				text = StringUtils.replace(text,oldPiece,pieces[i]);
			}
			query += text;
			logger.info("transformed query:"+ query);
			return query; 
		} catch(Exception e) {
			logger.error("Error",e);
			logger.error("Error in the research query: research queries must follow the following format: 'jsonNode'/'jsonChildNode'/.../'jsonRequestedNode' = 'requestedValue'");
			return null;
		}
	}

	/**
	 * JSON keys to lowercase. Useful to search key without a specific case of chars
	 * 
	 * @param text
	 * @return
	 */
	protected String keyToLowerCase(String json) {
		json = json.replaceAll("\\s","");
        Matcher m = Pattern.compile("\"\\b\\w{1,}\\b\"\\s*:").matcher(json);
        StringBuilder sanitizedJSON = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sanitizedJSON.append(json.substring(last, m.start()));
            sanitizedJSON.append(m.group(0).toLowerCase());
            last = m.end();
        }
        sanitizedJSON.append(json.substring(last));

        return sanitizedJSON.toString();
	}
	
	/**
	 * Returns value of a specific field
	 * 
	 * @param json
	 * @param fieldName 
	 * @return null if value is null, else the value
	 * @throws DPException
	 */
	protected String getFieldValueFromJsonText(String json,String fieldName) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode value = rootNode.findValue(fieldName);			
					
			if(value == null)
				return null;
			else
				return value.toString();
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException("ER01");
		}
	}
}
