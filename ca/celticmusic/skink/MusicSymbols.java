//
//  MusicSymbols.java
//  5 Line Skink
//
//  Created by wil macaulay on Mon Aug 11 2003.
//  Copyright (c) 2003 Wil Macaulay. All rights reserved.
//
/**
 * creates Shapes for music symbols.  Note that it uses RelativePath, which uses the PostScript convention
 * of Y increasing upward on the page.
 *
 * @author Wil Macaulay
 * @version 1.0f1 11 Aug 2003
 * @see G2DRenderer
 * @since Aug 2003
 *
 */
package ca.celticmusic.skink;
import java.awt.Shape;
import java.awt.geom.GeneralPath;


public class MusicSymbols {

	static GeneralPath trebleClef = new GeneralPath();
	static GeneralPath bassClef = new GeneralPath();
	static GeneralPath cClef = new GeneralPath(); //tenor and alto
	static GeneralPath fullHead = new GeneralPath();
	static GeneralPath halfNoteHead = new GeneralPath();
	static GeneralPath wholeNoteHead = new GeneralPath();
	static GeneralPath breveNoteHead = new GeneralPath();
	static GeneralPath longa = new GeneralPath();
	static GeneralPath quarterRest = new GeneralPath();
	static GeneralPath eighthRest = new GeneralPath();
	static GeneralPath sixteenthRest = new GeneralPath();
	static GeneralPath thirtysecondRest = new GeneralPath();
	static GeneralPath commonTime = new GeneralPath();
	static GeneralPath flagUp = new GeneralPath();
	static GeneralPath flagDown = new GeneralPath();
	static GeneralPath barLine = new GeneralPath();
	static GeneralPath thickBarLine = new GeneralPath();
	static GeneralPath smallDot = new GeneralPath();
	static GeneralPath fermata = new GeneralPath();
	static GeneralPath coda = new GeneralPath();
	static GeneralPath segno = new GeneralPath();
	static GeneralPath upperMordent = new GeneralPath();
	static GeneralPath slide = new GeneralPath();

	
   
   static boolean isInitialized = false;
   static RelativePath rp = new RelativePath();

   static Shape getUpperMordent(){
	   if (!isInitialized){
		   makeSymbols();
			}
		return upperMordent;
		}
		
   static Shape getSlide(){
	   if (!isInitialized){
		   makeSymbols();
			}
		return slide;
		}
	
	static Shape getCoda(){
	   if (!isInitialized){
		   makeSymbols();
			}
		return coda;
		}
   static Shape getSegno(){
	   if (!isInitialized){
		   makeSymbols();
			}
		return segno;
		}
   static Shape getFermata(){
	   if (!isInitialized){
		   makeSymbols();
			}
		return fermata;
		}

   static Shape getSmallDot() {
		if (!isInitialized) {
			makeSymbols();
		}
		return smallDot;
	}

	static Shape getBarLine() {
		if (!isInitialized) {
			makeSymbols();
		}
		return barLine;
	}

	static Shape getThickBarLine() {
		if (!isInitialized) {
			makeSymbols();
		}
		return thickBarLine;
	}

	static Shape getBassClef() {
		if (!isInitialized) {
			makeSymbols();
		}
		return bassClef;
	}

	static Shape getTrebleClef() {
		if (!isInitialized) {
			makeSymbols();
		}
		return trebleClef;
	} 

   static Shape getCClef() {
	   if (!isInitialized) {
		   makeSymbols();
	   }
	   return cClef;
   } 
     
   static Shape getFullHead(){
      if (!isInitialized){
         makeSymbols();
         }
      return fullHead;
      } 
            
   static Shape getHalfNoteHead(){
      if (!isInitialized){
         makeSymbols();
         }
      return halfNoteHead;
      }
         
   static Shape getWholeNoteHead(){
      if (!isInitialized){
         makeSymbols();
         }
      return wholeNoteHead;
      }
   static Shape getBreveNoteHead(){
      if (!isInitialized){
         makeSymbols();
         }
      return breveNoteHead;
      }
        
   static Shape getLonga(){
      if (!isInitialized){
         makeSymbols();
         }
      return longa;
      }
   static Shape getQuarterRest(){
      if (!isInitialized){
         makeSymbols();
         }
      return quarterRest;
      }
      
