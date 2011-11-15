/***********************
 * 
 *   In-tune field
 *      0.1 June 19 2000 WM
 *
 *
 *************/
package ca.celticmusic.skink;


public class FieldElement extends MarkupElement
   {
    public InfoField theField = null;
    
    public FieldElement(InfoField f)
        {
        theField = f;
        }
        
    public String elementType()
        {
        return "fild";
        }
        
    public String asString()
        {
        return theField.asString();
        } 
    public InfoField getField()
        {
        return theField;
        }
            
    // this 'element' does not need to be written on a staff   
    public boolean needsStaff()  
        {
        return false;
        } 
     
    public void paint(StaffPainter p, boolean advance){
      // don't try to paint an empty field
      if (theField != null){
         p.endBeam();
         p.paintField(this);
         }
      }             
    public int glyphWidth()
    // FIXME
       {
       return 0;
       }
   }
 