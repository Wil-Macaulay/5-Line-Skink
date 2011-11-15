/**
 * beginning a slur
 *  History:
 *      1.0f2 WM make it a markup element - no contribution to line width
 */
 
package ca.celticmusic.skink;
public class SlurBegin extends MarkupElement
  {
     public SlurBegin(){
       setSymbol("(");
       }
  public String elementType()
      {
      return "slrb";
      }
  public void paint(StaffPainter p, boolean advance){
     }
  public void updateLineContext(Line theLine){
     theLine.setSlurStarting();
	 }	
	  
  }
