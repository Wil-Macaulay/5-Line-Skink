/* 
	Skink.java

	Title:			Skink 
	Author:			Wil Macaulay wil@syndesis.com
	Description:	Java abc notation display
	
	Nov 1998 first version
*/

package ca.celticmusic.skink;
import javax.swing.*;

public class Skink {
  String fileName = "<no file>";
  MainFrame frame;
  public Skink(String [] args) {
        Debug.init();
        if (args.length == 1) fileName = args[0]; 
        //System.out.println("Skink starting on "+fileName);
        try {
                // For native Look and Feel, uncomment the following code.
                /* */
                String laf = System.getProperty("swing.defaultlaf","native");
                //System.out.println("default LAF"+laf);
                    
                if("native".equals(laf) )try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } 
                    catch (Exception e) { 
                    } /*
                    */
               
                 else
                    try {
                        UIManager.setLookAndFeel(laf);
                        }
                    catch (Exception e) {
					    System.out.println("Unable to set look and feel "+laf);
						System.exit(1);
					    }    

                frame = MainFrame.newMainFrame();
                frame.initComponents();
                
               if (args.length == 1){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                           try{
                             frame.doOpen(fileName);
                             }
                           catch (Exception e){ 
                             
                             }
                          }   
                        });
                    } 
        		frame.initWindowMenu();
                frame.setVisible(true);
                
        }
        catch (Exception e) {
                e.printStackTrace();
        }
}

	// Main entry point
	static public void main(String[] args) 
	{
		new Skink(args);
	}
	
}
