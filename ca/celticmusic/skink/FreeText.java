/**
 *
 *  FreeText.java - free text line
 * 
 *     0.1   12 May 1999 WM  
 *
 **/
 package ca.celticmusic.skink;
import java.util.*;

public class FreeText
  {
  Vector lines    = new Vector();

  public void newLine(String theLine)
     {
     lines.addElement(theLine);
     }
   
  public void dump()
     {
     dumpLines();
     }

  public void dumpLines()
     {

     String theLine;
     Enumeration lineList = lines.elements();
     while (lineList.hasMoreElements())
	 {
         theLine = (String) lineList.nextElement();
         System.out.println(theLine);	
	 }
      }


  }

