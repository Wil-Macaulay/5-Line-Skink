package ca.celticmusic.skink;
import java.util.*;

public class Meter
   {
   static List knownMeters;
   static boolean isInitialized = false;
   
   int notesPerBar;
   int noteLength;
   int defaultLen = SkinkConstants.DEFAULT_NOTE_LENGTH;  // default eighth note
   
   String symbol;
   boolean specialCharacter;
   
   
   static void initMeters()
       {
       if (isInitialized) return;
       
       knownMeters = new ArrayList();
       
       knownMeters.add(new Meter(2,2));
       knownMeters.add(new Meter(3,2));
       knownMeters.add(new Meter(2,4));
       knownMeters.add(new Meter(3,4));
       knownMeters.add(new Meter(4,4));
       knownMeters.add(new Meter(5,4));
       knownMeters.add(new Meter(6,4));
       knownMeters.add(new Meter(7,4));
       knownMeters.add(new Meter(6,8));
       knownMeters.add(new Meter(7,8));
       knownMeters.add(new Meter(9,8));
       knownMeters.add(new Meter(11,8));
       knownMeters.add(new Meter(12,8));
       knownMeters.add(new Meter(13,8));
       knownMeters.add(new Meter(15,8));
	   knownMeters.add(new Meter(15,16));
       knownMeters.add(new Meter(18,16));
     
       knownMeters.add(new Meter(2,2,"C"));
       knownMeters.add(new Meter(4,4,"C|"));
       knownMeters.add(new Meter(0,0,"free"));
       knownMeters.add(new Meter(0,0,"none"));
       isInitialized = true;
       }
       
   
   static public Meter findMeter(String theMeter)
      {
      //FIXME should use HashMap
      // initialize key sig list
      if (!isInitialized) initMeters();
      String meterString = theMeter.trim().toLowerCase();
      for (ListIterator i = knownMeters.listIterator();i.hasNext();){   
         Meter met = (Meter) i.next();
         if (met.symbol.toLowerCase().equals(meterString) ) 
            {
            Debug.output(2,"Found meter "+theMeter);
            return met;
            }
         }
         
       
      return(new Meter(0,0,"unknown"));   
      }
   
      
   public Meter(int num, int denom)
       {
       notesPerBar = num;
       noteLength = denom;
       symbol = num + "/" + denom;
       specialCharacter = false;
       }
       
   public Meter(int num, int denom, String sym)
       {
       notesPerBar = num;
       noteLength = denom;
       symbol = sym;
       specialCharacter = true;
       }
  
   public int defaultLength()
       {
       float frac = ((float) notesPerBar)/ ((float) noteLength);
       if (frac < 0.75) 
           return SkinkConstants.SIXTEENTH_LEN;
       else 
           return SkinkConstants.EIGHTH_LEN;
       }
       
   public boolean isSpecial(){
       return specialCharacter;
       }
       
   public int getNumerator() { 
       return notesPerBar;
       }
       
   public int getDenominator() {
       return noteLength;
       }
       
   public String getSymbol()   {
       return symbol;
       } 
       
/**
 * compound meters are multiples of 3/8, where the beat unit is 3/8 - e.g. 6/8, 9/8,12/8 etc
 */       
   public boolean isCompound(){
      return ((noteLength==8) && (notesPerBar == (notesPerBar/3) * 3));
      }
   
           
            

}  
