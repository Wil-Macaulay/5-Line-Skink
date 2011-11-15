//
//  RendererFactory.java
//  5 Line Skink
//
//  Created by wil macaulay on Sun Feb 17 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
/**
 *
 * Return the appropriate renderer for the environment (AWT or G2D)
 *
 * @author Wil Macaulay
 * @version 1.0 17 feb 2002
 *
 */
package ca.celticmusic.skink; 
public class RendererFactory {
  static Renderer theRenderer = null;

  static Renderer getRenderer() {
    //return new AWTRenderer();
    if (theRenderer == null){
       theRenderer = new G2DRenderer();
       }
    return theRenderer; 
  }
}
