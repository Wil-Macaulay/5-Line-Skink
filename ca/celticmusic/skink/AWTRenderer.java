/**
 *
 * Renders music elements for a JDK 1.1 compatible
 * AWT Graphics object.  
 * 
 * @author Wil Macaulay
 * @version 1.0a1 17 Feb 2002
 *
 * @see Renderer
 * @see G2DRenderer
 * @since Mar 2000
 */
//////////////
//  
// Music renderer (AWT)
//       7 dec 2000 WM  fix treble clef
//       17 jan 2001 WM add breve support
//       0.9 22 jun 2001 wm multi tune printing
//       1.2a0 8 jun 2004 double is now native - convert to int
//
//////////////
package ca.celticmusic.skink;
import java.awt.Font;
import java.awt.Graphics;

public class AWTRenderer implements Renderer
   {
   Graphics g;
   Font titleFont, infoFont,meterFont;
   int spaceHeight, quarterSize, halfSize;
   
   public AWTRenderer()
      {
      //titleFont = new Font("SansSerif", Font.ITALIC, 16);
      titleFont = new Font("SansSerif", Font.BOLD, 16);
      infoFont =  new Font("SansSerif", Font.PLAIN, 12);
      meterFont = new Font("Serif", Font.BOLD, 14);
      spaceHeight = (int)StaffPanel.spaceHeight;
      quarterSize = (int)StaffPanel.quarterSize;
      halfSize = (int)StaffPanel.halfSize;
      
      }
      
   public void setGraphics(Graphics gr)
      {
      g =  gr;
      Debug.output(4,"renderer setGraphics class:"+g.getClass().getName());
      }
   
   public Graphics getGraphics()
      {
      Debug.output(4,"renderer getGraphics class:"+g.getClass().getName());
      return g;
      }
   public void paintTupletMark(double xFirst, double y, 
                          double xLast,  double yLast,
                          int tupSize){ 
		paintTupletMark((int)xFirst, (int) y, (int)xLast, tupSize);
		}

   
   public void paintTupletMark(int xFirst, int y, 
                          int xLast,  
                          int tupSize)
       {                      
      
	   g.drawLine(xFirst,y,(xFirst+xLast)/2-3,y);
	   g.drawLine(xFirst,y,xFirst,y-3);
	   g.drawLine((xFirst+xLast)/2+3,y,xLast, y);
	   g.drawLine(xLast,y,xLast,y - 3);
	   String temp = ""+tupSize;
           g.setFont(infoFont);
	   g.drawString(temp,(xFirst+xLast)/2 - 2, y+3);
       }
       
   
   
   public void paintTail(double x,double y,
                         double tailLen,boolean tailsUp,
						 int numTails,double headSize,boolean isGrace){
      paintTail((int) x, (int) y, (int) tailLen, tailsUp, numTails, (int) headSize, isGrace);
	  }
	  
  
   public void paintTail(int x,int y,int tailLen,boolean tailsUp,int numTails,int headWidth,boolean isGrace){
      // tail up
       if (tailsUp){
            g.drawLine(x+headWidth, y+spaceHeight/2,
                        x+headWidth, y-tailLen);
            paintHooks(numTails,x+headWidth,y-tailLen,-4,isGrace);
            }          
       else {
            g.drawLine(x, y+spaceHeight/2,
                        x, y+tailLen);
            paintHooks(numTails,x,y+tailLen,4,isGrace);           
            }           
        }
	   
  protected void paintHooks(int numTails, int xBegin, int yEnd, int hookIncr,boolean isGrace){    
      // draw hooks 
      for (int i = 0; i<numTails; i++){
          
          g.drawLine(xBegin,yEnd,xBegin+quarterSize,yEnd-hookIncr-2);
          if(!isGrace){
             g.drawLine(xBegin,yEnd+1,xBegin+quarterSize,yEnd-hookIncr-1);
             yEnd -= hookIncr;
             }
          }
      }    
	  
   public void paintBeam(double xStart,double yStart,double xEnd,double yEnd,boolean grace){
      paintBeam((int) xStart,(int)yStart, (int)xEnd, (int)yEnd, grace);
	  }
	  
   public void paintBeam(int xStart,int yStart,int xEnd,int yEnd, boolean isGrace)
      {
      g.drawLine(xStart,yStart,xEnd,yEnd);
      if (!isGrace)
           g.drawLine(xStart,yStart+1,xEnd,yEnd+1);
      }
      	   
   public void paintGuitarChord(double x, double y, String chord){
      paintGuitarChord((int) x, (int) y,  chord);
	  }
	  
	public void paintStringDeco(double x, double y, String chord){
      paintGuitarChord((int) x, (int) y,  chord);
		}
		
   public void paintGuitarChord(int x, int y, String chord)
      {
      // set font, etc
      g.setFont(infoFont);
      g.drawString(chord,x,y-spaceHeight*2);
      }
	  
   public void paintInfoString(double x, double y, String s){
      paintInfoString((int) x, (int) y,  s);
	  }
   public void paintInfoString(int x, int y, String s)
      {
      // set font, etc
      g.setFont(infoFont);
      g.drawString(s,x,y);
      }
      
   public void paintBarline(double x, double y, String type){
      paintBarline((int) x, (int) y,  type);
	  }
	  
   public void paintBarline(int x, int y, String type)
      {
      g.drawLine(x,y,x,y+(4*spaceHeight));
      if (!"single".equals(type))
           g.drawLine(x+3,y,x+3,y+(4*spaceHeight));
      
       
      } 	                 
   public void paintNthEnding(double x, double y, String repeatString) {
      paintNthEnding((int) x, (int) y,  repeatString) ;
	  }
	  
   public void paintNthEnding(int x, int y, String repeatString) 
      {        
      g.drawLine(x,y-spaceHeight/2,x,y-spaceHeight*4);
      g.drawLine(x,y-spaceHeight*4,
                 x+spaceHeight,y-spaceHeight*4);
      g.setFont(infoFont);
      g.drawString(repeatString,x+2,y-spaceHeight*2-1);
      }
      
   public void paintDot(double x, double y)  {
      paintDot((int) x, (int) y);
	  } 
	    
   public void paintDot(int x, int y)  
      { 
      g.fillOval(x,y,3,3);
      }
      
   public void paintNumericMeter(double x, double y, String num, String denom){
      paintNumericMeter((int)x, (int)y, num, denom);
	  }
	  
   public void paintNumericMeter(int x, int y, String num, String denom) {   
	  g.setFont(meterFont);
	  g.drawString(num,x, y+2*spaceHeight-1 );
	  g.drawString(denom,x, y+4*spaceHeight-1);
	  }
   public void paintSpecialMeter(double x, double y, String s){
      paintSpecialMeter((int)x, (int)y, s);
	  }
	  
   public void paintSpecialMeter(int x, int y, String s)
      {	
      String s1 = s.trim().toUpperCase(); 
       
	  g.drawArc(x, y+spaceHeight, 2*spaceHeight, 
	               2*spaceHeight, 45, 270);
	  if (s1.equals("C|"))
	      {
	      g.drawLine(x+spaceHeight,y+spaceHeight/2,
	                       x+spaceHeight, y+7*spaceHeight/2);
          }
      } 
   public void paintThirtysecondRest(double x,double y){
      paintThirtysecondRest((int) x,(int) y);
      }
	  
   public void paintThirtysecondRest(int x,int y)
      {
      paintEighthRest(x,y);
      g.drawArc(x-2,y+spaceHeight+6,spaceHeight,spaceHeight/2,180,210);
      g.drawArc(x-4,y+spaceHeight+8,spaceHeight,spaceHeight/2,180,210);
      
      }
   public void paintSixteenthRest(double x,double y){
      paintSixteenthRest((int) x,(int) y);
      }
	  
   public void paintSixteenthRest(int x,int y)
      {
      paintEighthRest(x,y);
      g.drawArc(x-2,y+spaceHeight+6,spaceHeight,spaceHeight/2,180,210);
      
      }
      
   public void paintEighthRest(double x,double y){
      paintEighthRest((int) x,(int) y);
	  }
   public void paintEighthRest(int x,int y)
      {
      g.drawLine(x,y+4*spaceHeight,x+spaceHeight,y+2*spaceHeight/2);
      g.drawArc(x,y+3*spaceHeight/2,spaceHeight,spaceHeight/2,180,210);
      }
      
   public void paintQuarterRest(double x, double y){
       paintQuarterRest((int) x, (int) y);
       }
   public void paintQuarterRest(int x, int y)
      {
      g.drawLine(x,y-spaceHeight,x+3,y);
      g.drawArc(x,y-spaceHeight/2,spaceHeight/2,spaceHeight,90,180);
      g.drawLine(x-3,y+spaceHeight/2+2,x+3,y+spaceHeight/2);
      g.drawLine(x-3,y+spaceHeight/2+2,x,y+spaceHeight*3/2);
      }
      
   public void paintHalfRest(double x, double y){
       paintHalfRest((int) x, (int) y);
	   }
   public void paintHalfRest(int x, int y)
      {
      g.fillRect(x,y,quarterSize,spaceHeight/2);
      }
      
   public void paintWholeRest(double x, double y) {
      paintWholeRest((int) x, (int) y);
	  }
   public void paintWholeRest(int x, int y) {
      g.fillRect(x,y-spaceHeight/2,halfSize,spaceHeight/2);
      }
   public void paintBreveRest(double x, double y)  {
      paintBreveRest((int) x, (int) y);
	  }
   public void paintBreveRest(int x, int y)  {
      g.fillRect(x,y-spaceHeight/2,quarterSize,spaceHeight);
      }

   public void paintLongaRest(double x, double y)   {
      paintLongaRest((int) x,(int) y);
	  }
	  
   public void paintLongaRest(int x, int y)   {

      g.fillRect(x,y-spaceHeight/2,(quarterSize*2),spaceHeight);
      }
      
   public void paintTie(double tieStart,double y, double tieEnd, boolean tailsUp){
	  paintTie( (int)tieStart, (int)y, (int) tieEnd,  tailsUp);
	  }
	  
   public void paintTie(int tieStart,int y, int tieEnd, boolean tailsUp)
      {
      if (tailsUp)
        g.drawArc(tieStart,y+spaceHeight/2,
                  tieEnd+quarterSize/2-tieStart,
                  spaceHeight,180,180);
      else
        g.drawArc(tieStart,y-spaceHeight/2,
                  tieEnd+quarterSize/2-tieStart,
                  spaceHeight,180,-180);
	  } 
   public void paintSlur(double x1, double y1, 
                         double xcp1, double ycp1, 
						 double xcp2, double ycp2, double x2, double y2){
	  paintSlur(x1,y1,x2,true);
	  }
	  
   public void paintSlur(double x1, double y, double x2, boolean tailsUp){
      paintSlur((int) x1, (int) y, (int) x2,  tailsUp);
	  }
	  
   public void paintSlur(int x1, int y, int x2, boolean tailsUp)
      {
      paintTie(x1,y,x2-quarterSize,tailsUp) ;
      }
   	   
   public void paintTrill(double x, double y){
      paintTrill((int) x, (int) y);
	  }
	// FIXME not implemented  
	public void paintSlide(double x, double y){}
	public void paintUpperMordent(double x, double y){}
	public void paintSegno(double x, double y){}
	public void paintCoda(double x, double y){}
	public void paintFermata(double x, double y){}
	public void paintInvertedFermata(double x, double y){}
	public void paintAccent(double x, double y){};
	public void paintLowerMordent(double x, double y){};
	public void paintLongPhrase(double x, double y){};
	public void paintShortPhrase(double x, double y){};
	public void paintMediumPhrase(double x, double y){};
	public void paintBassClef(double x, double y){};
	public void paintAltoClef(double x, double y){};
	public void paintTenorClef(double x, double y){};
	
   public void paintTrill(int x, int y)
      {
      g.setFont(meterFont);
      g.drawString("t",x,y);
      g.drawString("r",x+2,y);
      }
   public void paintRoll(double x, double y){
      paintRoll((int) x, (int) y);
	  }
   public void paintRoll(int x, int y)
      {
      g.setFont(meterFont);
      g.drawString("~",x,y);
      }
      
   public void paintUpBow(double x, double y){
      paintUpBow((int) x,(int) y);
	  }
   public void paintUpBow(int x, int y)
      {
      x+=quarterSize/2;
      g.drawLine(x-3,y-4,x,y); 
      g.drawLine(x+3,y-4,x,y); 
      }
   
   public void paintDownBow(double x, double y){
      paintDownBow((int) x, (int) y);
	  }
	  
   public void paintDownBow(int x, int y)
      {
      g.drawLine(x-4,y-4,x-4,y);
      g.drawLine(x+4,y-4,x+4,y);
      g.drawLine(x-4,y-4,x+4,y-4);
      }
   public void paintGraceNote(double x, double y){
      paintGraceNote((int)x,(int)y);
	  }
   public void paintGraceSlash(double x, double y, boolean tailUp){};
	  
   public void paintGraceNote(int x, int y) {
      g.fillOval(x,y+1,quarterSize-2,spaceHeight-2);
      g.drawLine(x+quarterSize-3,y+spaceHeight/2,
                 x+quarterSize-3,y-5*spaceHeight/4);
      g.drawArc(x,y-3*spaceHeight/2+2,
                quarterSize,spaceHeight/2+2,70,-100);
      g.drawLine(x,y,x+6,y-6);
      }   
	  
   public void paintLeger(double x, double y){
      paintLeger((int) x, (int) y);
	  }
   public void paintLeger(int x, int y)
      {
      g.drawLine(x-2,y,x+quarterSize+2,y);
      }
             
   public void paintHeader(String title,double x, double y){
      paintHeader( title,(int) x, (int) y);
	  }
   public void paintHeader(String title,int x, int y)
      {
      g.setFont(titleFont);
      g.drawString(title, x, y);
      }
   public void paintComposer(String title,double x, double y){
      paintComposer(  title,(int) x, (int) y);
	  }
   public void paintComposer(String title,int x, int y)
      {
      g.setFont(infoFont);
      g.drawString(title, x, y);
      }
   public void paintLines( double xStart, double xEnd, double y){    
      paintLines( (int) xStart, (int) xEnd, (int) y) ;
	  }
	     
   public void paintLines( int xStart, int xEnd, int y)    
      {
      for (int i = 0; i < 5; i++)
	     {
         g.drawLine(xStart,y,xEnd,y);
	     y = y + spaceHeight;
	     }
	  }
  public void paintBreve(double x,double y){
      paintBreve((int) x,(int) y);
	  }
	  
  public void paintBreve(int x,int y){
      paintNoteHeadUnfilled(x,y);
      paintVertical(x-1,y);
      paintVertical(x+halfSize+1,y);
      }
   
   public void paintLonga(double x, double y){
	  paintLonga((int) x, (int) y);
      }
   public void paintLonga(int x, int y){
   //FIXME not implemented
      }
                              
   public void paintWhole(double x,double y){
      paintWhole((int) x,(int) y);
	  }
	  
   public void paintWhole(int x,int y){
      paintNoteHeadUnfilled(x,y);
      }
	  
   public void paintNoteHeadUnfilled(double x,double y){
      paintNoteHeadUnfilled((int) x,(int) y);
	  }

   public void paintNoteHeadUnfilled(int x,int y)
      {
      g.drawOval(x,y,halfSize,spaceHeight);
      }
   // short vertical lines (for breve, etc)
   public void paintVertical(double x, double y){
      paintVertical((int) x, (int) y);
      }
   public void paintVertical(int x, int y)
      {
      g.drawLine(x,y,x,y+spaceHeight);
      }   
   public void paintTrebleClef(double currentX, double currentY) {
	  paintTrebleClef((int) currentX, (int) currentY) ;
      }
   public void paintTrebleClef(int currentX, int currentY) 
      {   
      int baseC = currentY+6*spaceHeight;   
      g.fillOval(currentX,baseC-6,6,5); //bottom dot
      
      g.drawLine(currentX+4,baseC-2,currentX+spaceHeight+3,baseC-spaceHeight); //bottom diag line
      
      g.drawLine(currentX+spaceHeight+3,baseC-spaceHeight,
                 currentX+spaceHeight,currentY-spaceHeight);  //vertical
                 
      g.drawArc(currentX,currentY-spaceHeight,spaceHeight*2,  // top arc
                 2*spaceHeight,90,-180);
                
	  g.drawArc(currentX,currentY+spaceHeight,
	            5*spaceHeight/2,3*spaceHeight,90,180); // bottom arc
	  g.drawArc(currentX,currentY+2*spaceHeight,
	            2*spaceHeight+1,2*spaceHeight,90,-180); // tail arc
	            
	  }   

   public void paintSharp(double currentX, double y){
      paintSharp((int) currentX, (int) y);
	  }
   public void paintSharp(int currentX, int y)
      {
      g.drawLine(currentX,y-1,currentX,y+spaceHeight+1);
      g.drawLine(currentX+spaceHeight/2,y-1,
                 currentX+spaceHeight/2,y+spaceHeight);
      g.drawLine(currentX-spaceHeight/4,y+spaceHeight/4+1,
                 currentX+spaceHeight*3/4,y+spaceHeight/4-1);
      g.drawLine(currentX-spaceHeight/4,y+3*spaceHeight/4+1,
                 currentX+spaceHeight*3/4,y+3*spaceHeight/4-1);
	   
      }
   public void paintFlat(double currentX, double y) {
       paintFlat((int) currentX, (int) y);
	   }
   public void paintFlat(int currentX, int y) {
      g.drawLine(currentX,y-3,currentX,y+spaceHeight-1);
      g.drawArc(currentX,y+spaceHeight/2-2,spaceHeight/2,spaceHeight/2+1,90,-180);
      }
	  
   public void paintNatural(double currentX, double y) {
       paintNatural((int) currentX, (int) y);
	   }
   public void paintNatural(int currentX, int y) {
      g.drawLine(currentX,y-spaceHeight+1,currentX,y+spaceHeight-1);  //vertical
      g.drawLine(currentX+2*spaceHeight/3,y-1,currentX+2*spaceHeight/3,y+2*spaceHeight-1); //vertical
      g.drawLine(currentX,y+2,currentX+2*spaceHeight/3,y-1);
      g.drawLine(currentX,y+spaceHeight-1,currentX+2*spaceHeight/3,y+spaceHeight-3);
      }
   public void paintNoteHeadFilled(double currentX, double y) {
      paintNoteHeadFilled((int) currentX, (int) y);
	  }
	        
   public void paintNoteHeadFilled(int currentX, int y) {
      g.fillOval(currentX,y,quarterSize,spaceHeight);
      g.drawOval(currentX,y,quarterSize,spaceHeight);
      }
      
   public void paintDash(double x, double y){
      paintDash((int) x, (int) y);
      }
	  
   public void paintDash(int x, int y){
      g.drawLine(x-3,y,x+3,y);
      }
      
   public void paintHorizLine(double x1, double x2, double y){
      paintHorizLine((int )x1, (int) x2, (int) y);
      }
   public void paintHorizLine(int x1, int x2, int y){
      g.drawLine(x1,y,x2,y);
      }
      
   public void paintLyric(String word, double x, double y){
      paintLyric(  word, (int) x, (int) y);
	  }
   public void paintLyric(String word, int x, int y){
      x -= g.getFontMetrics(infoFont).stringWidth(word)/2;
      g.drawString(word,x,y);

      }

public void paintSystemBracket(double current, double save, double d) {
	// TODO Auto-generated method stub
	
}

public void paintVoiceName(double x, double y, String voiceName) {
	// TODO Auto-generated method stub
	
}
      
   }   