   static Shape getEighthRest(){
      if (!isInitialized){
         makeSymbols();
         }
      return eighthRest;
      }
   static Shape getSixteenthRest(){
      if (!isInitialized){
         makeSymbols();
         }
      return sixteenthRest;
      }
   static Shape getThirtysecondRest(){
      if (!isInitialized){
         makeSymbols();
         }
      return thirtysecondRest;
      }
   
   static Shape getFlagUp(){
      if (!isInitialized){
	     makeSymbols();
		 }
	  return flagUp;	 
	  }
	  	
   static Shape getFlagDown(){
      if (!isInitialized){
	     makeSymbols();
		 }
	  return flagDown;	
	  }
	  	
   static Shape getCommonTime(){
      if (!isInitialized){
	     makeSymbols();
		 }
	  return commonTime;
	  }	 
        
   static void makeSymbols() {
		makeSlide();
		makeUpperMordent();
		makeSegno();
		makeCoda();
		makeFermata();
		makeSmallDot();
		makeBarLine();
		makeThickBarLine();
		makeTrebleClef();
		makeBassClef();
		makeCClef();
		makeFilledNoteHead();
		makeFlagDown();
		makeFlagUp();
		makeHalfNoteHead();
		makeWholeNoteHead();
		makeBreveNoteHead();
		makeLonga();
		makeQuarterRest();
		makeEighthRest();
		makeSixteenthRest();
		makeThirtysecondRest();
		makeCommonTime();
	}

	static void makeSlide() {
		rp.init(-7.2, -4.8, slide);
		rp.rcurveTo(1.8, -0.7, 4.5, 0.2, 7.2, 4.8);
		rp.rcurveTo(-2.1, -5.0, -5.4, -6.8, -7.6, -6.0);

	}

	/*
	 * "\n/sld { % usage: x y sld - slide\n" " M -7.2 -4.8 rmoveto\n" " 1.8 -0.7
	 * 4.5 0.2 7.2 4.8 rcurveto\n" " -2.1 -5.0 -5.4 -6.8 -7.6 -6.0 rcurveto
	 * fill\n" "} bdef\n"
	 */		
   static void makeUpperMordent(){
	   rp.init(0,0,upperMordent);
		rp.rlineTo(2.2,2.2);
		rp.rlineTo(2.1,-2.9);		
		rp.rlineTo(0.7,0.7);		
		rp.rlineTo(-2.2,-2.2);
		rp.rlineTo(-2.1,2.9);
		rp.rlineTo(-0.7,-0.7);
		rp.rlineTo(-2.2,-2.2);
		rp.rlineTo(-2.1,2.9);
		rp.rlineTo(-0.7,-0.7);
		rp.rlineTo(2.2,2.2);
		rp.rlineTo(2.1,-2.9);		
		rp.rlineTo(0.7,0.7);		
		}

   static void makeFermata(){
	   rp.init(0.0f,0.0f,fermata);
		rp.rcurveTo(0.0,1.5,2.0,1.5,2.0,0.0);
		rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
		rp.rmoveTo(-7.5,0.0);
		rp.rcurveTo(0.0, 11.5, 15, 11.5, 15, 0.0);
		rp.rlineTo(-0.25,0.0);
		rp.rcurveTo(-1.25,9,-13.25,9,-14.50, 0);
		}  
		
   static void makeCoda(){
	   rp.init(0.0,0.0,coda);
		rp.rmoveTo(-7.5,7.5);
		rp.rcurveTo(0.0, 11.5, 15, 11.5, 15, 0.0);
		rp.rlineTo(-2.0,0);
		rp.rcurveTo(0.0, 9.5, -11, 9.5, -11, 0.0);
		rp.rlineTo(-2.0,0);
		rp.rcurveTo(0.0, -11.5, 15, -11.5, 15, 0.0);
		rp.rlineTo(-2.0,0);
		rp.rcurveTo(0.0, -9.5, -11, -9.5, -11, 0.0);
		rp.rlineTo(-2.0,0);
		
		rp.rmoveTo(7.0,-9.0);
		rp.rlineTo(0.0,9.0);
		rp.rlineTo(-9.0,0.0);
		rp.rlineTo(0.0,1.0);
		rp.rlineTo(9.0,0.0);
		rp.rlineTo(0.0,9.0);
		rp.rlineTo(1.0,0.0);
		rp.rlineTo(0.0,-9.0);
		rp.rlineTo(9.0,0.0);
		rp.rlineTo(0.0,-1.0);
		rp.rlineTo(-9.0,0.0);
		rp.rlineTo(0.0,-9.0);
		rp.rlineTo(-1.0,0.0);
	   }
		
