package ca.celticmusic.skink;

public class GuitarChordElement extends MarkupElement {
	public GuitarChordElement(){
		setSymbol("\"\"");
		fixedAdvance = true;
	}

	// includes quotes     
	public void setChord(String theString) {
		setSymbol(theString);
	}

	// no quotes 

	public String getContents() {
		return symbol.substring(1, symbol.length() - 1);
	}

	public String elementType() {
		return "gchd";
	}
// painting is done as a deco
	public void paint(StaffPainter p, boolean advance) {
		//p.paintGuitarChord(getContents());
	}

}
