/**
 *
 * A musical rest
 * @author Wil Macaulay 
 * @version 1.0 Mar 2 2002
 *
 *
 * History:  initial release Nov 1998  WM
 *           add player support Mar 2 2002
 */
 
package ca.celticmusic.skink;
public class RestElement extends MusicElement {

  public RestElement(){
      setSymbol("z");
      isAudible = true;
      }
      
  public String elementType(){
       return "rest";
      }
      
  public int play(Player player,int atTick, double stretch,KeySig curKey){
      int dur =(int) (absLength*stretch);
      return dur;
      }
      
  public int getMidi(){
      return -1;  // no pitch value, just duration
      }  
      
  public void paint(StaffPainter p, boolean advance){
      Debug.output(1,"painting a rest");
	  p.endBeam();
	  p.paintRest(this);
	  if (advance) p.advance(glyphWidth(),fixedAdvance, bpThis);
	  }  
  // Vertical offset for a rest is always zero (at mid line) no matter the clef
 
   public int findVerticalOffset(int midNote){
      return 0;
      }

   }
