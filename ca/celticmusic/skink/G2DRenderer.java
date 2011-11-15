/**
 *
 * Renders music elements for a JDK 1.2 compatible
 * Graphics2D object.  
 * 
 * @author Wil Macaulay
 * @version 1.0a1 27 Feb 2002
 *
 * @see AWTRenderer
 * @since Mar 2000
 */
//////////////
//  
// Music renderer (AWT)
//       7 dec 2000 WM  fix treble clef
//       17 jan 2001 WM add breve support
//       0.9 22 jun 2001 wm multi tune printing
//       1.0a4 30 May 2002 wm don't draw outside clip region
//       1.0a5 7 June 2002 wm guitar chord layout
//       1.0e3 26 july 2003 wm better clef drawing using generalPath
//       1.0f1 11 aug use MusicSymbols for shapes
//	     1.0f1 19 sep 2003 use Beamer for grace notes
//       1.2a0 9 jun 2004 use doubles instead of ints
//       1.2a1 5 aug 2004 finish converting to doubles to support G2D
//
//////////////
package ca.celticmusic.skink;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

public class G2DRenderer implements Renderer
   {
   /**
    * set up various stroke widths
    */
   private static final float STAFF_LINE_WIDTH = (float) 0.7; 
   private static final float NOTE_STEM_WIDTH = (float) 0.7;
   private static final float BEAM_WIDTH = (float) 3.0;
   private static final float GRACE_BEAM_WIDTH = (float) 2.0;
   private static final float BARLINE_WIDTH = (float) 1.5;
   private static final float MID_WIDTH = (float) 2.0;
   private static final BasicStroke barlineStroke = 
               new BasicStroke(BARLINE_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final BasicStroke staffLineStroke =
               new BasicStroke(STAFF_LINE_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final BasicStroke horizontalAccidentalStroke =
               new BasicStroke(MID_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final BasicStroke verticalAccidentalStroke =
               new BasicStroke(NOTE_STEM_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final BasicStroke beamStroke =
               new BasicStroke(BEAM_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final BasicStroke graceBeamStroke =
               new BasicStroke(GRACE_BEAM_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
   private static final Font titleFont = new Font("SansSerif", Font.ITALIC, 16);
   private static final Font infoFont =  new Font("SansSerif", Font.PLAIN, 12);
   private static final Font lyricFont =  new Font("SansSerif", Font.PLAIN, 10);
   private static final Font meterFont = new Font("Serif", Font.BOLD, 14);
   private static final Font decoFont = new Font("Serif", Font.BOLD+Font.ITALIC, 12);
   private static final Font chordFont = new Font("SansSerif", Font.PLAIN, 12);
  
    /**
    * rendering graphics
    */
    
   Graphics2D g;
  
   
   public G2DRenderer() {
        
      }
      
/**
 * don't bother drawing if the symbol is off the page
 */
 
   private final boolean outOfBounds(double y){
      return ((y < g.getClipBounds().y - 100) || (y > g.getClipBounds().y+g.getClipBounds().height+100));
      }
          
   
   public void setGraphics(Graphics gr)
      {
      g = (Graphics2D) gr;
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

      Debug.output(4,"renderer setGraphics class:"+g.getClass().getName());
      Debug.output(4,"renderer - clipping region: "+g.getClipBounds());

      }
   
   public Graphics getGraphics() {
      Debug.output(4,"renderer getGraphics class:"+g.getClass().getName());
      return g;
      }
      
   public void paintTupletMark(double xFirst, double yFirst, 
                          double xLast, double yLast, 
                          int tupSize){                      
        if (outOfBounds(yFirst)) return;
        double slope = (yLast-yFirst)/(xLast-xFirst);
		  double xmid1 =  (xFirst+xLast)/2-3;
		  double xmid2 = (xFirst+xLast)/2+5.0;
		  double ymid1 = (xmid1 - xFirst)*slope + yFirst;
		  double ymid2	= (xmid2 - xFirst)*slope + yFirst;
        g.draw(new Line2D.Double(xFirst,yFirst,xmid1,ymid1));
        //g.draw(new Line2D.Double(xFirst,y,xFirst,y-3));
        g.draw(new Line2D.Double(xmid2,ymid2,xLast,yLast));
        //g.draw(new Line2D.Double(xLast,y,xLast,y - 3));
        String temp = ""+tupSize;
        g.setFont(infoFont);
        g.drawString(temp,(float)((xFirst+xLast)/2 - 2), (float)(yFirst+yLast)/2+2);
        }
       
        
   public void paintTail(double x,double y,double tailLen,boolean tailsUp,int numTails,double headWidth,boolean isGrace){
       if (outOfBounds(y)) return;
      // tail up
       if (tailsUp){
            g.draw(new Line2D.Double(x+headWidth, y+StaffPanel.spaceHeight/2,
                        x+headWidth, y-tailLen));
			paintFlagsUp(numTails,(x+headWidth),(y-tailLen),isGrace);			
            }          
       else {
            g.draw(new Line2D.Double(x, y+StaffPanel.spaceHeight/2,
                        x, y+tailLen));
			paintFlagsDown(numTails,x,(y+tailLen),isGrace);			
            }           
        }
  public void paintFlagsUp( int nFlags, double x, double y, boolean isGrace){
	  AffineTransform at = g.getTransform();
      g.translate(x,y);
	  if (isGrace) {
		 g.scale(0.7,0.7);
		 }
	  for (int i=0; i<nFlags; i++){
	     g.fill(MusicSymbols.getFlagUp());
		 g.translate(0,3.5);
		 }
      g.setTransform(at);
	  }
  public void paintGraceSlash(double x, double y, boolean tailUp){
	  AffineTransform at = g.getTransform();
	  g.translate(x,y);
	  g.drawLine(2,-2,8,-8);
	  g.setTransform(at);
	  }

  public void paintFlagsDown(int nFlags, double x, double y,  boolean isGrace){			
      AffineTransform at = g.getTransform();
      g.translate(x,y);
	  if (isGrace) g.scale(0.7,0.7);
	  for (int i=0; i<nFlags; i++){
	     g.fill(MusicSymbols.getFlagDown());
		 g.translate(0,-3.5);
		 }
      g.setTransform(at);
	  }

   /**
    * Paint the beam on beamed notes
    */
   public void paintBeam(double xStart,double yStart,double xEnd,double yEnd,boolean grace){
	  if (outOfBounds(yStart)) return;
	  float xS = (float) xStart;
	  float xE = (float) xEnd + NOTE_STEM_WIDTH;
	  float yS = (float) yStart;
	  float yE = (float) yEnd;
	  float deltaY = BEAM_WIDTH;
      if (grace){
		  deltaY = GRACE_BEAM_WIDTH;
          }
	  GeneralPath theBeam = new GeneralPath();
	  theBeam.moveTo(xS,yS);
	  theBeam.lineTo(xE,yE);
	  theBeam.lineTo(xE,yE+deltaY);
	  theBeam.lineTo(xS,yS+deltaY);	
	  theBeam.lineTo(xS,yS);  
	  g.fill(theBeam);	      
      }
   /**
    * Paint guitar chords and other arbitrary text
    */
       	   
   public void paintGuitarChord(double x, double y, String chord)
      {
      if (outOfBounds(y)) return;
      // set font, etc
      if (chord == null) return;
      char c = chord.charAt(0);
      //Debug.output(0,"chord position: "+c);
      Font saveFont = g.getFont();
      g.setFont(chordFont);
      double ypos = y;
      double xpos = x;
      switch(c){
         // above
         case '^':
            chord = chord.substring(1);
            break;
         // below
         case '_':
            chord = chord.substring(1);
            //ypos += 9*StaffPanel.spaceHeight;
            break;
         // before
         case '<':
            chord = chord.substring(1);
            xpos -= (StaffPanel.wholeSize + g.getFontMetrics(chordFont).stringWidth(chord));
           // ypos += 4*StaffPanel.spaceHeight;
            break;
         // after
         case '>':
            chord = chord.substring(1);
            xpos += StaffPanel.wholeSize * 2;
            //ypos += 4*StaffPanel.spaceHeight;
            break;
         // centered above
         case '@':
            chord = chord.substring(1);
            xpos -= g.getFontMetrics(chordFont).stringWidth(chord)/2;
            break;
         default:
            break;
            }   
      g.drawString(chord,(float)xpos,(float)ypos);
      g.setFont(saveFont);
      }
   public void paintStringDeco(double x, double y, String deco){
	   // centered at x,y
	   Font saveFont = g.getFont();
	   g.setFont(decoFont);
	   paintStringCentered(x,y,deco);
	   g.setFont(saveFont);
   }
   public void paintVoiceName(double x,double y,String VoiceName){
	   Font saveFont = g.getFont();
	   g.setFont(infoFont);
	   paintStringLeftJustified(x,y,VoiceName);
	   g.setFont(saveFont);
   }
   
   private void paintStringCentered(double x, double y, String theString) {
		double xpos = x - g.getFontMetrics(g.getFont()).stringWidth(theString)/ 2;
		g.drawString(theString, (float) xpos, (float) y);
	}
   
	private void paintStringLeftJustified(double x, double y, String theString){
		double xpos = x - g.getFontMetrics(g.getFont()).stringWidth(theString);
		g.drawString(theString,(float)xpos,(float)y);	
		}
	
   public void paintDash(double x, double y){
      if (outOfBounds(y)) return;
      g.draw(new Line2D.Double(x-1,y,x+2,y));
      }
      
   public void paintHorizLine(double x1, double x2, double y){
      if (outOfBounds(y)) return;
      g.draw(new Line2D.Double(x1,y,x2,y));
      }
      
   public void paintLyric(String word, double x, double y){
      if (outOfBounds(y)) return;
      x -= g.getFontMetrics(lyricFont).stringWidth(word)/2;
      g.setFont(lyricFont);
      g.drawString(word,(float)x,(float)y);

      }
      
                                            
   public void paintInfoString(double x, double y, String s)
      {
      // set font, etc
      if (outOfBounds(y)) return;
      Font saveFont = g.getFont();
      if(s != null && s.length() != 0){
         g.setFont(infoFont);
         g.drawString(s,(float)x,(float)y);
         }
      g.setFont(saveFont);
      }
	/**
	 * Render barlines
	 */

   private void paintDoubleBarline(){
	   g.fill(MusicSymbols.getBarLine());
		g.translate(5,0);
	   g.fill(MusicSymbols.getBarLine());
		}

   private void paintThickThinBarline(){
	   g.fill(MusicSymbols.getThickBarLine());
		g.translate(5,0);
	   g.fill(MusicSymbols.getBarLine());
		}

   private void paintThinThickBarline(){
		g.translate(-4,0);
	   g.fill(MusicSymbols.getBarLine());
		g.translate(4,0);
	   g.fill(MusicSymbols.getThickBarLine());
		}
   private void paintThinThickThinBarline(){
		g.translate(-4,0);
	   g.fill(MusicSymbols.getBarLine());
		g.translate(4,0);
	   g.fill(MusicSymbols.getThickBarLine());
		g.translate(5,0);
	   g.fill(MusicSymbols.getBarLine());
		}
   private void paintThinBarline(){
	   g.fill(MusicSymbols.getBarLine());
		}
          
   public void paintBarline(double x, double y, String type) {
      if (outOfBounds(y)) return;
		if("none".equals(type))return;
	  AffineTransform at = g.getTransform();
	  g.translate(x,y);
	  if ("double".equals(type)){
			paintDoubleBarline();
			}
	  else if("thinthick".equals(type)){
	      paintThinThickBarline();
			}
	  else if("thickthin".equals(type)){
			paintThickThinBarline();
			}
	  else if("thinthickthin".equals(type)){
			paintThinThickThinBarline();
			}
	  else{
			paintThinBarline();
			}
	  g.setTransform(at);
	  }
   public void paintNthEnding(double x, double y, String repeatString) 
      {        
      if (outOfBounds(y)) return;
      g.draw(new Line2D.Double(x,y-StaffPanel.spaceHeight/2,x,y-StaffPanel.spaceHeight*4));
      g.draw(new Line2D.Double(x,y-StaffPanel.spaceHeight*4,
                 x+StaffPanel.spaceHeight,y-StaffPanel.spaceHeight*4));
      g.setFont(infoFont);
      g.drawString(repeatString,(float)x+2,(float)(y-StaffPanel.spaceHeight*2-1));
      }
      
   public void paintDot(double x, double y)  
      { 
      if (outOfBounds(y)) return;
	  AffineTransform at = g.getTransform();
	  g.translate(x,y);
      g.fill(MusicSymbols.getSmallDot());
	  g.setTransform(at);
      }
      
   public void paintNumericMeter(double x, double y, String num, String denom)
      {   
          g.setFont(meterFont);
	  g.drawString(num,(float)x, (float)(y+2*StaffPanel.spaceHeight-1) );
	  g.drawString(denom,(float)x,(float)( y+4*StaffPanel.spaceHeight-1));
	  }
	  
   public void paintSpecialMeter(double x, double y, String s)
      {	
      if (outOfBounds(y)) return;
      String s1 = s.trim().toUpperCase(); 
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getCommonTime());	  
	  if (s1.equals("C|"))
	      {
	      g.draw(new Line2D.Double(0.0,StaffPanel.spaceHeight/2,
	                       0.0, 7*StaffPanel.spaceHeight/2));
          }
       g.setTransform(at);
     } 
   public void paintThirtysecondRest(double x,double y)
      {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getThirtysecondRest());
      g.setTransform(at);
      }
   public void paintSixteenthRest(double x,double y)
      {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getSixteenthRest());
      g.setTransform(at);
      }
      
   public void paintEighthRest(double x,double y)
      {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getEighthRest());
      g.setTransform(at);
      }
      
   public void paintQuarterRest(double x, double y)
      {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getQuarterRest());
      g.setTransform(at);
      }
      
   public void paintHalfRest(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.fill(new Rectangle2D.Double(x,y,StaffPanel.quarterSize,StaffPanel.spaceHeight/2));
      }
      
   public void paintWholeRest(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.fill(new Rectangle2D.Double(x,y-StaffPanel.spaceHeight/2,StaffPanel.halfSize,StaffPanel.spaceHeight/2));
      }
   public void paintBreveRest(double x, double y)
      {
      if (outOfBounds(y)) return;

      g.fill(new Rectangle2D.Double(x,y-StaffPanel.spaceHeight/2,StaffPanel.quarterSize,StaffPanel.spaceHeight));
      }
   public void paintLongaRest(double x, double y)
      {
      if (outOfBounds(y)) return;

      g.fill(new Rectangle2D.Double(x,y-StaffPanel.spaceHeight/2,(StaffPanel.quarterSize*2),StaffPanel.spaceHeight));
      }
      
   public void paintTie(double tieStart,double y, double tieEnd, boolean tailsUp)
      {
      if (outOfBounds(y)) return;
	  float xstart,xend,ystart,yend,xcp1,xcp2,ycp1,ycp2;
	  xstart = (float)tieStart;
	  xend = (float)(tieEnd+StaffPanel.quarterSize/2);
	  xcp1 = xstart+(xend-xstart)*0.3f;
	  xcp2 = xstart+(xend-xstart)*0.6f;
	  ystart = (float)(y+StaffPanel.spaceHeight/2);
	  yend = ystart;
	  
      if (tailsUp){
		 ycp1 = (float)(y+StaffPanel.spaceHeight*1.5);
		 ycp2 = ycp1+1;
		 }
	  else	{
		 ycp1 = (float)(y-StaffPanel.spaceHeight*1.5);
		 ycp2 = ycp1 -1;
		 }
	  GeneralPath theTie = new GeneralPath();
	  theTie.moveTo(xstart,ystart);
	  theTie.curveTo(xcp1,ycp1,xcp2,ycp1,xend,yend);
	  theTie.curveTo(xcp2,ycp2,xcp1,ycp2,xstart,ystart);
	  g.fill(theTie);				
	  } 
	  
   public void paintSlur(double xstart, double ystart, 
                         double xcp1, double ycp1, 
						 double xcp2, double ycp2, double xend, double yend){
	  GeneralPath theSlur = new GeneralPath();
	  theSlur.moveTo((float)xstart,(float)ystart);
	  theSlur.curveTo((float)xcp1,(float)ycp1,(float)xcp2,(float)ycp1,(float)xend,(float)yend);
	  theSlur.curveTo((float)xcp2,(float)ycp2,(float)xcp1,(float)ycp2,(float)xstart,(float)ystart);
	  g.fill(theSlur);				
	}

   public void paintSlur(double x1, double y, double x2, boolean tailsUp)
      {
      if (outOfBounds(y)) return;
      paintTie(x1,y,x2-StaffPanel.quarterSize,tailsUp) ;
      }
		
	public void paintSegno(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getSegno());
      g.setTransform(at);
	   }

	public void paintCoda(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getCoda());
      g.setTransform(at);
		}
	public void paintSlide(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getSlide());
      g.setTransform(at);
		}
	public void paintUpperMordent(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getUpperMordent());
      g.setTransform(at);
		}
	public void paintAccent(double x, double y){
	   if (outOfBounds(y)) return;
		g.draw(new Line2D.Double(x,y+StaffPanel.spaceHeight/2,x+StaffPanel.quarterSize,y));
		g.draw(new Line2D.Double(x,y-StaffPanel.spaceHeight/2,x+StaffPanel.quarterSize,y));
	   };
		
	public void paintLowerMordent(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getUpperMordent());
		g.draw(new Line2D.Double(0,2,0,-6));
      g.setTransform(at);
		}
		
	public void paintLongPhrase(double x, double y){
	   if (outOfBounds(y)) return;
	   g.draw(new Line2D.Double(x-StaffPanel.quarterSize,y,x-StaffPanel.quarterSize,y+4*StaffPanel.spaceHeight));
	   }
		
	public void paintShortPhrase(double x, double y){
	   if (outOfBounds(y)) return;
	   g.draw(new Line2D.Double(x-StaffPanel.quarterSize,y,x-StaffPanel.quarterSize,y+2*StaffPanel.spaceHeight));
		}
		
	public void paintMediumPhrase(double x, double y){
	   if (outOfBounds(y)) return;
	   g.draw(new Line2D.Double(x-StaffPanel.quarterSize,y,x-StaffPanel.quarterSize,y+3*StaffPanel.spaceHeight));
		}
		
	public void paintFermata(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getFermata());
      g.setTransform(at);
		}

	public void paintInvertedFermata(double x, double y){
	   if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
		g.translate(0,-7.5);
		g.scale(1,-1);
      g.fill(MusicSymbols.getFermata());
      g.setTransform(at);
		}
   	   
   public void paintTrill(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.setFont(decoFont);
      g.drawString("tr",(float)x,(float)y);
      }
   public void paintRoll(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.setFont(meterFont);
      g.drawString("~",(float)x,(float)y);
      }
      
   public void paintUpBow(double x, double y)
      {
      if (outOfBounds(y)) return;
      x+=StaffPanel.quarterSize/2;
      g.draw(new Line2D.Double(x-3,y-4,x,y)); 
      g.draw(new Line2D.Double(x+3,y-4,x,y)); 
      }
   
   public void paintDownBow(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.draw(new Line2D.Double(x-4,y-4,x-4,y));
      g.draw(new Line2D.Double(x+4,y-4,x+4,y));
      g.draw(new Line2D.Double(x-4,y-4,x+4,y-4));
      }
   public void paintGraceNote(double x, double y) {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x+0.7,y+0.7);
      g.scale(0.7,0.7);
      g.fill(MusicSymbols.getFullHead());
      g.setTransform(at);
      }
 
  public void paintLeger(double x, double y){
      if (outOfBounds(y)) return;
      Stroke s = g.getStroke();
      g.setStroke(staffLineStroke);
      g.draw (new Line2D.Double(x-2,y,x+StaffPanel.quarterSize+2,y));
      g.setStroke(s);
      }
             
   public void paintHeader(String title,double x, double y)
      {
      if (outOfBounds(y)) return;
      g.setFont(titleFont);
      g.drawString(title, (float)x, (float)y);
      }
   public void paintComposer(String title,double x, double y)
      {
      if (outOfBounds(y)) return;
      g.setFont(infoFont);
      g.drawString(title, (float)x, (float) y);
      }
      
   /**
    *  Paint the staff lines
    */
       
   public void paintLines( double xStart, double xEnd, double y)    
      {
      if (outOfBounds(y)) return;
      Stroke s = g.getStroke();
      g.setStroke(staffLineStroke);
      for (int i = 0; i < 5; i++)
	     {
             g.draw (new Line2D.Double(xStart,y,xEnd,y));
	     y = y + StaffPanel.spaceHeight;
	     }
      g.setStroke(s);       
      }
   /**
    * Paint the note heads (half, whole)
    */
       
   public void paintWhole(double x, double y){
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getWholeNoteHead());
      g.setTransform(at);
      }
   public void paintBreve(double x, double y){
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getBreveNoteHead());
      g.setTransform(at);
      }
   public void paintLonga(double x, double y){
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getLonga());
      g.setTransform(at);
      }
   
   public void paintNoteHeadUnfilled(double x,double y) {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getHalfNoteHead());
      g.setTransform(at);
      }
   // short vertical lines (for breve, etc)
   public void paintVertical(double x, double y)
      {
      if (outOfBounds(y)) return;
      g.draw( new Line2D.Double(x,y,x,y+StaffPanel.spaceHeight));
      }   
	  
   public void paintSystemBracket(double x, double yStart, double yEnd){
	   Stroke savedStroke = g.getStroke();
	   g.setStroke(beamStroke);
	   g.draw( new Line2D.Double(x,yStart,x,yEnd));
	   g.setStroke(savedStroke);
   }
   public void paintTrebleClef(double currentX, double currentY)  {   
      if (outOfBounds(currentY)) return;
      double baseC = currentY+6*StaffPanel.spaceHeight;  
      AffineTransform at = g.getTransform();
      g.translate(currentX+10,baseC-12);
      g.fill(MusicSymbols.getTrebleClef());
      g.setTransform(at);
                   
      } 
   public void paintBassClef(double currentX, double currentY)  {   
      if (outOfBounds(currentY)) return;
      double baseC = currentY+6*StaffPanel.spaceHeight;  
      AffineTransform at = g.getTransform();
      g.translate(currentX+10,baseC-12);
      g.fill(MusicSymbols.getBassClef());
      g.setTransform(at);
                   
      } 
            
   public void paintAltoClef(double currentX, double currentY) {
	// TODO Auto-generated method stub
	      if (outOfBounds(currentY)) return;
	      double baseC = currentY+6*StaffPanel.spaceHeight;  
	      AffineTransform at = g.getTransform();
	      g.translate(currentX+10,baseC-12);
	      g.fill(MusicSymbols.getCClef());
	      g.setTransform(at);
	
}

