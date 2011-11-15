//
//  LengthElement.java
//  5 Line Skink
//
//  Created by wil macaulay on Wed Jan 22 2003.
//  Copyright (c) 2003 wil macaulay. All rights reserved.
//
package ca.celticmusic.skink;

/**
 * default note length
 * @version 1.0 in 1.0e2  
 */

public class LengthElement extends AbstractMusicElement {
	int defaultNote = SkinkConstants.DEFAULT_NOTE;

	public LengthElement(int deflen) {

		setSymbol("%len");
		if (deflen > 0) {
			defaultNote = deflen;
		}
	}

	public int getDefaultNote() {
		return defaultNote;
	}

	public int getDefaultDuration() {
		return SkinkConstants.WHOLE_LEN / defaultNote;
	}

	public int glyphWidth() {
		return 0;
	}

	public boolean needsStaff() {
		return false;
	}

	public String elementType() {
		return "leng";
	}
	protected BarPosition updateBarPosition(BarPosition bpCurrent) {
		return  new BarPosition(bpCurrent);

	} 
	protected void setBarPosition(BarPosition bpCurrent){
		BarPosition bpNew = new BarPosition(bpCurrent); 
		bpThis = bpNew;
	}
}
