/**
 * 
 */
package ca.celticmusic.skink;

/**
 * @author wil macaulay
 * @version 2.0e2 30 Jan 2008
 * Layout staves vertically
 *
 */
import java.util.*;
public class StaffMetrics {
	TreeMap<BarPosition,LayoutEntry> layoutTree = null;
	ArrayList<AbcLineMetrics> theMetrics = null; 
	double widTotal; // running total
	double widFixed;
	double widFloat;
	
	int numEntries = 0;
	int numFloat = 0;
	int numFixed = 0;
	int numDuplicates = 0;
	int debugLevel = 2;
	
	public StaffMetrics(int numVoices){
		theMetrics = new ArrayList<AbcLineMetrics>(numVoices);
		layoutTree = new TreeMap<BarPosition,LayoutEntry>();
		// create line metrics for each voice 
		// ensure they share the layout tree
		
	}
	public void dump(){
		Debug.output(debugLevel,"-+::::+-");
		Debug.output(debugLevel,"StaffMetrics.dump: entries "+numEntries
				+" Duplicates" + numDuplicates
				+" Floating" + numFloat + " float width " + widFloat
				+" Fixed" + numFixed + " fixed width " + widFixed
 				+" total glyph width " + widTotal);
		
		Debug.output(debugLevel,"-+::::+-");
		for (Map.Entry<BarPosition,LayoutEntry> entry : layoutTree.entrySet()){
			Debug.output(debugLevel, "entry: "+ entry);
			
		}
		Debug.output(debugLevel,"-+::::+-");
		
	}
	/**
	 * Update after all lines have been scanned
	 * 
	 * 
	 */
	public void update(double xMaxWidth) {
		if (debugLevel == 0) {
			Debug.output(debugLevel, "StaffMetrics.update: updating with max width "
					+ xMaxWidth);
			
		}
		double pad = 0;
		widTotal = widFloat + widFixed;
		if (xMaxWidth > widTotal){
			if (numFloat < 2){
				pad = xMaxWidth - widTotal;
			}
			else{
				pad = (xMaxWidth - widTotal)/(numFloat-1);
			}
		}
		double offset = 0.0d;
		for (LayoutEntry e: layoutTree.values()){
			if (!e.isFixed()){
				e.addPad(pad);
			}
			offset = e.setOffset(offset);

		}



		if (debugLevel == 0) dump();	
	}

	
	/**
	 * Initialize for reuse - make sure dependent objects
	 * can be GC'd
	 */
	public void init() {
		widTotal = 0;
		widFixed = 0;
		widFloat = 0;
		numEntries = 0;
		numDuplicates = 0;
		numFloat = 0;
		numFixed = 0;
		for (LayoutEntry l : layoutTree.values() ){
			l.clear();
		}
		layoutTree.clear();
		for (AbcLineMetrics m : theMetrics){
			m.clear();
		}
		theMetrics.clear();
	}


	public void addLineMetrics(AbcLineMetrics lineMetrics) {
		theMetrics.add(lineMetrics);
		lineMetrics.setStaffMetrics(this);
		
	}
	
	public void addEntry(int width, BarPosition bpCurrent, boolean isFixed, String glyphID) {
		// does an entry already exist for this BP?
		//NOTE there is an assumption that there can never be a float and a fixed
		// glypth at the same barpos
		if (!layoutTree.containsKey(bpCurrent)) {
			makeNewEntry(width, bpCurrent, isFixed,glyphID);
		} else {
			updateExistingEntry(width, bpCurrent, isFixed,glyphID);
		}

	}
	/**
	 * @param width
	 * @param bpCurrent
	 * @param isFixed
	 */
	private void updateExistingEntry(int width, BarPosition bpCurrent, boolean isFixed, String glyphID) {
		numDuplicates++;
		LayoutEntry theEntry = layoutTree.get(bpCurrent);
		if (isFixed){
			widFixed += theEntry.update(width, glyphID);
		}
		else{
			widFloat += theEntry.update(width, glyphID);
		}
	}
	/**
	 * @param width
	 * @param bpCurrent
	 * @param isFixed
	 */
	private void makeNewEntry(int width, BarPosition bpCurrent, boolean isFixed,String glyphID) {
		LayoutEntry newEntry = new LayoutEntry(width,isFixed,glyphID);
		// clone the key so nobody screws around with it afterwards
		layoutTree.put(new BarPosition(bpCurrent), newEntry);

		numEntries++;
		if (!isFixed) {
			numFloat++;
			widFloat += width;
		}
		else{
			numFixed++;
			widFixed += width;
		}
	}
	public double getX(BarPosition bpCurrent) {
		double xPos = 0;
		Debug.output(debugLevel, "StaffMetrics.getX: current BP"+bpCurrent);
		if (bpCurrent != null && layoutTree.containsKey(bpCurrent)){
			LayoutEntry theEntry = layoutTree.get(bpCurrent);
			xPos = theEntry.getOffset();
		}
		return xPos;
	}

	
	/**
	 * 
	 */
}
