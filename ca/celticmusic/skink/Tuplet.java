// Tuplet.java
/********
 * handles tuplets (triplets, etc)
 * @author Wil Macaulay
 * @version 1.0 2 Mar 2002
 * history
 *      13 June 2002 support for generic tuplets 
 
p = the number of notes to be put into time q
q = the time that p notes will be played in
r = the number of notes to continue to do this action for.

If q is not specified, it defaults to 3 in compound time signatures and 2 
in simple time signatures. If r is not specified, it is taken to be the same as p.
 *     17 Sep 2002 1.0d1 handle extraneous (slurs, chords...) within triplet
 ***********/

package ca.celticmusic.skink;
import java.util.*;

public class Tuplet extends MultiElement{
   String tupletSpec = "";
   int numNotes;
   int numBeats;
   int timeOf;
   int notesFound=0;
	AbcElement firstAudibleElement;

  public Tuplet(){
      startDelim = "(";
      endDelim = "";
      numBeats = 0;
      timeOf = 0;
      }
      
  public String elementType(){
      return "tupl";
      }
/**
 * return the relative size of the symbol for layout purposes
 * overriden for tuplets, because chords are stacked
 */      
  public int glyphWidth(){     
	  int mySize = 0;
	  for (AbcElement theElement:theElements) {
		  mySize += theElement.glyphWidth();
	  }
	  return mySize;
  }
  /**
 * Get painting metrics 
 */
  public BarPosition getMetrics(AbcLineMetrics m,BarPosition bpCurrent){
		Debug.output(debugLevel, "Tuplet.getMetrics: "
				+this.toString() + " bpCurrent ="
				+bpCurrent);

	  BarPosition bpNew = new BarPosition(bpCurrent);
	  setBarPosition(bpNew);
	  for (AbcElement theElement:theElements) {
		  bpNew = theElement.getMetrics(m,bpNew);
	  }
	  return bpNew;
  }

   public void setTimeOf(String timeOfSet){
      timeOf =  Integer.parseInt(timeOfSet);
      }
      
   public void setNumBeats(String numBeatsSet){
      numBeats = Integer.parseInt(numBeatsSet);
      }
         
   public void setNumNotes(String numNotesSet){
      numNotes = Integer.parseInt(numNotesSet);
      startDelim += numNotesSet;
      }
   public void addElement(AbcElement theElement){
     Debug.output(3,"adding element");
     super.addElement(theElement);
     
     if (theElement.isAudible()){
        Debug.output(3,"audible element");
        notesFound++;
		  if (firstAudibleElement == null){
		     firstAudibleElement = theElement;
			  }
        }
     }   
   public boolean isFull(){
       Debug.output(3,"notes found: "+notesFound+" need: "+numNotes());
       return (notesFound >= numNotes());
      }

   public boolean isNotFull(){
      return (!isFull());
      }
  /**
   * returns the number of beats
   */
   public int numNotes(){
      if (numNotes == 0){
        numNotes = numBeats;
        }
      return numNotes;
      }
            
   public int beats(){
      return numBeats;
      }
            
 
   public double stretchFactor(){
      int beats = beats();
      int timeof = timeOf();
      return (double)timeof/(double)beats;
      }
      
   public int timeOf(){
      if (timeOf == 0){
         int beats = beats();   
         switch (beats){
            case 2:  // assume 2 in time of three
            case 4:
              timeOf = 3;
              break;
            case 3:  // triplet, 3 in time of 2
            case 6:
              timeOf = 2;
              break;
            case 5:
            case 7:
            case 9:
              timeOf = 3; //FIXME should be 2 if not compound meter
              break;
            default:
              timeOf = beats; // play it straight
              break;  
           }   
       }
         
       return timeOf;
       }       
   
/**
 * play the notes in a tuplet. Notes need to be 'stretched' so that
 * the eventual duration of the tuplet fits into the available length
 */      
   public int play(Player player,int atTick,double stretch,KeySig curKey){
      AbcElement theElement=null;
      int duration = 0;
      double myStretch = stretchFactor()*stretch;
      for (ListIterator i = theElements.listIterator(); i.hasNext(); ){
        theElement = (AbcElement) i.next();
        duration += theElement.play(player, atTick+duration, myStretch, curKey);
        }
      return duration;
      }
		
   public void paintMarkup(StaffPainter p){
	   // paint the tuplet marking
	   MusicElement m1 = (MusicElement)firstAudibleElement;
	   MusicElement m2 = (MusicElement)lastElement();

	   if ((m1 != null) && (m2 != null)){
		   double x1 = m1.getReferenceX();
		   double x2 = m2.getReferenceX() + StaffPanel.quarterSize;
		   double y1,y2;
		   if (m1.isTailUp()){
			   y1 = m1.getYmin() - StaffPanel.spaceHeight;
			   y2 = m2.getYmin() - StaffPanel.spaceHeight;
		   }
		   else{
			   y1 = m1.getYmax() + StaffPanel.spaceHeight;
			   y2 = m2.getYmax() + StaffPanel.spaceHeight;
		   }
		   p.paintTuplet(x1,y1,x2,y2,beats());
	   }	



	   // now handle slurs
	   for (ListIterator i = theElements.listIterator(); i.hasNext(); ){
		   AbcElement theElement = (AbcElement) i.next();
		   theElement.paintMarkup(p);
	   }
   }  
   public void updateLineContext(Line l){
	   for (ListIterator i = theElements.listIterator(); i.hasNext(); ){
		   AbcElement theElement = (AbcElement) i.next();
		   theElement.updateLineContext(l);
	   }
   }

   public void paint(StaffPainter p, boolean advance){
	   p.paintMusicElements(theElements,true);
   }  

}


