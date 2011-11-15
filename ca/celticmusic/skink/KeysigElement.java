/////////////////////

//KeysigElement.java
//Jan 31 2001 WM version 1.0

//keysig element (like meter and clef) are drawn but not 
//explicit in abc



package ca.celticmusic.skink;
public class KeysigElement extends AbstractMusicElement {
	KeySig theKey;

	public KeysigElement(KeySig k)  {
		fixedAdvance = true;
		theKey = k;
		setSymbol("%keysig");         // should never be dumped
	}
	public KeysigElement(String s) {
		fixedAdvance = true;
		theKey = KeySig.findKey(s);
		setSymbol("K:"+s);
	}   
	public String elementType(){
		return "kysg";
	}
	public KeySig getKeySig() {
		return theKey;
	}

	public void updatePlayerContext(Player player){
		player.setKeySig(theKey);
	}             
	public int glyphWidth(){
		return (7*theKey.getNumSF()+10);
	}
	public void doPaint(StaffPainter p, boolean advance){
		p.endBeam();

		p.paintKeySig(theKey);
	}   
	public boolean needsStaff(){
		return true;
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
