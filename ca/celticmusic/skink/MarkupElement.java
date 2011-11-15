/**
 * A MarkupElement doesn't take up space on a staff 
 *  History:
 *        1.0f2 WM initial release
 */

package ca.celticmusic.skink;
public abstract class MarkupElement extends AbstractMusicElement{
	/** 
	 * no glyph contribution
	 */
	public int glyphWidth() {
		return 0;
	}

	/**
	 * don't need a staff
	 */

	public boolean needsStaff(){
		return false;
	}   
	/**
	 * Get painting metrics 
	 */
	public  BarPosition getMetrics(AbcLineMetrics m,BarPosition bpCurrent){ 
		Debug.output(debugLevel, "MarkupElement.getMetrics: "
				+this.toString() + " bpCurrent ="
				+bpCurrent);
		setBarPosition(bpCurrent);
		
		return updateBarPosition(bpCurrent);
	}
	/**
	 * nothing to play
	 */

	public int play(Player player,int atTick, double stretch,KeySig curKey){
		return 0;
	}


	protected BarPosition updateBarPosition(BarPosition bpCurrent) {
		return bpCurrent;
	} 
	
	protected void setBarPosition(BarPosition bpCurrent){
		BarPosition bpNew = new BarPosition(bpCurrent); 
		bpThis = bpNew;
	}

}