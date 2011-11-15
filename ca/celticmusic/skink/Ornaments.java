/*******
 *
 * Ornaments.java - ornament a note 
 *           0.1 10 May 1999 WM
 *
 *******/
package ca.celticmusic.skink;
import java.util.*;
public class Ornaments
   {
   List<String> theOrnaments = new ArrayList<String>();
   List<GuitarChordElement> gcUp = new ArrayList<GuitarChordElement>();
   List<GuitarChordElement> gcDown = new ArrayList<GuitarChordElement>();
   List<GuitarChordElement> gcRight = new ArrayList<GuitarChordElement>();
   List<GuitarChordElement> gcLeft = new ArrayList<GuitarChordElement>();
   //String theChord = "";
   Grace theGraceNotes = null;
   String graceSymbol = "";

   public void addOrnament(String theOrnament)
      {
      theOrnaments.add(theOrnament);
      graceSymbol += theOrnament;
      }
      
   public List getOrnaments()
      {
      return theOrnaments;
      }   
   
   public void addGuitarChord(GuitarChordElement theChord)
   {
	   String gc=theChord.getContents();
	   char c = gc.charAt(0);
	   switch(c){
	   // below
	   case '_':
		   gcDown.add(theChord);
		   break;
	   // before
	   case '<':
		   gcLeft.add(theChord);
		   break;
	   // after
	   case '>':
		   gcRight.add(theChord);
		   break;
	   //  above
	   default:
		   gcUp.add(theChord);
	   break;
	   }   

   }

   

   public List<GuitarChordElement> getGuitarChordsUp(){
	   return gcUp;
   }
   public List<GuitarChordElement> getGuitarChordsDown(){
	   return gcDown;
   }
   public List<GuitarChordElement> getGuitarChordsLeft(){
	   return gcLeft;
   }
   public List<GuitarChordElement> getGuitarChordsRight(){
	   return gcRight;
   }

   public void setGraceNotes(Grace theG)
      {
      theGraceNotes = theG;
      }
      
   public Grace getGraceNotes()
      {
      return theGraceNotes;
      }   

   public String asString()
      {
      String theString = "";//theChord;
      if (theGraceNotes != null) theString += theGraceNotes.asString();
      theString += graceSymbol;
      return theString;
      }


   }

