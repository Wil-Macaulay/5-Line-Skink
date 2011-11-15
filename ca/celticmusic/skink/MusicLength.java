/**
 *
 * MusicLength.java
 *
 *     0.1  7 Dec 1998 WM
 *
 **/
package ca.celticmusic.skink;
public class MusicLength
  {
  int numerator = 1;
  int denominator = 1;

  public void setNumerator(String theNum)
     {
     numerator = Integer.parseInt(theNum);
     }

  public void setDenominator(String theNum)
     {
     denominator = Integer.parseInt(theNum);
     }
     
  public int getAbs(int defaultLength)
     // return the 'absolute' length in multiples of 64th note
     // [in other words, eighth note is 8, quarter is 16, etc.]
     {
     return ((numerator * defaultLength)/denominator);   
     }
  
  public String asString()
     {
     String retString = "";
     if (numerator != 1)
	{
	retString += numerator;
	}
     if (denominator != 1)
	{
	retString += "/";
	retString += denominator;
	}
     return retString;
     }
  }
