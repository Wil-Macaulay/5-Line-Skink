//
//  Unroller.java
//  5 Line Skink
//
//  Created by wil macaulay on Fri Mar 15 2002.
//  Copyright (c) 2001 Wil Macaulay. All rights reserved.
//
/**
 * unroll abc so it can be played in sequence - expand repeats, parts, multiple endings
 * @author Wil Macaulay
 * @version 1.0a2 Mar 15 2002
 *    History
 *         1.0f2 23 May 2004 WM forget() 
 */
package ca.celticmusic.skink;
import java.util.*;

public class Unroller {
  int barSize;   // size of bar in 64th notes according to meter
  int curBar;
  int anacSize;  // size of anacrusis
  int sectionStart;
  int barline;  // barline to go back to
  int forwardRep; // forward repeat
  int partEnd; // backward repeat
  List unrolled;
  Map partMap;

  
  
  public Unroller(){
    unrolled = new ArrayList();
    sectionStart = 0;
    forwardRep = 0;
    barline = -1;
    partMap = new HashMap();
    partMap.put("beginRepeat",new Integer(0));
    }

/**
 * stick an element in and advance 
 */
  public void unroll(List lines){
    for(ListIterator i = lines.listIterator(); i.hasNext();){
      unrollAbc(((Line)i.next()).getElements());
      }
    }
      
  public void unrollAbc(List abc){
    for(ListIterator i = abc.listIterator(); i.hasNext();){
       AbcElement e = (AbcElement)i.next();
       e.doUnroll(this);
       }
    }   
  public void setBarSize(int size){
    barSize = size;
    }
     
  public void advance(AbcElement a){
    curBar += a.getAbsLength();
    unrolled.add(a);
    }
  
  public void resetBar(){
    curBar = 0;
    }
// remember where you are by ID    
  public void remember(String id){
    Debug.output(1,"Unroller.remember: "+id+" at "+unrolled.size());
    partMap.put(id,new Integer(unrolled.size()));
    }
 // forget previous ID
  public void forget(String id){
    Debug.output(1,"Unroller.forget: "+id);
	partMap.remove(id);
	}
    
  public void copySub(String from,String to){
    int start = 0;
    int end = unrolled.size();   // copy to the end if unspecified
    Integer t = (Integer)partMap.get(from);
	Debug.output(1,"Unroller.copySub: from "+from+" to "+to);
    if (t!=null){
      start = t.intValue();
      }
    t = (Integer)partMap.get(to);
    if (t!=null){
      end = t.intValue();
      }  
	Debug.output(1,"Unroller.copySub: start "+start+" end "+end+" size "+unrolled.size());
    ArrayList temp=new ArrayList(unrolled.subList(start,end));  
    unrolled.addAll(temp);                                
    }
    
  public List getUnrolled(){
    return unrolled;
    }  
     
    
  

}