	static void makeSegno(){
		rp.init(0,0,segno);
		rp.rmoveTo(0,3);
		rp.rcurveTo(1.5, -1.7, 6.4, 0.3, 3.0, 3.7);
		rp.rcurveTo(-10.4, 7.8, -8.0, 10.6, -6.5, 11.9);
		rp.rcurveTo(4.0, 1.9, 5.9, -1.7, 4.2, -2.6);
		rp.rcurveTo(-1.3, -0.7, -2.9, 1.3, -0.7, 2.0);
		rp.rcurveTo(-1.5, 1.7, -6.4, -0.3, -3.0, -3.7);
		rp.rcurveTo(10.4, -7.8, 8.0, -10.6, 6.5, -11.9);
		rp.rcurveTo(-4.0, -1.9, -5.9, 1.7, -4.2, 2.6);
		rp.rcurveTo(1.3, 0.7, 2.9, -1.3, 0.7, -2.0);
		rp.rmoveTo(-6.0,1.6);
		rp.rlineTo(12.6,12.6);
		rp.rlineTo(0.6,-0.6);
		rp.rlineTo(-12.6,-12.6);
		rp.rlineTo(-0.6,0.6);
		rp.rmoveTo(0,6.5);
		rp.rcurveTo(0.0,1.5,2.0,1.5,2.0,0.0);
		rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
		rp.rmoveTo(12,0);
		rp.rcurveTo(0.0,1.5,2.0,1.5,2.0,0.0);
		rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
	   }					


	  
   static void makeBarLine(){   
        
        // barline
		rp.init(0.0f,0.0f,barLine);
		rp.rlineTo(0.0f,-24.0f);
		rp.rlineTo(1.0f,0.0f);
		rp.rlineTo(0.0f,24.0f);
		rp.rlineTo(-1.0f,0.0f);
		}
      
   static void makeThickBarLine(){   
        
        // barline
        rp.init(0.0f,0.0f,thickBarLine);
		rp.rlineTo(0.0f,-24.0f);
		rp.rlineTo(2.0f,0.0f);
		rp.rlineTo(0.0f,24.0f);
		rp.rlineTo(-2.0f,0.0f);
		}
      
 
	static void makeTrebleClef(){   
        
        // treble clef
        rp.init(-1.9f,3.7f,trebleClef);
        
        rp.rcurveTo(-3.3f,1.9f,-3.1f,6.8f,2.4f,8.6f);
        rp.rcurveTo(7.0f, 0.0f, 9.8f, -8.0f, 4.1f, -11.7f);
        rp.rcurveTo(-5.2f, -2.4f, -12.5f, 0.0f, -13.3f, 6.2f);
        rp.rcurveTo(-0.7f, 6.4f, 4.15f, 10.5f, 10.0f, 15.3f);
        rp.rcurveTo(4.0f, 4.0f, 3.6f, 6.1f, 2.8f, 9.6f);
        rp.rcurveTo(-2.3f, -1.5f, -4.7f, -4.8f, -4.5f, -8.5f);
        rp.rcurveTo(0.8f, -12.2f, 3.4f, -17.3f, 3.5f, -26.3f);
        rp.rcurveTo( 0.3f, -4.4f, -1.2f, -6.2f, -3.8f, -6.2f );
        rp.rcurveTo(-3.7f, -0.1f, -5.8f, 4.3f, -2.8f, 6.1f);
        rp.rcurveTo(3.9f, 1.9f, 6.1f, -4.6f, 1.4f, -4.8f);
        rp.rcurveTo(0.7f, -1.2f, 4.6f, -0.8f, 4.2f, 4.2f);
        
        rp.rcurveTo( -0.2, 10.3, -3.0, 15.7, -3.5, 28.3 );
        rp.rcurveTo(0.0, 4.1, 0.6, 7.4, 5.0, 10.6);
        rp.rcurveTo(2.3, -3.2, 2.9, -10.0, 1.0, -12.7);
        rp.rcurveTo(-2.4, -4.3, -11.5, -10.3, -11.8, -15.0);
        rp.rcurveTo(0.4, -7.0, 6.9, -8.5, 11.7, -6.1);
        rp.rcurveTo(3.9, 3.0, 1.3, 8.8, -3.7, 8.1);
        rp.rcurveTo(-4.0, -0.2, -4.8, -3.1, -2.7, -5.7);
        }
        
