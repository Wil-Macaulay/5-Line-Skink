//
//  GraceBeamer.java
//  5 Line Skink
//
//  Created by wil macaulay on Fri Sep 19 2003.
//  Copyright (c) 2003 wil macaulay. All rights 
//  reserved.
//  NOTE:  there is no behaviour difference between this and
//  a regular beamer, so it will be replaced by a constructor.
//     History
//         19 Jun 2004 WM add grace style 
//
package ca.celticmusic.skink;
public class GraceBeamer extends Beamer{
    boolean isSlashed = true;       // slash through single gracenotes
	boolean isSlurToNote = false; // slur mark ending on graced note FIXME not yet implemented
	int numTailsSingle = 1; // number of tails for a single grace note
    public void init(double mid){
       super.init(mid);
       tailsUp = true;
       autoTail = false;
       headSize = StaffPanel.quarterSize-2;
       isGrace = true;
       beamSeparation = 2;
       tailHeight = (StaffPanel.tailHeight * 3)/5;
       }
	   
    public void setGraceStyle( boolean tailsUp, boolean slurToNote, boolean slash,int numTailsSingle){
	   this.tailsUp = tailsUp;
	   this.isSlurToNote = slurToNote;
	   this.isSlashed = slash;
	   this.numTailsSingle = numTailsSingle;
	   }
	public boolean isSlashed(){
	   return isSlashed;
	   }
	public boolean isSlurToNote(){
	   return isSlurToNote();
	   }    
   // draw a single note (no beam)    
   protected void drawSingleNote(Renderer r, boolean tailUp){       
        BeamEntry b = (BeamEntry) theNotes.get(0);
        r.paintTail(b.xPos,b.yPos(tailUp),tailHeight+((b.numTails()-1)*beamSeparation),tailUp,numTailsSingle,headSize,true);
		if (isSlashed){
		   r.paintGraceSlash(b.xPos,b.yPos(tailUp),tailUp);
		   }
        }
}
