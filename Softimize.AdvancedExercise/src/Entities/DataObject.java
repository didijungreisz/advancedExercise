package Entities;

import java.util.HashMap;

public class DataObject {
	private HashMap<String, String> dataObject = new HashMap<String, String>();

	public void setDataObject(String field, String value) {
		dataObject.put(field , value);
	}
	
	public HashMap<String, String> getDataObject(){
		return dataObject;
	}
	
	
	

}
