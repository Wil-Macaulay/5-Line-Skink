/**
 * Invisible/inaudible rests
 * FIXME - audible invisible rests (y) are not handled
 * 19 aug 2003 wm audible invisible - inherit from RestElement
 */
package ca.celticmusic.skink;
public class InvisibleRestElement extends RestElement {

  public InvisibleRestElement() {
    isAudible = false;
    isVisible = false;
    }
  
  public void setType(String type){
    setSymbol(type);
    if (type.equals("x")){
       isAudible = true;
       }
    }
         
  public String elementType(){
    return "invr";
    }

  public int glyphWidth(){
    return 12;
    }
	
// if we're in a chord, we don't advance
  public void paint(StaffPainter p, boolean advance){
    if (advance) {
		p.setReference(this);  
	   p.advance(glyphWidth(),fixedAdvance, bpThis);
	   }
    } 
    
  public int play(Player player,int atTick, double stretch,KeySig curKey){
      int dur = 0;
      if (isAudible){
          dur =(int) (absLength*stretch);
          }
      return dur;
      }
    
}