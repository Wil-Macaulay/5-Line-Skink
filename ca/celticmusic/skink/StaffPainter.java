package ca.celticmusic.skink;

import java.util.List;

public interface StaffPainter {


	public abstract void advance(int glyphSize, boolean fixedAdvance, BarPosition bpCurrent);

	public abstract void paintMarkup(java.util.List<AbcElement> abcList);

	public abstract void paintMusicElements(java.util.List<AbcElement> abcList,
			boolean advance);

	public abstract void endBeam();

	public abstract void slurBegin(MusicElement m, int numSlurs);

	public abstract void slurEnd(MusicElement m, int numSlurs);

	public abstract void updateSlurContour(MusicElement m);

	public abstract void paintChord(MultiElement m);

	//public abstract void paintGuitarChord(String chord);

	public abstract void paintField(FieldElement f);

	public abstract void paintRest(RestElement r);

	// set a reference for decos    
	public abstract void setReference(MusicElement m);

	public abstract void paintNote(MusicElement m, boolean advance);

	public abstract void drawDecos(double x, double y, double yMax,
			double yMin, boolean tailUp, List<String> decorations,
			List<DecorationElement> extendedDecorations, 
			List<GuitarChordElement> guitarChordsUp, List<GuitarChordElement> guitarChordsLeft, List<GuitarChordElement> guitarChordsDown, List<GuitarChordElement> guitarChordsRight);

	public abstract void paintClef();

	public abstract void paintTuplet(double x1, double y1, double x2, double y2, int i);

	public abstract void paintDash(double curX, double curY);

	public abstract void paintLine(double lastX, double curX, double curY);

	public abstract void paintLyric(String word, double curX, double curY);

	public abstract void paintKeySig(KeySig theKey);

	public abstract void paintMeter(Meter meter);

	public abstract void paintNthEnding(NthRepeatElement element);

	public abstract void paintBarline(BarLineElement element);

	public abstract void paintRepeat(RepeatElement element);

	public abstract void paintVoiceName(String contents);

}