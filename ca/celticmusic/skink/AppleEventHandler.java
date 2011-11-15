//
//  AppleEventHandler.java
//  5 Line Skink
//
//  Created by wil on Fri Jun 15 2001.
//  Copyright (c) 2001  wil macaulay. All rights reserved.
// 23 jul 2005 use MRJAdapter
// 18 mar 2011 use apple adapter if available
//
package ca.celticmusic.skink;
//import com.apple.mrj.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import net.roydesign.mac.MRJAdapter;


public class AppleEventHandler //implements MRJQuitHandler, MRJAboutHandler, MRJOpenDocumentHandler
{
   MainFrame mF = null;
   public AppleEventHandler(MainFrame theMF){
       mF = theMF;
       try {
    	   Class appEvent = Class.forName("com.apple.eawt.AppEvent");
    	   useAppleAdapters();
       } catch (ClassNotFoundException e){
    	   useMRJ();
       }
   }
   
   private void useMRJ(){
		 /*
       MRJApplicationUtils.registerQuitHandler(this);
       MRJApplicationUtils.registerAboutHandler(this);
       MRJApplicationUtils.registerOpenDocumentHandler(this);
		 */
		 Debug.output(1,"Adding action listeners");
		 MRJAdapter.addAboutListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mF.doAbout();
				}
			});
		 MRJAdapter.addQuitApplicationListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mF.doExit();
				}
			});
		 MRJAdapter.addOpenDocumentListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			   File f = ((net.roydesign.event.ApplicationEvent)e).getFile();
				try{
					mF.doOpen(f);
					}
				catch (Exception err)
					{
					System.err.println("error opening file" + err);
					System.exit(1);
					}

				}
			});
			
		 MRJAdapter.addPreferencesListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mF.doPreferences();
			}
		 });
		 // 
		 
       }
   
  
  


   private void useAppleAdapters(){
	   com.apple.eawt.Application theApp = com.apple.eawt.Application.getApplication();
	   
	   theApp.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
		   public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent e){
			   mF.doPreferences();
		   }
	   });
	   
	   theApp.setOpenFileHandler(new com.apple.eawt.OpenFilesHandler() {
		   public void openFiles(com.apple.eawt.AppEvent.OpenFilesEvent e){
			   System.out.println("opening files");
			   java.util.List<File> files = e.getFiles();
			   File f = files.get(0);
				try{
					mF.doOpen(f);
					}
				catch (Exception err)
					{
					System.err.println("error opening file" + err);
					System.exit(1);
					}

				}
	   });
	   
	   theApp.setAboutHandler(new com.apple.eawt.AboutHandler(){
		   public void handleAbout(com.apple.eawt.AppEvent.AboutEvent e){
			   mF.doAbout();
		   }
	   });
	   
	   theApp.setQuitHandler(new com.apple.eawt.QuitHandler(){
		   public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent e,com.apple.eawt.QuitResponse response){
			   mF.doExit();
		   }
	   });

   }

}


