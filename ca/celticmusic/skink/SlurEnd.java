/**
 * mark the point where a slur ends.
 *   
 *   History:
 *       1.0 WM 18 May 04 Make it a MarkupElement 
 */
 

package ca.celticmusic.skink;
public class SlurEnd extends MarkupElement {
	public SlurEnd(){
		setSymbol(")");
	}

	public String elementType(){
		return "slre";
	}

	public void paint(StaffPainter p, boolean advance){
	}
	public void updateLineContext(Line theLine){
		MusicElement last = (MusicElement) theLine.getLastAudibleElement();
		if (last != null){
			last.endSlur(1);
		}
	}
}
