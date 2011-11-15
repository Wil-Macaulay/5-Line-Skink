/////////////////////

//ClefElement.java
//Jan 31 2001 WM version 1.0

//clef element (like meter and key) are drawn but not 
//explicit in abc



package ca.celticmusic.skink;
public class ClefElement extends AbstractMusicElement{

	public ClefElement(){
		setSymbol("%clef");         // should never be dumped
		fixedAdvance = true;
	}

	public String elementType(){
		return "clef";
	}

	public int glyphWidth(){
		return 25;
	}

	public boolean needsStaff(){
		return true;
	} 

	public int getAbsLength(){
		return 0;
	}  

	public void doPaint(StaffPainter p, boolean advance){
		p.endBeam();
		p.paintClef();
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
