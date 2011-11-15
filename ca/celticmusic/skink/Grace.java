/********
 *
 * Grace.java - set of grace notes
 *
 ***********/
package ca.celticmusic.skink;

public class Grace extends MultiElement {
	String tupletSpec = "";
	
	/**
	 * A series of gracenotes
	 *
	 */
	public Grace() {
		startDelim = "{";
		endDelim = "}";
	}
	
	public String elementType() {
		return "grce";
	}
	
	public void updateLineContext(Line theLine) {
	}
	
	public void paint(StaffPainter p, boolean advance) {
	}
/**
 *  Update player context - null for now
 */	
	public void updatePlayerContext(Player player) {
	}
}
