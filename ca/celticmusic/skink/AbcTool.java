//
//  AbcTool.java
//  5 Line Skink
//
//  Created by wil macaulay on Sun Jun 23 2002.
//  Copyright (c) 2002 Wil Macaulay All rights reserved.
//
package ca.celticmusic.skink;
import java.util.*;
/**
 * Describes the interface for tools (added to the Tools menu)
 */
public interface AbcTool {

/**
 * @returns the menu item string
 */
public String getMenuItem();

/**
 * @returns the toolTip
 */

public String getToolTip();
 
/**
 * does the action
 * @param selectedString 
 * @param selectedTunes
 * @returns the string to replace the selected string. Null if no replace 
 * @throws bad selection
 */
public String doTool(String selectedString,List selectedTunes) throws Exception;



}
