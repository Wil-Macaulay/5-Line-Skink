//
//  AlignedWords.java
//  5 Line Skink
//
//  Created by wil macaulay on Wed May 29 2002.
//  Copyright (c) 2002 Wil Macaulay. All rights reserved.
//     History
//           31 Jan 2004 -extra dash if --
//
/**
 * maintains a set of lyrics that will be aligned with the words
 */
 
package ca.celticmusic.skink;
import java.util.*;
public class AlignedWords {
   private List<String> contents = new ArrayList<String>();
   private boolean appendNext = false;
   private boolean waitForBar = false;
   private boolean sawDash = false; 
   private ListIterator i;
   private double lastX;
   
   public AlignedWords(){};
   
   public void paint(StaffPainter p, double curX,double curY){
	   Debug.output(1,"AlignedWords.paint()");
	   if (!waitForBar) {
		   if(i.hasNext()){
			   String word = (String)i.next();
			   // this should probably be done with inner classes
			   if (sawDash && "-".equals(word)){
				   p.paintDash(curX,curY);
				   sawDash = false;
				   lastX = curX;
				   return;
			   }
			   sawDash = false;
			   if ("*".equals(word)){
				   // skip
				   Debug.output(1,"Skipping");
			   }
			   else if ("-".equals(word)){
				   // need a dash halfway between old x and new x (first dash)
				   p.paintDash((curX+lastX)/2,curY);
				   sawDash = true;

				   // now we paint the next word
				   paint(p,curX,curY);
			   }
			   else if ("_".equals(word)){
				   // need a line from old x to new x
				   p.paintLine(lastX,curX,curY);
			   }
			   else if ("|".equals(word)){
				   waitForBar = true;
			   }
			   else {  // just a word
				   p.paintLyric(word,curX,curY);
			   }
			   lastX = curX;  
		   }
	   }
   }   
   /**
 * appends the contents of an existing AW to this
 */
    
public void addAll(AlignedWords rhs){
   contents.addAll(rhs.contents);
   }
      
public void newBar(){
   waitForBar = false;
	// if next on list is a wait for bar, go past it
	if (i.hasNext()){
		String word = (String)contents.get(i.nextIndex());
		if ("|".equals(word)){
			word = (String)i.next();  // iterate past
			}
		}	
   }  
	        
public void reset(double startX){
   waitForBar = false;
   i = contents.listIterator();
   lastX = startX;
   }
   
      
public void nextBar(){
   Debug.output(1,"AW- nextBar");
   appendNext = false;
   contents.add("|");
   }
   
public void litDash(){
   Debug.output(1,"AW- litDash");
   if (!contents.isEmpty()){
      appendLast("-");
      }
   }
private void appendLast(String sep){
   String temp = (String) contents.get(contents.size()-1) + sep;   
   contents.set(contents.size()-1,temp);
   appendNext = true;
   }
   
public void sylBreak(){
   Debug.output(1,"AW- sylBreak");
   appendNext = false;
   contents.add("-");
   }
   
public void holdSyl(){
   Debug.output(1,"AW- holdsSyl");
   appendNext = false;
   contents.add("_");
   }
   
public void skip(){
   Debug.output(1,"AW- skip");
   appendNext = false;
   contents.add("*");
   }

public void join(){
   Debug.output(1,"AW- join");
   if (!contents.isEmpty()){
      appendLast(" ");
      appendNext = true;
      }
   }
   
public void breakWord(){
   appendNext = false;
   }
   
public void escapedChar(String escaped){
   if(appendNext){ 
      appendLast("\\");
      }
    else{
       contents.add(new String("\\") );
       }
    appendNext = true;
    }
             
public void word(String word){
   Debug.output(1, "AW- word "+ word);
   if (appendNext) {
      appendLast(word);
      }
   else{
      contents.add(new String(word));
      }   
   appendNext = true;
   }
   
public void dump(){
   Debug.output(1, "AW- dump");
   for (ListIterator i = contents.listIterator() ; i.hasNext();){
      Debug.output(1,(String)i.next());
      }
   }         
                     
                                     


}
