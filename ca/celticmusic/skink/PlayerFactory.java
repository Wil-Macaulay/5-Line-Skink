
//
//  PlayerFactory.java
//  5 Line Skink
//
//  Created by wil macaulay on Fri Mar 01 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
/**
 * Return the appropriate supported player for the platform, or null if no
 * player is supported.
 * @author Wil Macaulay
 * @version 1.0 1 mar 2002
 */
package ca.celticmusic.skink; 
public class PlayerFactory {

/**
 * currently active player
 */
 
private static Player currentPlayer;

/**
 * get a player of the appropriate flavour
 */
 
public static Player getPlayer() {
  if (currentPlayer == null) {
    currentPlayer = new JMFPlayer();
    }
  if (currentPlayer.canPlay()) {
    return currentPlayer;
    }
  return null;
  }  
      

}
