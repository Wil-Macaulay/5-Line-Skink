/**
  * TuneBook class
  *
  *   0.1  9 Nov 1998 WM
  *
  *   0.2 27 May 1999 WM
  *   1.0 4 Apr 2002 WM    convert Vectors to Lists
  *
  **/
package ca.celticmusic.skink;
import java.util.*;

public class TuneBook 
   {
   List<Tune> theTunes = new ArrayList<Tune>();
   Tune currentTune;
   String fileName = "untitled.abc";
   int debugLevel = 3;
   String debugString = "TuneBook";

   public int getTuneAtLine(int currentLine) {
	   // start at the back and work forward
	    int tuneBookSize = theTunes.size();
	    int foundIndex = -1;
	    Debug.output(debugLevel, debugString + ".getTuneAtLine: "+"currentLine="+currentLine);
	    for (int currentTune = tuneBookSize-1; (currentTune>= 0)&&(foundIndex == -1); currentTune--){
	    	Debug.output(debugLevel, debugString + ".getTuneAtLine: "+"currentTune"+currentTune);
	    	if(theTunes.get(currentTune).isLineIn(currentLine)){
	    		Debug.output(debugLevel, debugString + ".getTuneAtLine: "+"currentLine="+currentLine);
	    		foundIndex = currentTune;
	    	}
	    }
		return foundIndex;
	}

	public void addTune(Tune theTune) {
		currentTune = theTune;
	}

	public void endTune() {
		theTunes.add(currentTune);
		currentTune = null;
      }

   public void listTunes() {
		for (Tune theTune : theTunes) {
			System.out.println(theTune.titleString());
		}
	}

   public List getTuneTitles()
      {

      List<String> rList=new ArrayList<String>();
      for (Tune theTune : theTunes){
         rList.add(theTune.titleString());
         }
      return rList;
      }

   public List getTunes(){
      return theTunes;
      }

   public Tune getTuneAt(int index) {
		if (index >= 0) {
			return (Tune) theTunes.get(index);
		} else
			return null;
	}

	public int indexOf(Tune t) {
		return theTunes.indexOf(t);
	}

	public String toString() {
		return fileName;
	}

	public void setName(String name) {
		fileName = name;
	}

	public void dumpTunes() {
		for (ListIterator i = theTunes.listIterator(); i.hasNext();) {
			((Tune) i.next()).dump();
		}
	}
}


