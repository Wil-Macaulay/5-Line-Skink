//
//  SlurMarkup.java
//  5 Line Skink
//
//  Created by wil macaulay on Mon Aug 09 2004.
//  Copyright (c) 2004 wil macaulay. All rights reserved.
//
package ca.celticmusic.skink;
public class SlurMarkup {
   boolean tailsUp;
   double xStart,xEnd,yStart,yEnd;
	double yPeak;
   SlurMarkup(MusicElement m){
		xStart = xEnd = m.getReferenceX();
		tailsUp = m.isTailUp();
		yStart = m.getReferenceY();
		if (tailsUp) yStart += StaffPanel.spaceHeight;
		yPeak = yEnd =  yStart ;
		}
   SlurMarkup(double x, double y,boolean tailsUp){
		xStart= xEnd = x;
		yStart= yPeak = yEnd = y;
		this.tailsUp = tailsUp;
		}
   public void update(MusicElement m){
		xEnd=m.getReferenceX();
		yEnd=m.getReferenceY();
		if(tailsUp){
			yEnd = m.getYmax()+StaffPanel.spaceHeight;
			if(yEnd>yPeak){
				yPeak = yEnd;
				}
			}
		else{
			yEnd = m.getYmin();
			if(yEnd<yPeak){
				yPeak = yEnd();
				}
			}
		}
	public double yPeak(){
	   return yPeak;
		}	
	// go around an enclosed slur	
	public void update(SlurMarkup enclosed){
	   xEnd = enclosed.xEnd();
		yEnd = enclosed.yEnd();
	   if(tailsUp && enclosed.isTailUp()){
			if(yPeak<enclosed.ycp1()){
				yPeak = enclosed.ycp1()+2*StaffPanel.spaceHeight;
				}
			}
		else{
			if((!tailsUp) && (!enclosed.isTailUp())){
				if(yPeak>enclosed.ycp1()){
					yPeak = enclosed.ycp1()-2*StaffPanel.spaceHeight;
					}
				}
			}
	   }		
		
				
   public double xStart(){
      return xStart;
	  }
   public double yStart(){
      return yStart;
	  }
   public double xEnd(){
      return xEnd;
	  }
   public double yEnd(){
      return yEnd;
	  }
   public double xcp1(){
      return xStart+(xEnd - xStart)*0.3;
	  }
   public double ycp1(){
	   if (tailsUp){
         return yPeak + 2*StaffPanel.spaceHeight;
			}
		else{
			return yPeak - StaffPanel.spaceHeight;
			}
	  }
   public double xcp2(){
      return xStart + (xEnd - xStart)*0.7;
	  }
   public double ycp2(){
      return ycp1()+1.0;
	  }
	  
	public boolean isTailUp(){
		return tailsUp;
		}
}
