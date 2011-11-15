package ca.celticmusic.skink;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class WindowMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int debugLevel = 4;
	
public WindowMenu(){
	Debug.output(debugLevel, "Window Menu constructor");
	setHorizontalTextPosition(4);
	setText("Window");
	setRequestFocusEnabled(false);
	setHorizontalAlignment(2);
	JPopupMenu popup = getPopupMenu();
	Debug.output(debugLevel,"popup menu"+popup);
	// need to populate the menu for the first time to ensure menulistener 
	// gets fired properly on Mac OS
	populateMenu();
//* 
	addMenuListener(new MenuListener(){
			public void menuSelected(MenuEvent e){
				Debug.output(debugLevel, "Window menu selected");
				populateMenu();
			}
			public void menuDeselected(MenuEvent e){
				Debug.output(debugLevel, "Window menu deselected");	
				removeAll();
			}
			public void menuCanceled(MenuEvent e){
				Debug.output(debugLevel, "Window menu canceled");	
			}
	});
//*/	
	setVisible(true);
}
public void populateMenu(){
	Debug.output(debugLevel, "populating window menu");
	removeAll();
	for (final MainFrame theFrame : MainFrame.getMainFrames()){
		JMenuItem item = new JMenuItem();
		item.setText(theFrame.getTitle());
		Debug.output(debugLevel, "frame title"+theFrame.getTitle());
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e){
				theFrame.popToFront();
			}
			
		});
		add(item);
		
		
	}

}

}
