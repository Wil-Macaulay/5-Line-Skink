//////////////
//
//  handle beaming
//      May 24 2000 WM
//      Dec 28 2001 WM - handle case where the tail direction is preset (bagpipe)
//      1.1a2 28 Sep 2003 WM - maxslope on beams, fixed beam position for HP
//      1.1f2 12 May 2004 WM - fix tails on chords
//      1.2a0 9 June 2004 WM - use doubles instead of ints for x, y positions
//
////////
package ca.celticmusic.skink;
import java.util.*;
//import java.awt.*;
 public class Beamer
    {
    List<BeamEntry> theNotes = null;
    double yMid;
    double maxY = -1;
    double maxX = -1;
    double minY = -1;
    double minX = -1;
    double firstY = -1;
    double lastY = -1;
    double firstX = -1;
    double lastX = -1;
    int minNumTails = -1;       // minimum number of tails for a note in the beam
    int maxNumTails = -1;       // maximum number of tails for a note in the beam
    int nUp =0;
    int nDown = 0;
    double headSize = StaffPanel.quarterSize;
    int beamLength = 0;         // length in 64th notes of the whole beam
    boolean empty = false;
    // style parameters
    boolean autoTail;     // tail orientation set automatically
    boolean tailsUp;            // if constant, tail is up
    boolean isGrace = false;      // beamer for gracenotes, so use thinner beams
    double maxSlope =  0.2;      // maximum slope for beam
    double beamSeparation = 3;     // how far apart the beams are 
    double tailHeight = StaffPanel.tailHeight;
	double maxDeltaY = StaffPanel.spaceHeight * 1.5;
    
    
    // new Beamer
    public Beamer() {
       empty = true;
       }
    
    
    // init if a single voice, auto set tails
    public void init(double mid)  {
       Debug.output(1,"initializing Beam");
       empty = true;
       yMid = mid;
       nUp = nDown = 0;
       beamLength = 0;
       minNumTails = 0;
       theNotes = new ArrayList<BeamEntry>();
       autoTail=true;
       }
    public void setMaxSlope(double slope){
       maxSlope = slope;
       }
       
    // if this voice has a constant tail direction, or bagpipe music
    public void init(double mid, boolean autoTail, boolean tailUp)
       {
       Debug.output(1,"init Beam with autoTail");
       init(mid);
       this.autoTail = autoTail;
       tailsUp = tailUp;
       
       }
       
    public void addNote(MusicElement elem,double x,double y) {
	   BeamEntry b;
       Debug.output(1,"adding note at "+x+","+y); 
	   
       if (y >= yMid) 
          nUp++;
       else
          nDown++;   
       if (y < minY) {
          minY = y;
          minX = x;
          }
       if (y > maxY)  {
          maxY = y;
          maxX = x;
          }
	   // adding a note at the same x position as last time - must be a chord, so update y 
	   if (x == lastX) {  
	      b = (BeamEntry)theNotes.get(theNotes.size()-1);
		  b.updateY(y);
		  lastY = y;
          lastX = x;
	      return;
		  }
	   else {	  
		  b =  new BeamEntry(elem,beamLength,x, y) ; 
		  }    
       lastY = y;
       lastX = x;

       if (empty)  {
           firstY = y;
           firstX = x;
           minY = y;
           minX = x;
           maxX = x;
           maxY = y;
           minNumTails = b.numTails();
           maxNumTails = b.numTails();
           empty = false;
           }
       else{ // make sure the previous element knows where this is 
           ((BeamEntry)theNotes.get(theNotes.size()-1)).setXNext(x);
           } 
       theNotes.add(b);
       beamLength += elem.getAbsLength();
       if (b.numTails() < minNumTails) minNumTails = b.numTails();
       if (b.numTails() > maxNumTails) maxNumTails = b.numTails();   
       }
       
    // check whether tails go up or down for this beam   
    public boolean tailsUp()
       {
       if (!autoTail)
          return tailsUp;
       else
          {
          if (nUp >= nDown)
             return true;
          else
             return false;
          }
       }
   // draw a single note (no beam)    
   protected void drawSingleNote(Renderer r, boolean tailUp){       
        BeamEntry b = (BeamEntry) theNotes.get(0);
        r.paintTail(b.xPos,b.yPos(tailUp),tailHeight+((b.numTails()-1)*beamSeparation),tailUp,b.numTails(),headSize,isGrace);
        b.getMusicElement().setReferencePosition(b.xPos,b.yPos(tailUp),tailUp,tailHeight+((b.numTails()-1)*beamSeparation));
		}
        
   protected double getBeamY(){
       double yy;
       if (maxSlope == 0.0){
          if (tailsUp()){
             yy = yMid - 5*StaffPanel.spaceHeight; // grace notes for Hp go up
             }
          else{
             yy = yMid + 4*StaffPanel.spaceHeight; // melody notes go down
             }
           }  
       else{     
          if (tailsUp()){
             yy = minY - (tailHeight+(maxNumTails-1)*beamSeparation) + StaffPanel.spaceHeight/2 ; // y coord 
             }
          else{   
             yy = maxY+(tailHeight+(maxNumTails-1)*beamSeparation)  +StaffPanel.spaceHeight/2;
             }
          }   
        return yy;   
        } 
           
    
    // end the beam and draw the notes                                          
    public void endBeam(Renderer r) {
       if (empty) return;
       Debug.output(1,"Ending Beam");
       Debug.output(1,"max at "+maxX+","+maxY);
       Debug.output(1,"min at "+minX+","+minY);
       // first calculate the slope - 
       double deltaX = lastX - firstX;
       double deltaY = lastY - firstY;
       // now normalize so slope is not too steep

       double slope = (double)deltaY/(double)deltaX;
       if (slope > maxSlope) deltaY = (int)((double)deltaX * maxSlope);
       if (slope < -maxSlope) deltaY = -(int)((double)deltaX * maxSlope);
	   if (deltaY > maxDeltaY) deltaY = (int)maxDeltaY;
	   if (deltaY < -maxDeltaY) deltaY = -(int)maxDeltaY;
       /* for now, the algorithm is:
               - slope is from first to last, normalized to maxSlope
               - direction of tail is the according to the majority of the notes
               - addElement length to ensure minimum tail length is met
               FIXME - not quite right, because the highest note
               could be at the start, so the beam crosses a lower
               note which is above the line from high to low
                
       */
       
       
       boolean tailUp = false;
       double xOffset,yOffset; 
       double xx,yy;   
        
      if (tailsUp())  {
           tailUp = true;
           xx = minX;            // x coord of the smallest tail (highest note in beam)
           yy = getBeamY();
           xOffset = headSize; // because tail is behind
           yOffset = 4;
           }
      else {
            tailUp = false;
            xx = maxX;
            yy = getBeamY();
            xOffset = 0;   // because the tail is at the front of the note
            yOffset = -4;
            }
            
      Debug.output(2,"tails go up?"+tailUp); 
      // check for single note in 'beam'
       if ((nUp+nDown) == 1 ) {
           drawSingleNote(r, tailUp);
           return;
           }
       // draw the minimum number of tails for all elements of the beam
 
       // check for only chord in beam
       if (deltaX == 0)  {
           yOffset *= -1;
           BeamEntry b = (BeamEntry) theNotes.get(0); // assume all notes in the chord have the same length
		   // chord=>need to set all notes, not just one - we'll set the first for now, and make sure that
		   // MultiElement gets its info from the first FIXME this still isn't quite right, because chords in the
		   // middle of the beam will not have the total tail length in first position
		   if (tailUp){
		      yy = maxY;
			  }
		   else{ 
		      yy = minY;
			  }
			  	  
		   r.paintTail(b.xPos,yy,
					   tailHeight+maxY-minY+((b.numTails()-1)*beamSeparation),
					   tailUp,b.numTails(),headSize,isGrace); 
		   b.getMusicElement().setReferencePosition(b.xPos,b.yPos(tailUp),
		                                            tailUp,
													tailHeight+maxY-minY+((b.numTails()-1)*beamSeparation));
				  
           }
       else{      
            // so we make a beam from firstX+quarterSize to lastX+quarterSize
            double y1 = yy - (deltaY*(xx-firstX)/deltaX);  // start beam
            double yn = y1 + (deltaY*(lastX-firstX)/deltaX); // end beam
            
            // draw as many complete beams as we need to
            for (int i = 0; i<minNumTails ; i++)  {
                r.paintBeam((firstX+xOffset),(i*yOffset)+y1,lastX+xOffset,(i*yOffset)+yn,isGrace);
                }
            
            double xPrev = firstX;  // keep track of previous x position
            // 
            for (ListIterator lI = theNotes.listIterator() ; lI.hasNext();){
                BeamEntry b = (BeamEntry)lI.next();
                Debug.output(1,"note at "+b.xPos+","+b.yPos(tailUp)+" pulse: "
                                +b.beamPulse + "next at" + b.xNext);
                // draw the riser/descender
                double yTop = y1 + (deltaY*(b.xPos-firstX)/deltaX);
                double abslen = ((yTop-b.yPos(tailUp)) > 0) ?(yTop-b.yPos(tailUp)) : (b.yPos(tailUp) - yTop);
                r.paintTail(b.xPos,b.yPos(tailUp),abslen,tailUp,0,headSize,isGrace);
				b.getMusicElement().setReferencePosition(b.xPos,b.yPos(tailUp),tailUp,abslen);

                if (b.numTails() > minNumTails  ) {
                    
                        /* 'extra' beam for smaller notes than min number of tails
                            - back if at end
                            - forward if at beginning
                            - back toward previous if minor pulse
                            - forward if major pulse - where major pulse is a 'one' for the length
                        */
                        
                        int fwdBack;
                        int len = b.getAbsLength();
                        if ((b.beamPulse + len) == beamLength) // last pulse 
                            {
                            Debug.output(1,"last pulse");
                            fwdBack = 1;  // point back
                            }
                        else {
                            int numPulse = beamLength/len;
                            int pulsePos = b.beamPulse /len;
                            fwdBack = pulsePos %2;
                            Debug.output(1,"numpulse " + numPulse + " pulse position = "+pulsePos+" fwdBack "+fwdBack);
                            }
                        for (int i = minNumTails; i < b.numTails() ; i++) {
                            if (fwdBack == 0)  { // beam forward
                                Debug.output(1,"drawing beam forward");
                                r.paintBeam(b.xPos+xOffset,
                                            yTop+yOffset*i,
                                            (b.xNext+b.xPos)/2+xOffset,
                                            yTop+yOffset*i+(deltaY*((b.xNext-b.xPos)/2)/deltaX),isGrace);
                                } 
                            else             // beam back
                                { 
                                Debug.output(1,"drawing beam backward");
                                r.paintBeam(b.xPos+xOffset,
                                            yTop+yOffset*i,
                                            (xPrev+b.xPos)/2+xOffset,
                                            yTop+yOffset*i+(deltaY*((xPrev-b.xPos)/2)/deltaX),isGrace);
                                }    
                            } 
                        }    
                        xPrev = b.xPos;     
                }
          }
 
     }  
       
     
   
    protected class BeamEntry
       {
       //int len;
       double yPosMin;
	   double yPosMax;
       double xPos;
       double xNext;      // the next note's x position
       int beamPulse;  // what pulse are we in the beam?
	   MusicElement theElement;
       
       public BeamEntry(MusicElement m, int bp, double x, double y) {
          theElement = m;
		  
          xPos = x;
          yPosMin = yPosMax = y;
          beamPulse = bp;  
          }
       public void setXNext(double x){
          xNext = x;
          }
       public void updateY(double y) {
	      if (y > yPosMax) yPosMax = y;
		  if (y < yPosMin) yPosMin = y;
		  }
	   // if tails are up, return the max Y, else the min
	   public double yPos(boolean tailsUp){	  
	      if (tailsUp) return yPosMax;
		  else return yPosMin;
		  }
		  
 
	   public int numTails() {
	      return theElement.getNumTails();
		  }	   
	   public int getAbsLength(){
	      return theElement.getAbsLength();
		  }	
	   public MusicElement getMusicElement(){
	      return theElement;
		  }	    
       }    
    }
    
    