   static void makeSmallDot(){
        rp.init(0,0,smallDot);
		rp.rcurveTo(0.0,1.5,2.0,1.5,2.0,0.0);
        rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
		}
		
   static void makeBassClef(){     
        // bass clef
        rp.init(-8.8f,3.5f,bassClef);
        rp.rcurveTo(6.3, 1.9, 10.2, 5.6, 10.5, 10.8);
        rp.rcurveTo(0.3, 4.9, -0.5, 8.1, -2.6, 8.8);
        rp.rcurveTo(-2.5, 1.2, -5.8, -0.7, -5.9, -4.1);
        rp.rcurveTo(1.8, 3.1, 6.1, -0.6, 3.1, -3.0);
        rp.rcurveTo(-3.0, -1.4, -5.7, 2.3, -1.9, 7.0);
        rp.rcurveTo(2.6, 2.3, 11.4, 0.6, 10.1, -8.0);
        rp.rcurveTo(-0.1, -4.6, -5.0, -10.2, -13.3, -11.5);
        rp.rmoveTo( 15.5, 17.0 );
        rp.rcurveTo(0.0, 1.5, 2.0, 1.5, 2.0 ,0.0 );
        rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
        rp.rmoveTo(0.0, -5.5);
        rp.rcurveTo(0.0, 1.5, 2.0, 1.5, 2.0, 0.0);
        rp.rcurveTo(0.0, -1.5, -2.0, -1.5, -2.0, 0.0);
        
        isInitialized = true;
        }
   static void makeCClef(){
	   //tenor and alto clef
	   rp.init(0.0f,0.0f, cClef);
	   halfCClef();
	  
	   rp.rOrigin();
	   rp.rmoveTo(0.0, 24.0);
	   rp.rscale(1.0f, -1.0f);
	   halfCClef();
	   
	   rp.rOrigin();
	   rp.rmoveTo(-5.0, 0.0);
	   rp.rlineTo(0.0, 24.0);
	   rp.rlineTo(3.0, 0.0);
	   rp.rlineTo(0.0, -24.0);
	   rp.rlineTo(-3.0, 0.0);
	   
	   rp.rOrigin();
	  
	   rp.rmoveTo(-0.5, 0.0);
	   rp.rlineTo(0.0, 24.0);
	   rp.rlineTo(0.8, 0.0);
	   rp.rlineTo( 0.0,-24);
	   rp.rlineTo(-0.8, 0.0);
	   
	   /*
	    * 
	    * 	"\n/cchalf {\n"
	"	0 0 M 0.0 12.0 rmoveto\n"
	"	2.6 5.0 rlineto\n"
	"	2.3 -5.8 5.2 -2.4 4.7 1.6 rcurveto\n"
	"	0.4 3.9 -3.0 6.7 -5.1 4.0 rcurveto\n"
	"	4.1 0.5 0.9 -5.3 -0.9 -1.4 rcurveto\n"
	"	-0.5 3.4 6.5 4.3 7.8 -0.8 rcurveto\n"
	"	1.9 -5.6 -4.1 -9.8 -6.0 -5.4 rcurveto\n"
	"	-1.6 -3.0 rlineto\n"
	"	fill\n"
	"} bdef\n"

	"\n/cclef {	% usage: x y cclef\n"
	"	gsave T\n"
	"	cchalf 0 24 T 1 -1 scale cchalf\n"
	"	-5.0 0 M 0 24 rlineto 3 0 rlineto 0 -24 rlineto fill\n"
	"	-0.5 0 M 0 24 rlineto 0.8 setlinewidth stroke grestore\n"
	"} bdef\n"

	    */
	   
   }

/**
 * 
 */
private static void halfCClef() {
	rp.rmoveTo(0.0, 12.0);
	   rp.rlineTo(2.6, 5.0);	
	   rp.rcurveTo(2.3,-5.8,5.2,2.4,4.7,1.6);
	   rp.rcurveTo(0.4, 3.9, -3.0, 6.7, -5.1, 4.0);
	   rp.rcurveTo(4.1, 0.5, 0.9, -5.3, -0.9, -1.4);
	   rp.rcurveTo(-0.5, 3.4, 6.5, 4.3, 7.8, -0.8);
	   rp.rcurveTo(1.9, -5.6, -4.1, -9.8, -6.0, -5.4);
	   rp.rlineTo(-1.6, -3.0);
}
   /**
    * Make filled note head for quarter note and smaller
    */
         
