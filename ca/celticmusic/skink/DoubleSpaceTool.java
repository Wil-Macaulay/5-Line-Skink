//
//  DoubleSpaceTool.java
//  5 Line Skink
//
//  Created by wil macaulay on Sun Jun 23 2002.
//  Copyright (c) 2001 Wil Macaulay All rights reserved.
//
package ca.celticmusic.skink;
import java.util.*;
public class DoubleSpaceTool implements AbcTool {

/**
 * @returns the menu item string
 */
public String getMenuItem(){
   return "Remove double spacing";
   }

/**
 * @returns the toolTip
 */

public String getToolTip(){
   return "Turns doublespaced files into singlespaced files";
   }
 
/**
 * does the action
 * @param selectedString 
 * @param selectedTunes
 * @returns the string to replace the selected string. Null if no replace 
 * @throws bad selection
 */
public String doTool(String selectedString,List selectedTunes) throws Exception{
   Debug.output(0,"DoubleSpaceTool: doTool called");
   return null;
   }
  




}
