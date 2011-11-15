/**
 *  Space ends a beam 
 *
 *   History:
 *        18 May 04 WM make it a MarkupElement
 */
 
package ca.celticmusic.skink;
public class SpaceElement extends MarkupElement
  {
  public SpaceElement()
     {
     setSymbol(" ");
   
     }
  public String elementType()
     {
     return "spac";
     }
     
  public void paint(StaffPainter panel, boolean advance){
     panel.endBeam();
     }
  
  public String asString(){
	  return ("Space element");
  }
   
  }
