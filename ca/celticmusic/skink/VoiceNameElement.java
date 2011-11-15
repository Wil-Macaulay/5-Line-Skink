package ca.celticmusic.skink;

public class VoiceNameElement extends MarkupElement {
	VoiceNameElement(String theName){
		setSymbol(theName);
	}
	public void paint(StaffPainter p, boolean advance){
		p.paintVoiceName(getContents());
	}  

	@Override
	public String elementType() {
		// TODO Auto-generated method stub
		return "Vnam";
	}

}
