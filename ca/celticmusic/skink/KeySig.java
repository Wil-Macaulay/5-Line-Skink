/*
 * Key signature - modes
 
 Ionian (major)
 Dorian
 Phrygian
 Lydian
 Mixolydian
 aeolian (minor)
 locrian
 
 History
    23 Jan 2003  explicit tonic/mode
	28 Jan 2004  support for bass clef
	7 jul 2005 HP support (recapture lost code)
 */

package ca.celticmusic.skink;
import java.util.*;

public class KeySig
   {
   static List<KeySig> knownSigs;
   static boolean isInitialized = false;
   //                    a  b  c  d  e  f  g
   static int[] sfc  = { 0, 0, 0, 0, 0, 0, 0};
   
   static int[] sfg  = { 0, 0, 0, 0, 0, 1, 0};
   static int[] sfd  = { 0, 0, 1, 0, 0, 1, 0};
   static int[] sfhp = { 0, 0, 1, 0, 0, 1, 9};  // Hp - highland pipes has explicit natural
   static int[] sfa  = { 0, 0, 1, 0, 0, 1, 1};
   static int[] sfe  = { 0, 0, 1, 1, 0, 1, 1};
   static int[] sfb  = { 1, 0, 1, 1, 0, 1, 1};
   static int[] sffs = { 1, 0, 1, 1, 1, 1, 1};
   static int[] sfcs = { 1, 1, 1, 1, 1, 1, 1};
   
   static int[] sff  = { 0,-1, 0, 0, 0, 0, 0};
   static int[] sfbf = { 0,-1, 0, 0,-1, 0, 0};
   static int[] sfef = {-1,-1, 0, 0,-1, 0, 0};
   static int[] sfaf = {-1,-1, 0,-1,-1, 0, 0};
   static int[] sfdf = {-1,-1, 0,-1,-1, 0,-1};
   static int[] sfgf = {-1,-1,-1,-1,-1, 0,-1};
   static int[] sfcf = {-1,-1,-1,-1,-1,-1,-1};
   
   static String notes = "abcdefg";
   static void initKeySigs()
      {
      // already initialized?
      if (isInitialized) return;
      knownSigs = new ArrayList<KeySig>();
      // C and related keys, no sharps or flats
      KeySig k;

      k = new KeySig("C",sfc);   

      addRelative(k,"C","D","E","F","G","A","B");
      
      //sharp keys
      
      // G and related keys - one sharp (f)
      k = new KeySig("G",sfg);
      addRelative(k,"G","A","B","C","D","E","F#");
      
      // D and related keys - two sharps (f,c)
      k = new KeySig("D",sfd);
      addRelative(k,"D","E","F#","G","A","B","C#");
      Debug.output(1, "adding HP");
      
      // Highland pipe - explicit f,c sharp, g natural
      k = new KeySig("Hp",sfd, sfhp,true,false);
      k.graceNoteLength = SkinkConstants.THIRTYSECOND_LEN;
      knownSigs.add(k);        
      
      // Highland pipe - no sharps or naturals
      k = new KeySig("HP",sfd, sfc, true,false);
      k.graceNoteLength = SkinkConstants.THIRTYSECOND_LEN;
      knownSigs.add(k);        
      
      // A and related keys - three sharps (f,c,g)
      k = new KeySig("A",sfa);
      addRelative(k,"A","B","C#","D","E","F#","G#");
     
      // E and related keys - four sharps (f,c,g,d)
      k = new KeySig("E",sfe);
      addRelative(k,"E","F#","G#","A","B","C#","D#");
      
      // B and related keys - five sharps (f,c,g,d,a)
      k = new KeySig("B",sfb);
      addRelative(k,"B","C#","D#","E","F#","G#","A#");

      // F# and related keys - six sharps (f,c,g,d,a,e)
      k = new KeySig("F#",sffs);
      addRelative(k,"F#","G#","A#","B","C#","D#","E#");
      
      // C# and related keys - seven sharps (f,c,g,d,a,e,b)
      k = new KeySig("C#",sfcs);
      addRelative(k,"C#","D#","E#","F#","G#","A#","B#");
   
      
      // flat keys
      
      // F and related (flats b)
      k = new KeySig("F",sff);
      addRelative(k,"F","G","A","Bb","C","D","E");
      
      // Bb and related (flats b, e )
      k = new KeySig("Bb",sfbf);
      addRelative(k,"Bb","C","D","Eb","F","G","A");
      
      // Eb and related (flats b, e, a)
      k = new KeySig("Eb",sfef);
      addRelative(k,"Eb","F","G","Ab","Bb","C","D");
      
      // Ab and related (flats b, e, a, d)
      k = new KeySig("Ab",sfaf);
      addRelative(k,"Ab","Bb","C","Db","Eb","F","G");
      
      // Db and related (flats b, e, a, d, g)
      k = new KeySig("Db",sfdf);
      addRelative(k,"Db","Eb","F","Gb","Ab","Bb","C");
      
      // Gb and related (flats b, e, a, d, g, c)
      k = new KeySig("Gb",sfgf);
      addRelative(k,"Gb","Ab","Bb","Cb","Db","Eb","F");
     
      // Cb and related (flats b, e, a, d, g, c, f)
      k = new KeySig("Cb",sfcf);
      addRelative(k,"Cb","Db","Eb","Fb","Gb","Ab","Bb");
      isInitialized = true;
      
      }
    
   static void addRelative (KeySig k, String doh, String re, String mi, String fah,
                                      String soh, String la, String ti){
      knownSigs.add(k);
      knownSigs.add(new KeySig(doh,"Major",k));
      knownSigs.add(new KeySig(doh,"Ionian",k));
      knownSigs.add(new KeySig(re,"Dorian",k));
      knownSigs.add(new KeySig(mi,"Phrygian",k));
      knownSigs.add(new KeySig(fah,"Lydian",k));
      knownSigs.add(new KeySig(soh,"Mixolydian",k));
      knownSigs.add(new KeySig(la,"Minor",k));
      knownSigs.add(new KeySig(la,"M",k));
      knownSigs.add(new KeySig(la,"Aeolian",k));
      knownSigs.add(new KeySig(ti,"Locrian",k));
      }                               
                                      
   static KeySig findKey(String keyString) {
      // initialize key sig list
      // Debug.output(0,"finding key "+keyString);
      if (!isInitialized) initKeySigs();
      // take out spaces, trim to 'canonical form' 
      StringBuffer tempBuf = new StringBuffer(keyString.trim().toLowerCase());
      // start at the end to avoid changing index of chars we haven't processed
      for (int i = tempBuf.length()-1; i >= 0 ;i--){
         if(tempBuf.charAt(i) == ' '){
            tempBuf.deleteCharAt(i);
            }
         }   
      
      String testSig = tempBuf.toString();
      for (ListIterator i = knownSigs.listIterator();i.hasNext();){   
         KeySig sig = (KeySig) i.next();
			// explicit test for HP FIXME - kludgey
         if (sig.isSameKey(testSig)|| sig.isSameKey(keyString) )  {
           // Debug.output(0,"Found key "+testSig);
            return sig;
            }
         }
         
      // unknown key sig - no sharps/flats for now   
      int[] sfc = {0,0,0,0,0,0,0};
       
      return(new KeySig("unknown", sfc));   
      }
   
   // instance variables   
      
   int[] sharpFlat; // -1 is flat 0 is nat +1 is sharp
                    // {a,b,c,d,e,f,g}
	int[] sharpFlatShown; // HP doesn't show accidentals 
   String sig = null;
   boolean setTailDirection = false;
   boolean tailsUp = false;
   int numSF = 0; 
   String mode;
   String tonic;
   
   //FIXME: factor out clef into clef element?
   String clefname; // treble, alto, tenor, bass
   char middle; // pitch value for middle line
	int transpose; // semitones to transpose
	int octava; // +8 or -8
	int stafflines; // number of lines for the staff
   
   int matchLen = 0; // 0 means match the whole string
   int graceNoteLength = SkinkConstants.SIXTEENTH_LEN;
// usual case, display and play the same
   public KeySig(String sigString,int[] shFl) {
      sig = sigString.toLowerCase(); 
      sharpFlat = shFl;
	   sharpFlatShown = shFl;
      
      for (int i =0; i<7;i++){
        if (sharpFlat[i]!=0){
          numSF++;
          }
        }  
      }
      
   public KeySig(String tonic, String mode, KeySig relativeMajor){
      this(tonic + mode, relativeMajor.sharpFlat);  
      this.mode = mode;
      this.tonic = tonic;
      // only match the first three chars for long modes
      if(mode.length() > 2){
         matchLen = tonic.length()+3;
         sig = sig.substring(0,matchLen);
         }
         
     }
          
   public KeySig(String sigString, int[] shFl, int[] shFlShown, boolean setTail, boolean up){
      this(sigString,shFl);
		sig = sigString;
		sharpFlatShown = shFlShown;
      setTailDirection = setTail;
      tailsUp = up;
      }
   
           
   
   public int getNumSF(){
     return numSF;
     }
     
   public String toString() {
      return sig;
      }
    
   // does this key match the key you're looking for? 
   //       - trim spaces and (FIXME) ignore trailing stuff after key
   //       - eventually, handle global accidentals
   
   public boolean isSameKey(String keyName)  {
      //Debug.output(0,"matching " +keyName + " with "+tonic + ":" +mode);
      // check for exact match
      if (matchLen == 0){
         return(sig.equals(keyName));
         }
      // check for partial match for long names
      return(keyName.startsWith(sig));
      }
      
   public boolean setTailDirection()  {
      return setTailDirection;
      }  
       
   public int getGraceNoteLength(){
      return graceNoteLength;
      }
        
   public boolean tailsUp() {
      return tailsUp;
      }   
 
	// is this note sharp or flat in this key?    
	public int sharpOrFlatPlayed(String note) {
		int sf = sharpFlat[notes.indexOf(note.toLowerCase())];
		return sf;
		}
    
	public int sharpOrFlatShown(String note) {
		int sf = sharpFlatShown[notes.indexOf(note.toLowerCase())];
		return sf;
		}
      
   }            

           
 