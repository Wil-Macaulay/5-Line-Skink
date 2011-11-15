/////////////////////
//
//   MeterElement.java
//            Jan 31 2001 WM version 1.0
//              
//            clef element (like clef and key) are drawn but not 
//            explicit in abc
//
////////////////////

package ca.celticmusic.skink;
public class MeterElement extends AbstractMusicElement {
  Meter meter;
  public MeterElement(Meter m) {
     fixedAdvance = true;
     meter = m;
     setSymbol("%meter");         // should never be dumped
     }
  public MeterElement(String s) {
     meter = Meter.findMeter(s);
     fixedAdvance = true;
     setSymbol("M:"+s);
     }   
  public String elementType() {
     return "metr";
     }
  
  public Meter getMeter() {
     return meter;
     }   
  public int glyphWidth()  {
     return 25;
     }
  public boolean needsStaff(){
    return true;
    }
       
  public void doPaint(StaffPainter p, boolean advance){
    p.endBeam();
    p.paintMeter(meter);
    } 
  
	protected BarPosition updateBarPosition(BarPosition bpCurrent) {
		bpCurrent.resetPulse();
		//bpCurrent.incrementSequence(1);
		return bpCurrent;
	} 
	
	protected void setBarPosition(BarPosition bpCurrent){
		bpCurrent.incrementSequence(1);
		BarPosition bpNew = new BarPosition(bpCurrent); 
		bpThis = bpNew;
	}
  
 }
