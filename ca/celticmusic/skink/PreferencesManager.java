package ca.celticmusic.skink;

import java.util.Hashtable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.AbstractAction;


@SuppressWarnings("serial")
public class PreferencesManager {//extends JFrame{
	JFrame prefsWindow = null;
	Preferences myPrefs;
	int debugLevel = 4;
	String debugString = "PreferencesManager";
	String theTitle;
	
	Hashtable<String,String> prefsMap = null;
	static PreferencesManager theManager = null;
	static PreferencesManager getManager(String title,Class prefsNode){
		if (theManager==null){
			theManager = new PreferencesManager(title,prefsNode); 
		}
		return theManager;
	}
	
	private PreferencesManager(){};
	
	private PreferencesManager(String title,Class prefsNode){
		theTitle = title;
		myPrefs = Preferences.userNodeForPackage(prefsNode);
		prefsMap = new Hashtable<String, String>();
	}
	
	private JFrame makeWindow(){
		JFrame theFrame = new JFrame(theTitle);

		// insert the overall box

		Box prefsBox = new Box(BoxLayout.PAGE_AXIS);
		prefsBox.add(Box.createVerticalStrut(5));
		theFrame.getContentPane().add(prefsBox);
		String[] keys = {};
		try {
			keys = myPrefs.keys();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		for (String pref:keys){
			Debug.output(debugLevel, debugString+":"+pref+"="+myPrefs.get(pref,"??"));
	

//
			Box keyBox = new Box(BoxLayout.LINE_AXIS);
			JLabel keyLabel = new JLabel(pref);
			JTextField valueField = new JTextField(myPrefs.get(pref, "??"));
			JButton updateButton = new JButton("Update");
			AbstractAction buttonAction = new AbstractAction(){
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String pref = (String)getValue("pref");
					Debug.output(debugLevel,"button action"+"pref = "
							+getValue("pref") );
					
					JTextField theField = (JTextField)getValue("valueField");
					String value = theField.getText();
					Debug.output(debugLevel, "new value: "+value);
					setPreference(pref,value);
					
				}
			
			};
			buttonAction.putValue("pref", pref);
			buttonAction.putValue("valueField", valueField);
			
			updateButton.addActionListener(buttonAction);
			keyBox.add(Box.createHorizontalStrut(2));
			keyBox.add(keyLabel);
			keyBox.add(Box.createHorizontalStrut(2));
			keyBox.add(valueField);
			keyBox.add(Box.createHorizontalStrut(2));
			keyBox.add(updateButton);
			keyBox.add(Box.createHorizontalStrut(2));
			prefsBox.add(keyBox);
			prefsBox.add(Box.createVerticalStrut(5));
			prefsBox.add(Box.createVerticalGlue());
		}
		theFrame.pack();
		return theFrame;
		
	}

	public void addPreference(String pref,String defaultValue ,boolean isVisible){
		myPrefs.put(pref, defaultValue);
	}
	
	public String getPreference(String pref,String defaultValue, boolean isVisible){
		if (myPrefs.get(pref,"<no value>").equals("<no value>")){
			addPreference(pref,defaultValue, isVisible);
		}
		return myPrefs.get(pref, defaultValue);
	
	}
	public void setPreference(String pref, String value){
		myPrefs.put(pref, value);
	}
	
	public boolean editPreferences(){
		if (prefsWindow == null) prefsWindow = makeWindow();
		prefsWindow.setVisible(false);
		prefsWindow.setVisible(true);
		prefsWindow.toFront();
		return false;
	}
}
