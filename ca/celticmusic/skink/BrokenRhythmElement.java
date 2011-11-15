// BrokenRhythmElement.java

/**
 * Broken rhythm (a>b or a<b)
 *
 * note that the first note in the pair is no longer attached to the second note, both are
 * played and displayed independently
 *  History:
 *         10 May 2004 1.0f2 no longer a MultiElement, just a separator (markupElement)
 *
 ************/
 
package ca.celticmusic.skink;

public class BrokenRhythmElement extends MarkupElement { 
    int dotCount = 0; // (dotCount > 0 for >>> )
	String separator = "";
	
    public BrokenRhythmElement() {
        }
		
	   
    public String elementType() {
        return "brhy";
        }
        
    public String asString() {
		return separator;
        }

  public void setDotCount(int dc)  {  
        // this should only be done when there are already 2 elements (note the first element is in Line)  
        dotCount = dc;
		}

          
  public void paint(StaffPainter p, boolean advance){

    } 
	 
  public void updateLineContext(Line theLine){
    MusicElement first = (MusicElement) theLine.getLastAudibleElement();
	if (first != null){ // what do we signal if nul?
        if(dotCount>0)  {
		   int dc = dotCount;
           for (int i=0; i<dc;i++) {
              separator += ">";
              first.dotIt();
              }
           }
        else{
           int dc = -dotCount;      
           for (int i=0; i<dc; i++) {
              separator +="<";
              first.dashIt();

              }
           } 
 	   theLine.setBrokenRhythmElement(this); // could just add to an in-progress list
	   first.setAbsLength(theLine.getDefaultNoteDuration(),theLine.getGraceNoteLength());
       Debug.output(1,"BR set: dotcount "+dotCount+" separator is:"+separator); 
	   }
	}

public void doBroken(Line theLine){
	MusicElement last = (MusicElement) theLine.getLastAudibleElement();
	int dc = dotCount;
	Debug.output(1,"doBroken: dotcount "+dotCount);
	if (last != null){
        if(dc>0) {
           for (int i=0; i<dc;i++) {
              last.dashIt();
              }
           }
        else {
           dc = -dc;      
           for (int i=0; i<dc; i++){
              last.dotIt();
              }
           } 
           
        }
	theLine.setBrokenRhythmElement(null);	
	}

	
}				    
	    