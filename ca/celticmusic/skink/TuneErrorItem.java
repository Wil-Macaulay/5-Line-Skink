//
//  TuneErrorItem.java
//  5 Line Skink
//
//  Created by wil macaulay on Thu Jul 18 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
//import _generated.Skink;

package ca.celticmusic.skink;
import ca.celticmusic.skink.abcparser.*;

public class TuneErrorItem  implements TuneTreeItem{
    ParseException ex;
    public TuneErrorItem(ParseException e){
        ex = e;
        }
    
    public boolean isErrorDetected(){
        return true;
        }
    public String toString(){
        return("Found "+ex.currentToken.next.image+
         " at line "+getStartLine()
         +" column "+getStartColumn());
        }
    public int getStartLine(){
        return(ex.currentToken.next.beginLine);
        }
    public int getStartColumn(){
        return(ex.currentToken.next.beginColumn);
        }

    }      
    


