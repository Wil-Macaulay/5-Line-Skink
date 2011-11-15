package ca.celticmusic.skink;
public class BarLineElement extends AbstractMusicElement
{
	int refPoint = 0; // reference point
	public String type = "single"; 
	public boolean isDouble = false;
	public BarLineElement()
	{
		setSymbol("|");
	}
	public int getAbsLength(){
		return 0;
	}     
	public String elementType() {
		return "barl";
	}
	public void setSymbol(String s) {
		super.setSymbol(s);
		if (s.length()==2){
			type = "double";
			isDouble = true;
		}
		if("[|".equals(s)){
			type="thickthin";
			refPoint = 0;
		}
		if("|]".equals(s)){
			type="thinthick";
			refPoint = 5;
		}	 
	}         
	public int getReferencePoint(){
		return refPoint;
	}
	public int glyphWidth() {
		if (isDouble) return 7;
		return 1;
	}
	// pre-updated by setBar Position
	protected BarPosition updateBarPosition(BarPosition bpCurrent){
		BarPosition bpNew = new BarPosition(bpCurrent); 
		return bpNew;
	}
	public void updatePlayerContext(Player player){
		player.clearAccidentals();
	}
	public boolean needsStaff(){
		return true;
	}   
	/**
	 * update line context 
	 */
	public void updateLineContext(Line theLine){	    
//		ornaments?
		myOrnaments = theLine.getOrnaments();
		theLine.setOrnaments(null);	
		myDecorations = theLine.getDecorations();
	}

	public void doUnroll(Unroller u){
		u.advance(this);
		u.resetBar();
		if (isDouble) {
			u.remember("beginRepeat");
			u.forget("endRepeat");  // don't allow end repeat to be remembered across double barline
		}
	}

	public void doPaint(StaffPainter p,boolean advance){
		p.endBeam();
		p.paintBarline(this);
	} 
	protected void setBarPosition(BarPosition bpCurrent){	 
		bpCurrent.incrementBar(1);
		BarPosition bpNew = new BarPosition(bpCurrent);
		bpThis = bpNew;
	}

}
