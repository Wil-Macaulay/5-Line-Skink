/////////////
//
//  ErrorDialog.java
//
//    display error message
//
//////////////

package ca.celticmusic.skink;

import javax.swing.*;

class ErrorDialog
   {
   static void show(JFrame f, Exception e)
      {
      Debug.output(2,"attempting to show error dialog for exception"+e);
      JOptionPane.showMessageDialog(f, e.getMessage());
      }
   static void show(JFrame f, Error e)
      {
      Debug.output(2,"attempting to show error dialog for exception"+e);
      JOptionPane.showMessageDialog(f, e.getMessage());
      }
      
      
   }   
   
   
   