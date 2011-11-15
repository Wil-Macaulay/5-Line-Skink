package ca.celticmusic.skink;

public class DecorationElement extends MarkupElement
  {
  // list of default decorations
  static java.util.List knownDecos;
  static boolean isInitialized = false;
  static String[][] defaultDecos = {
      {"~","turn"},
		{".","staccato"},
		{"u","upbow"},
		{"v","downbow"},
		{"H","fermata"},
		{"I","invertedfermata"}, // =barfly
		{"J","slide"}, //=abcm2ps
		{"L","accent"},
		{"M","mordent"},
		{"O","coda"},
		{"P","pralltriller"},
		{"Q","longphrase"}, //=barfly
		{"R","mediumphrase"}, //=barfly
		{"S","shortphrase"},  //=barfly, {"S","segno"},   proposed 2.0
		{"T","trill"},
		{"W","segno"}       // barfly
		};
  
  static String[][] decoAliases = {
      {"lowermordent", "pralltriller"}, // =barfly, not 2.0
		{"uppermordent", "mordent"},   // =barfly, not 2.0
		{">","accent"},
		{"emphasis","accent"},
		{"<(","crescendo("},
		{"<)","crescendo)"},
		{">(","dimuendo("},
		{">)","dimuendo)"}
		};
		
  
  static void initDecos(){
       isInitialized = true;
		 }
/**
 * get the canonical deco - if there is none, the original string
 * is trimmed and lowercased
 */
    
  static String getCanonicalDeco(String decoString){
       if (!isInitialized){
		    initDecos();
			 }
		 // trim and lowercase
		 String foundDeco = decoString.trim().toLowerCase();
		 // now look for alias
		 for (int i = 0; i<decoAliases.length; i++){
		   if(decoAliases[i][0].equals(foundDeco)){
			   foundDeco = decoAliases[i][1];  
				break;
				} 
			}
		 return foundDeco;
		 }	 
 /**
  * get the deco from a gracing - if there is no definition, 
  * return null and it will be ignored
  */		 
  static String getDecoFromGracing(String gracing){
       String foundDeco = null;
		 // first we should check the user-defined FIXME NYI
		 for (int i = 0; i<defaultDecos.length; i++){
		   if(defaultDecos[i][0].equals(gracing)){
			   foundDeco = defaultDecos[i][1];  
				break;
				} 
			}	
		 return foundDeco;
		 }
		 		
  public DecorationElement(){
       setSymbol("!!");
       fixedAdvance = true;
       }
  // includes delimiters     
  public void setDeco(String theString){
       setSymbol(theString);
       }
  // no delimiters 
      
  public String getContents()  { 
       return symbol.substring(1,symbol.length()-1);
       }   
 
   public String elementType(){
      return "deco";
      }
		
/*  public void paint(StaffPanel p, boolean advance){
    p.paintDecoration(getContents());
    }  */
    
  }