   static void makeFilledNoteHead(){
   
        //rp.init(3.5f,2.0f,fullHead);
        //rp.init(7.0f,-1.0f,fullHead);
        //rp.init(7.0,-1.3,fullHead);
        rp.init(7.5,-1.3,fullHead);
        rp.rcurveTo(-2.0, 3.5, -9.0, -0.5, -7.0, -4.0);
        rp.rcurveTo(2.0, -3.5, 9.0, 0.5, 7.0, 4.0);
        }
   
   static void makeFlagUp(){
        rp.init(0,0,flagUp);
		rp.rcurveTo(0.9, -3.7, 9.1, -6.4, 6.0, -12.4);
		rp.rcurveTo(1.0, 5.4, -4.2, 8.4, -6.0, 8.4); 

		}
   static void makeFlagDown(){
        rp.init(0,0,flagDown);
		rp.rcurveTo(0.9, 3.7 ,9.1, 6.4, 6.0, 12.4);
		rp.rcurveTo(1.0, -5.4, -4.2, -8.4, -6.0, -8.4); 
		}
   
   static void makeHalfNoteHead(){
        //rp.init(3.0, 1.6,halfNoteHead);
        rp.init(7.0,-1.3,halfNoteHead);
        rp.rcurveTo(-1.0, 1.8, -7.0, -1.4, -6.0, -3.2);
        rp.rcurveTo(1.0, -1.8, 7.0, 1.4, 6.0, 3.2);
        rp.rmoveTo(0.5, 0.3);
        rp.rcurveTo(2.0, -3.8, -5.0, -7.6, -7.0, -3.8);
        rp.rcurveTo(-2.0, 3.8, 5.0, 7.6, 7.0, 3.8);
        }
        
   static void makeWholeNoteHead(){
        rp.init(2.1, -0.75,wholeNoteHead);
        
        rp.rcurveTo(2.8, 1.6, 6.0, -3.2, 3.2, -4.8);
        rp.rcurveTo(-2.8, -1.6, -6.0, 3.2, -3.2, 4.8);
        rp.rmoveTo(7.2, -2.4);
        rp.rcurveTo(0.0, 1.8, -2.2, 3.2, -5.6, 3.2);
        rp.rcurveTo(-3.4, 0.0, -5.6, -1.4, -5.6, -3.2);
        rp.rcurveTo(0.0, -1.8, 2.2, -3.2, 5.6, -3.2);
        rp.rcurveTo(3.4, 0.0, 5.6, 1.4, 5.6, 3.2);
        }

   static void makeBreveNoteHead(){
        //rp.init(-1.6, 2.4,breveNoteHead);
        rp.init(2.1, -0.75,breveNoteHead);
        
        rp.rcurveTo(2.8, 1.6, 6.0, -3.2, 3.2, -4.8);
        rp.rcurveTo(-2.8, -1.6, -6.0, 3.2, -3.2, 4.8);
        rp.rmoveTo(7.2, -2.4);
        rp.rcurveTo(0.0, 1.8, -2.2, 3.2, -5.6, 3.2);
        rp.rcurveTo(-3.4, 0.0, -5.6, -1.4, -5.6, -3.2);
        rp.rcurveTo(0.0, -1.8, 2.2, -3.2, 5.6, -3.2);
        rp.rcurveTo(3.4, 0.0, 5.6, 1.4, 5.6, 3.2);
        rp.rmoveTo( -11.0, -4.0);
        rp.rlineTo(0.0, 9.0); 
        rp.rlineTo(-1.0,-1.0);
        rp.rlineTo(0.0,-9.0);
        rp.rlineTo(1.0,1.0);
        rp.rmoveTo(12.0,0.0);
        rp.rlineTo(0.0, 9.0); 
        rp.rlineTo(-1.0,-1.0);
        rp.rlineTo(0.0,-9.0);
        rp.rlineTo(1.0,1.0);
   
   
       }
   static void makeLonga(){
       rp.init(0,-6.0,longa);
       rp.rlineTo(10.0,0.0);
       rp.rlineTo(0,1.5);
       rp.rlineTo(-10.0,0.0);
       rp.rlineTo(0,-1.5);
       rp.rmoveTo(0.0,5.4);
       rp.rlineTo(10.0,0.0);
       rp.rlineTo(0,1.5);
       rp.rlineTo(-10.0,0.0);
       rp.rlineTo(0,-1.5);
       rp.rmoveTo(-2.5,-8.0);
       rp.rlineTo(0.0,10.0);
       rp.rlineTo(2.5,1.5);
       rp.rlineTo(0.0,-10.0);
       rp.rlineTo(-2.5,-1.5);
       rp.rmoveTo(12.5,-5.0);
       rp.rlineTo(0.0,15.0);
       rp.rlineTo(2.5,1.5);
       rp.rlineTo(0.0,-15.0);
       rp.rlineTo(-2.5,-1.5);
       
       
       }
   static void makeQuarterRest(){
       rp.init(0.0,6.0,quarterRest);
       rp.rlineTo(1.3, -3.4);
       rp.rlineTo( -2.0, -4.5);
       rp.rlineTo(3.1, -4.8);
       rp.rcurveTo(-3.2, 3.5, -5.8, -1.4, -1.4, -3.8);
       rp.rcurveTo(-1.9, 2.0, -0.8, 5.0, 2.4, 2.6);
       rp.rlineTo( -2.2, 4.2 );
       rp.rcurveTo(0.0, 0.0, 2.0, 4.7, 2.1, 4.7);
       rp.rlineTo( -3.3, 5.0);
       }
       
