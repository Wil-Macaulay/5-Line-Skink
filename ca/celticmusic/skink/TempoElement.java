//
//  TempoElement.java
//  5 Line Skink
//
//  Created by wil macaulay on Fri Aug 02 2002.
//  Copyright (c) 2001 Wil Macaulay. All rights reserved.
//


package ca.celticmusic.skink;
public class TempoElement extends AbstractMusicElement{
   int beatNumerator;
   int beatDenominator;
   int beatsPerMinute;
   
   public TempoElement(){
      
      };
   
   public String elementType(){
      return "tmpo";
      };
            
   public void updatePlayerContext(Player player){
      };
      
   public boolean needsStaff(){
      return false;
      };
   public void setBeatsPerMinute(int bpm){
      beatsPerMinute = bpm;
      }
      
   public void setBeat(int num, int denom){
      beatNumerator = num;
      beatDenominator = denom;
      
      }
      
   public int beatNumerator(){
      return beatNumerator;
      }
      
   public int beatDenominator(){
      return beatDenominator;
      }      
         
   public int beatsPerMinute(){
      return beatsPerMinute;
      }
            
   public int glyphWidth(){
      return 0;
      }         
	protected BarPosition updateBarPosition(BarPosition bpCurrent) {
		return new BarPosition(bpCurrent);

	} 
	protected void setBarPosition(BarPosition bpCurrent){
		BarPosition bpNew = new BarPosition(bpCurrent); 
		bpThis = bpNew;
	}

}
