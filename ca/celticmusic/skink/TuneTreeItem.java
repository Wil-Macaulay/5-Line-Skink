//
//  TuneTreeItem.java
//  5 Line Skink
//
//  Created by wil macaulay on Thu Jul 18 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
/** defines items that can be in the tune tree 
 * @since 1.0b2
 */
 
package ca.celticmusic.skink;
public interface TuneTreeItem {
/**
 * indicates that the item is in error
 */
public boolean isErrorDetected();

/**
 * returns the line number of the start of the item
 */
 
public int getStartLine();

/**
 * returns the column number of the start of the item
 */
 
public int getStartColumn();
 
    

}
