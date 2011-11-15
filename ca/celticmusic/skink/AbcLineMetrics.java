//
//  AbcLineMetrics.java
//  5 Line Skink
//
//  Created by wil macaulay on Mon Mar 18 2002.
//  Copyright (c) 2002 Wil Macaulay. All rights reserved.
//
/**
 * accumulate metrics for layout lines
 *
 * @author Wil Macaulay
 * @version 1.0a2 18 mar 2002
 */
package ca.celticmusic.skink; 
public class AbcLineMetrics {
    int totGlyphWidth = 0;
    int numFixedGlyph = 0; 
    int numFloatGlyph = 0;
    double xMaxWidth = 0;
    int maxAlignedWords = 0;
	static final float FIXEDPAD = 3.0f;
	StaffMetrics theStaffMetrics = null;

    public void init(double max){
     totGlyphWidth = 0;
     numFixedGlyph = 0;
     numFloatGlyph = 0;
     xMaxWidth = max;
     maxAlignedWords = 0;
     }
    
      
    public int getMaxAlignedWords(){
      return maxAlignedWords;
      }
    
    public void setMaxAlignedWords(int maxAW){
       maxAlignedWords = maxAW;
       }
                      
    private void addGlyph(int width, int nGlyphs, boolean fixed){
	   Debug.output(1,"addGlyph: width= "+width+" number "+nGlyphs+ " fixed?"+fixed);
	   totGlyphWidth += (width);
       if (fixed){
		  numFixedGlyph+=nGlyphs;
          }
       else{
          numFloatGlyph+=nGlyphs;
          }
       }
   public void addGlyph(int width, boolean fixed, BarPosition bpCurrent, String glyphID){
     addGlyph(width,1,fixed);
     theStaffMetrics.addEntry(width,bpCurrent, fixed, glyphID);
     }
      

      
    public int getTotGlyphWidth(){
      return totGlyphWidth;
      }
  /**
   * pad width is calculated only from the number of floating glyphs
   * fixed width is also used in the calculation
   */
 

	public void setStaffMetrics(StaffMetrics metrics) {
		theStaffMetrics = metrics;
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public double getXoffset(BarPosition bpCurrent) {
		
		return theStaffMetrics.getX(bpCurrent);
	};
  

}
