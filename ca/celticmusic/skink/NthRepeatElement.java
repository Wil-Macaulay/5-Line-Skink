package ca.celticmusic.skink;
public class NthRepeatElement extends RepeatElement
  {
  public String repeatNum;
  
  public String elementType()
      {
      return "nend";
      }
  public void setSymbol(String theSym) // sets up endrepeat (if necessary)
      {
      Debug.output(3,"nth repeat: setting symbol "+theSym);
      super.setSymbol(theSym);
		//FIXME should be in parser
		if (theSym.substring(0,1).equals("[")){
		   type="none";
			}
		else{
		   type="single";
			}	
      }
      
  public void setRepeatNumber(){    
      // cheat here! just take the last character as the repeat number
      String theSym = getSymbol();
      repeatNum = theSym.substring(theSym.length()-1,theSym.length());
      setDirection();
      }
  public void doUnroll(Unroller u)
      {
      if (endRepeat){
        u.copySub("beginRepeat","endRepeat");  
		u.forget("endRepeat");
        u.remember("beginRepeat"); // may not be quite right 
        } 
      else {  // kludge
        u.remember("endRepeat");
        }    
      }
   public void doPaint(StaffPainter p, boolean advance){
     p.endBeam();
     p.paintNthEnding(this);
     }   
   public int glyphWidth(){
     
     return (super.glyphWidth()+8);
     }
  }
  