public void paintTenorClef(double currentX, double currentY) {
	// TODO Auto-generated method stub
    if (outOfBounds(currentY)) return;
    double baseC = currentY+6*StaffPanel.spaceHeight;  
    AffineTransform at = g.getTransform();
    g.translate(currentX+10,baseC-18); // move up one space
    g.fill(MusicSymbols.getCClef());
    g.setTransform(at);
	
}

/**
    * paint sharps for accidentals or keysignatures.
    */
   public void paintSharp(double currentX, double y){
      if (outOfBounds(y)) return;
      Stroke s = g.getStroke();
      // vertical strokes
    
      g.setStroke(verticalAccidentalStroke);
      g.draw(new Line2D.Double(currentX+1,(y-StaffPanel.spaceHeight+1),
                currentX+1,(y+2*StaffPanel.spaceHeight)));
      g.draw(new Line2D.Double((currentX+StaffPanel.spaceHeight-1),(y-StaffPanel.spaceHeight),
                 (currentX+StaffPanel.spaceHeight-1),(y+2*StaffPanel.spaceHeight-1)));
      // horizontal strokes
      g.setStroke(horizontalAccidentalStroke);           
      g.draw(new Line2D.Double(currentX-StaffPanel.spaceHeight/4,y+StaffPanel.spaceHeight/4,
                 currentX+StaffPanel.spaceHeight*5/4,y+StaffPanel.spaceHeight/4-2));
      g.draw(new Line2D.Double(currentX-StaffPanel.spaceHeight/4,y+3*StaffPanel.spaceHeight/4+2,
                 currentX+StaffPanel.spaceHeight*5/4,y+3*StaffPanel.spaceHeight/4));
      g.setStroke(s);	   
      }
   /**
    * paint flats for accidentals or keysignatures.
    */ 
       
   public void paintFlat(double currentX, double y)
      {
      if (outOfBounds(y)) return;

      Stroke s = g.getStroke();
      g.setStroke(verticalAccidentalStroke);
      g.draw(new Line2D.Double(currentX,y-3*StaffPanel.spaceHeight/2+2,currentX,y+StaffPanel.spaceHeight+3));
      g.setStroke(horizontalAccidentalStroke);
	  g.draw(new QuadCurve2D.Double(currentX,y+StaffPanel.spaceHeight+3,
			 currentX+StaffPanel.spaceHeight,y,currentX,y));
      g.setStroke(s);     
      }
   /**
    * paint naturals
    */
    
   public void paintNatural(double currentX, double y)
      {
      if (outOfBounds(y)) return;

      Stroke s = g.getStroke();
      // verticals
      g.setStroke(verticalAccidentalStroke);
      g.draw(new Line2D.Double(currentX,y-StaffPanel.spaceHeight+1,
                 currentX,y+StaffPanel.spaceHeight-1));  
      g.draw(new Line2D.Double(currentX+2*StaffPanel.spaceHeight/3,y-1,
                 currentX+2*StaffPanel.spaceHeight/3,y+2*StaffPanel.spaceHeight-1)); 
      // horizontals           
      g.setStroke(horizontalAccidentalStroke);
      g.draw(new Line2D.Double(currentX,                           y+1,
                 currentX+2*StaffPanel.spaceHeight/3,y-1));
      g.draw(new Line2D.Double(currentX,                           y+StaffPanel.spaceHeight-1,
                 currentX+2*StaffPanel.spaceHeight/3,y+StaffPanel.spaceHeight-3));
      g.setStroke(s);
      }
      
   public void paintNoteHeadFilled(double x , double y)
      {
      if (outOfBounds(y)) return;
      AffineTransform at = g.getTransform();
      g.translate(x,y);
      g.fill(MusicSymbols.getFullHead());
      g.setTransform(at);
      }
 
   
   }   