package ca.celticmusic.skink;

public class LayoutEntry {
	double width;
	double offset;
	boolean fixed; // fixed glyph or floating?
	String id = "";
	
	public LayoutEntry(double width2,boolean isFixed,String glyphID) {
		width = width2;
		offset = 0.0d;
		fixed = isFixed;
		id = glyphID;
	}

	public double update(double newWidth, String glyphID){
		double delta = newWidth - width;
		width = Math.max(width, newWidth);
		id = id + "*"+ glyphID;
		return Math.max(0.0, delta);
	}

	public void clear() {
		id = null;
		
	}
	public double getWidth(){
		return width;
	}
	public double getOffset(){
		return offset;
	}
	public boolean isFixed(){
		return fixed;
	}

	public void addPad(double pad) {
		width += pad;
		
	}

	public double setOffset(double offset2) {
		offset = offset2;
		return offset + width;
	}
	public String toString(){
		return ("LayoutEntry: w="+width+" o= "+offset 
				+ " fixed?" +fixed
				+ " id ["+id +"]");
	}

}
