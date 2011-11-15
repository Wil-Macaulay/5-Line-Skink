package ca.celticmusic.skink;
public class RepeatElement extends BarLineElement
  {
  public boolean beginRepeat = false;
  public boolean endRepeat = false;

  public RepeatElement()
      {
      }
      
  public String elementType()
      {
      return "repe";
      }
      
  public void setSymbol(String theSym)
      {
      super.setSymbol(theSym);
      Debug.output(2,"RepeatElement: setting Symbol:"+theSym);
      isDouble = false;
      }
  public void doUnroll(Unroller u)
      {
      if (endRepeat){
        //u.remember("endRepeat");
        u.copySub("beginRepeat","endRepeat"); 
		u.forget("endRepeat"); // clear previous end repeat FIXME not good for |1,3  :|2,4 case
        u.remember("beginRepeat"); // may not be quite right 
        }   
      // if it's begin/end repeat, need to copySub before resetting beginRepeat  
      if (beginRepeat){
        u.remember("beginRepeat");
		u.forget("endRepeat");
        }
      }
        
  public void setDirection()
      {    
      // cheat here!
      String theSym=getSymbol();
      String temp = theSym.substring(0,1);
      if (temp.equals(":")){
		    endRepeat = true;
			 type = "thinthick";
			 refPoint =4;
			 }
      temp = theSym.substring(1,2);
      if (temp.equals(":")) {
			 beginRepeat = true;
			 if(endRepeat) {
				type = "thinthickthin";
				refPoint = 0;
				}
			 else {
				type = "thickthin";
				refPoint = 0;
				}
			 }
      Debug.output(1,"Begin repeat: "+beginRepeat+" end repeat "+endRepeat);
	
      }
  
  public int glyphWidth()
      {
      int wid = super.glyphWidth();
      if (beginRepeat) {
        wid+=18;
        }
      if (endRepeat){  
        wid+=  13;
        }
      return wid;  
      }
  public void doPaint(StaffPainter p, boolean advance){
    p.endBeam();
    p.paintRepeat(this);
    }    

  }
  