   static void makeEighthRest(){
       rp.init(0.0,-12.0,eighthRest);
       rp.rmoveTo(3.3,4.0);
       rp.rlineTo(-3.4,-9.6);
       rp.rlineTo(0.5,0.5);
       rp.rlineTo(3.4,9.6);
       rp.rlineTo(-0.5,-0.5);
       makeEighthRestCurve();
       }
       
   static void makeSixteenthRest(){
       rp.init(0.0,-12.0,sixteenthRest);
       rp.rmoveTo(3.3,4.0);
       rp.rlineTo(-4.0,-15.6);
       rp.rlineTo(0.5,0.5);
       rp.rlineTo(4.0,15.6);
       rp.rlineTo(-0.5,-0.5);
       makeEighthRestCurve();
       rp.rmoveTo(-1.45,-6.0);
       makeEighthRestCurve();
       }
       
   static void makeThirtysecondRest(){
       rp.init(0.0,-12.0,thirtysecondRest);
       rp.rmoveTo(4.8,10.0);
       rp.rlineTo(-5.5,-21.6);
       rp.rlineTo(0.5,0.5);
       rp.rlineTo(5.5,21.6);
       rp.rlineTo(-0.5,-0.5);
       makeEighthRestCurve();
       rp.rmoveTo(-1.45,-6.0);
       makeEighthRestCurve();
       //rp.rmoveTo(1.9,-2.0);
       rp.rmoveTo(-1.45,-6.0);
       makeEighthRestCurve();
       }
   
   // helper function	       
   static void makeEighthRestCurve(){    
       rp.rcurveTo(-1.5, -1.5, -2.4, -2.0, -3.6, -2.0);
       rp.rcurveTo(2.4, 2.8, -2.8, 4.0, -2.8, 1.2);
       rp.rcurveTo(0.0, -2.7, 4.3, -2.4, 5.9, -0.6);
       }
   
   
   static void makeCommonTime(){  
      rp.init(0.0,-24.0,commonTime);
	  rp.rmoveTo(1.0,17.3);
	  rp.rcurveTo(0.9, -0.0, 2.3, -0.7, 2.4, -2.2);
	  rp.rcurveTo(-1.2, 2.0, -3.6, -0.1, -1.6, -1.7);
	  rp.rcurveTo(2.0, -1.0, 3.8, 3.5, -0.8, 4.7);
	  rp.rcurveTo(-2.0, 0.4, -6.4, -1.3, -5.8, -7.0);
	  rp.rcurveTo(0.4, -6.4, 7.9, -6.8, 9.1, -0.7);
	  rp.rcurveTo(-2.3, -5.6, -6.7, -5.1, -6.8, 0.0);
	  rp.rcurveTo(-0.5, 4.4, 0.7, 7.5, 3.5, 6.9);
	  }
	
	  
    
               
        
        
}


