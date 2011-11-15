/**
 *
 *  InfoField.java
 * 
 *     0.1   3-May-1999 WM 
 *
 **/
package ca.celticmusic.skink;

public class InfoField{
	String code  = null;
	String infoString = null;
	
	public InfoField(){
	}
	
	public InfoField(String theCode,String theInfoString){
		code = theCode;
		infoString = theInfoString;
	}
	// create the field directly from an input token (includes the colon)
	public InfoField(String fieldLine){
		Debug.output(4,"Info field: "+fieldLine);
		code = fieldLine.substring(0,1);
		infoString = fieldLine.substring(2);
		
	}
	public void setInfoString(String theString){
		infoString = theString;
	}
	
	public void setCode(String theCode){
		code = theCode;
	}
	
	public void dump(){
		System.out.println(code+":"+infoString);
	}
	
	public String getCode(){
		return code;
	}
	
	public String getInfoString(){
		return infoString;
	}
	
	public String asString(){
		String retString = code + ":" + infoString;
		return retString;
	}
  }

