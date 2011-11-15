/**
 * Display the main Skink frame, menus etc.
 *
 * @author Wil Macaulay
 * @version 1.0a2 1 mar 2002
 */

/* 
 MainFrame.java

 Title:			Skink 
 Author:			macaulay
 Description:	Simple Java abc notation display
 0.1 - 0.6 various updates (WM)
 0.7  27 Apr 2001  WM  cleanup to allow continuous redisplay
 0.8  27 May 2001 WM  disable continuous display
 3  jun 2001 WM  set initial divider 
 10 jun 2001 WM  continuous display, multi tune display, print
 0.9a 13 jul 2001 WM  debug parser menu item 
 0.9d 8  jan 2002 wm  use StringBuffer instead of String for read 
 1.0a1 17 feb 2002 wm  start cleanup, javadocs etc
 1.0a2 1 mar 2002 wm basic player menu   
 1.0a2 16 mar 2002 wm improve layout
 1.0a3 26 mar 2002 wm check for available player
 4 mar 2002 wm convert Vectors to Lists
 1.0a4 29 may 2002 wm scrolling improvements 
 1.0a5  5 jun 2002 wm basic undo/redo   
 19 jun 2002 wm about box improvements   
 1.0a6 23 jun 2002 wm refactor for tools menu
 1.0a7 10 jul 2002 wm refactor for multi-file
 1.0b1 16 jul 2002 wm new error handling 
 1.0b2 18 jul 2002 wm new error handling, display menu
 1.0c1-4 29 jul 2002 wm fix open events
 1.0d1 17 sep 2002 wm improved tuplet handling
 1.0e1 9 jan 2003 wm improved MIDI playback
 1.0f1 3 aug 2003 wm selected tune/error foreground colour
 7 aug 2003 wm display menu refactoring, 'wide' display
 1.2a1 5 aug 2004 wm zoom in and out
 2.0a2 6 feb 2006 wm more refactoring, goto menu	
 2.0f0 10 apr 2008 wm UI improvements on continuous edit mode 

 */

package ca.celticmusic.skink;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.apple.eawt.Application;

import net.roydesign.mac.MRJAdapter;

import ca.celticmusic.skink.abcparser.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	// factory - keeps track of windows
	static int classDebugLevel = 4;

	static ArrayList<MainFrame> frameList = new ArrayList<MainFrame>();

	static boolean useMulti = true;	
	static synchronized MainFrame newMainFrame(){
		MainFrame theFrame = new MainFrame();
		frameList.add(theFrame);
		return theFrame;
	}
	static synchronized ArrayList<MainFrame>  getMainFrames(){
		return new ArrayList<MainFrame>(frameList);

	}
	static synchronized void removeFrame(MainFrame theFrame){
		if(frameList.remove(theFrame)){
			theFrame.dispose();
		}
		if (frameList.size() == 0){
			System.exit(0);
		}

	}
	static synchronized MainFrame getFrameForFile(File theFile){
		if (theFile == null) return null;
		for (MainFrame theFrame :frameList){
			if (theFile.equals(theFrame.getCurrentFile()))return theFrame;
		}
		return null;

	}
	static synchronized void doCloseAll(){
		Debug.output(classDebugLevel, "closing all");
		ArrayList<MainFrame> frames = new ArrayList<MainFrame>(frameList);
		for (MainFrame theFrame : frames){
			Debug.output(classDebugLevel, "closing "+theFrame.getTitle());
			theFrame.doClose();  // will remove itself frome frameList only if close not cancelled		

		}
	}

	int debugLevel = 3;
	String debugString = "mainFrame";
	static boolean debugMenuVisible = false;//true;
	static boolean debugParser = true;

	static boolean setDivider = false;
	static boolean toolsMenuVisible = true;

	static boolean screenMenu = false;

	static boolean useMRJ = false;

	String initText = SkinkConstants.defaultInitialText;

	String abcExternalTool; 
	String abcFormatFile;
	ConsoleProcess console ;
	PreferencesManager prefsManager;
	boolean saveNeeded = false;

	boolean displayNeeded = false;

	boolean contDisplay = false;

	boolean opening = false;

	String fileName = null;

	FileReader theFR = null;

	File currentFile = null;

	File currentDirectory = null;

	BufferedReader theBR = null;

	AbcParser parser;

	TuneBook theTuneBook;

	Tune selectedTune = null;

	boolean useAwtFileDialogs = false; //use AWT file dialogs under Mac OSX

	StaffPanel staffPanel = new StaffPanel();

	DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("<new>.abc");

	DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, true);

	PageFormat musicPageFormat = PrinterJob.getPrinterJob().defaultPage();

	//javax.swing.undo.UndoableEdit edit = null;
	CompoundEdit edit = null;

	// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
	// every time the form is saved. All manual changes will be overwritten.
	// BEGIN GENERATED CODE
	// member declarations
	javax.swing.JSplitPane tunePane = new javax.swing.JSplitPane();

	javax.swing.JScrollPane tunePanel = new javax.swing.JScrollPane();

	javax.swing.JScrollPane editScroller = new javax.swing.JScrollPane();

	javax.swing.JTextArea abcEdit = new javax.swing.JTextArea();

	//Menubar and menus

	javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

	//File menu

	javax.swing.JMenu jMenuFile = new javax.swing.JMenu();

	javax.swing.JMenuItem jMenuFileNew = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileOpen = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileFetch = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileSave = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileSaveAs = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileClose = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFilePrint = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFilePageSetup = new javax.swing.JMenuItem();

	javax.swing.JSeparator jMenuFileSeparator1 = new javax.swing.JSeparator();

	javax.swing.JMenuItem jMenuFileExit = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuFileAbout = new javax.swing.JMenuItem();

	//edit menu
	javax.swing.JMenu jMenuEdit = new javax.swing.JMenu();

	javax.swing.JMenuItem jMenuEditUndo = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditRedo = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditCut = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditCopy = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditPaste = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditSelectAll = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuEditGoTo = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuEditPreferences = new javax.swing.JMenuItem();

	//abc menu

	javax.swing.JMenu jMenuAbc = new javax.swing.JMenu();

	javax.swing.JMenuItem jMenuAbcCompile = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuAbcPlay = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuAbcStopPlay = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuAbcWriteMidi = new javax.swing.JMenuItem();	
	javax.swing.JMenuItem jMenuAbcInvokeTool = new javax.swing.JMenuItem();

	//Display menu

	javax.swing.JMenu jMenuDisplay = new javax.swing.JMenu();

	javax.swing.JRadioButtonMenuItem jMenuDisplayContinuous = new javax.swing.JRadioButtonMenuItem();

	javax.swing.JMenuItem jMenuDisplayWide = new javax.swing.JMenuItem();

	javax.swing.JScrollPane treeScroller = new javax.swing.JScrollPane();

	javax.swing.JTree tuneBookTree = new javax.swing.JTree();

	// window menu is managed centrally
	WindowMenu windowMenu;// = new WindowMenu();

	//debug menu

	javax.swing.JMenu jMenuDebug = new javax.swing.JMenu();

	javax.swing.JMenuItem jMenuDebugDump = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugShowAll = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugShowSettings = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugIncrN = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugDecrN = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugIncrD = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugDecrD = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugIncrH = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugDecrH = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugIncrW = new javax.swing.JMenuItem();

	javax.swing.JMenuItem jMenuDebugDecrW = new javax.swing.JMenuItem();

	javax.swing.JCheckBoxMenuItem jMenuDebugParser = new javax.swing.JCheckBoxMenuItem();

	// END GENERATED CODE

	public MainFrame() {
		initEnvironment();
	}

	synchronized void setOpening(boolean flag) {
		opening = flag;
	}

	synchronized boolean isOpening() {
		return opening;
	}

	private void initEnvironment() {
		Debug.init();

		Debug.output(2, "os.name=" + System.getProperty("os.name", "??"));
		Debug.output(2, "user.home=" + System.getProperty("user.home", "???"));
		console = new ConsoleProcess("Export to abcm2ps");
		prefsManager = PreferencesManager.getManager("Preferences",this.getClass());


		currentDirectory = new File(prefsManager.getPreference("abc directory", 
				System.getProperty("user.home", "."),true));
		abcExternalTool = prefsManager.getPreference("abcm2ps", 
				System.getProperty("ca.celticmusic.skink.abctool",SkinkConstants.defaultToolDirectory+SkinkConstants.defaultTool),true);
		abcFormatFile = prefsManager.getPreference("abcm2ps format file", "", true);
		//if (System.getProperty("os.name","unknown").equals("Mac OS X"))
		if (MRJAdapter.isAWTUsingScreenMenuBar()){
			//if (System.getProperty("mrj.version") != null) {
			// running on a Mac
			useAwtFileDialogs = true;
		}

		String javaVersion = System.getProperty("java.version", "???");
		Debug.output(2, "java.version=" + javaVersion);
		float jVersion = Float.valueOf(javaVersion.substring(0, 3))
		.floatValue();
		if (jVersion > 1.2) {
			setDivider = true;
		}
		//debugMenuVisible = Boolean.getBoolean("skink.debug.menuvisible");

		debugParser = Boolean.getBoolean("skink.debug.parser");
		// event handlers using MRJAdapter 

		AppleEventHandler a = new AppleEventHandler(this);

	}


	public void initComponents() throws Exception {
		Debug.output(debugLevel, debugString+".initCompontents: Debug visible - " + debugMenuVisible);

		setFrameSize();
		tuneBookTree.setModel(treeModel);

		// the following code sets the frame's initial state
		tunePane.setSize(new java.awt.Dimension(1200, 32767));
		tunePane.setMaximumSize(new java.awt.Dimension(20000, 32767));
		tunePane.setMinimumSize(new java.awt.Dimension(100, 20));
		tunePane.setPreferredSize(new java.awt.Dimension(1200, 32767));
		tunePane.setVisible(true);
		tunePane.setOpaque(false);
		tunePane.setOrientation(0);
		tunePane.setOneTouchExpandable(true);
		tunePane.setAlignmentY((float) 0.5);

		tunePanel.getViewport().add(staffPanel);
		tunePanel.getViewport().setBackground(Color.white);
		tunePanel.setMinimumSize(new java.awt.Dimension(200, 200));
		if (setDivider){			
			double ht = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int dividerLoc = (int)(ht*0.75);		
			tunePane.setDividerLocation(dividerLoc); 
		}
		tunePanel.setVisible(true);

		staffPanel.setVisible(true);
		staffPanel.setPreferredSize(new java.awt.Dimension(1200, 800));

		editScroller.setVisible(true);
		editScroller.getViewport().add(abcEdit);

		abcEdit.setVisible(true);
		String editorFontSize = prefsManager.getPreference("Editor Pane Font Size", 
				System.getProperty("ca.celticmusic.skink.editFontSize","11"),true);
		int fontSize = Integer.parseInt(editorFontSize);
		String editorFont = prefsManager.getPreference("Editor Font", 
				System.getProperty("ca.celticmusic.skink.editFont","Monospaced"),true);

		abcEdit.setFont(new java.awt.Font(editorFont, 0, fontSize));

		tunePane.add(tunePanel, javax.swing.JSplitPane.LEFT);
		tunePane.add(editScroller, javax.swing.JSplitPane.RIGHT);

		menuBar.setVisible(true);
		menuBar.add(jMenuFile);
		menuBar.add(jMenuEdit);
		menuBar.add(jMenuAbc);
		menuBar.add(jMenuDisplay);
		//menuBar.add(windowMenu);
		initDisplayMenu();

		if (debugMenuVisible)
			initDebugMenu();
		initFileMenu();

		initEditMenu();

		initAbcMenu();

		initTree();




		getContentPane().setLayout(new BoxLayout(getContentPane(), 0));
		setJMenuBar(menuBar);
		setTitle("Five Line Skink");
		setLocation(new java.awt.Point(0, 0));
		getContentPane().add(Box.createHorizontalStrut(5));
		getContentPane().add(tunePane);
		getContentPane().add(Box.createHorizontalStrut(5));
		getContentPane().add(treeScroller);

		tuneBookTree
		.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(
					javax.swing.event.TreeSelectionEvent e) {
				tuneBookTreeValueChanged(e);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				thisWindowClosing(e);
			}
		});
		// END GENERATED CODE

		// set up editor pane
		abcEdit.setBackground(Color.white);
		abcEdit.getDocument().addDocumentListener(
				new javax.swing.event.DocumentListener() {
					public void insertUpdate(javax.swing.event.DocumentEvent e) {
						Debug.output(debugLevel,
						"mainFrame.initComponents: insertUpdate");
						handleUpdate(e);
					}

					public void removeUpdate(javax.swing.event.DocumentEvent e) {
						Debug.output(debugLevel,
						"mainFrame.initComponents: removeUpdate");
						handleUpdate(e);
					}

					public void changedUpdate(javax.swing.event.DocumentEvent e) {
						Debug.output(debugLevel,
						"mainFrame.initComponents: changedUpdate");
					}

					/**
					 * @param e
					 */
					private void handleUpdate(javax.swing.event.DocumentEvent e) {
						saveNeeded = true;
						getRootPane().putClientProperty("windowModified", Boolean.TRUE);
						int lineOffset = 0;
						int offset = e.getOffset();
						try {
							lineOffset = abcEdit.getLineOfOffset(offset);
							Debug.output(debugLevel,
									"mainFrame.initComponents: insertUpdate - offset="
									+ offset + " length="
									+ e.getLength() + "Line="
									+ lineOffset);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						redisplayStaff(lineOffset);
					}

				});
		abcEdit.getDocument().addUndoableEditListener(
				new UndoableEditListener() {
					public void undoableEditHappened(UndoableEditEvent ev) {
						UndoableEdit newEdit = ev.getEdit();
						if (edit == null) {
							edit = new UndoManager();
						}

						if (!edit.addEdit(newEdit)) {
							// Debug.output(0,"can't absorb "+newEdit.getPresentationName()+" into "+edit.getPresentationName());
						} else {
							// Debug.output(0,"absorbed "+newEdit.getPresentationName()+ " into " +edit.getPresentationName());
						}

						updateUndoMenu();
					}
				});
		updateUndoMenu();
		if (!isOpening()) {
			initEditText();
		} else {
			if (currentFile != null)
				setTitle("Five Line Skink - " + currentFile.getCanonicalFile());
		}

		setOpening(false);
	}

	public File getCurrentFile(){
		return currentFile;
	}
	/**
	 * 
	 */
	private void setFrameSize() {
		// look for screen dimensions
		String frameWidth = prefsManager.getPreference("Initial Window Width", 
				System.getProperty("ca.celticmusic.skink.windowWidth","automatic"),true);
		String frameHeight = prefsManager.getPreference("Initial Window Height", 
				System.getProperty("ca.celticmusic.skink.windowHeight","automatic"),true);
		Dimension dim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());

		double wid = Math.min(dim.getWidth(), SkinkConstants.DEFAULT_INITIAL_WINDOW_WIDTH);
		double ht = dim.getHeight()-30.0; //back off a little.
		if (!frameWidth.equalsIgnoreCase("automatic")){
			wid = Math.min(wid, Integer.parseInt(frameWidth));
		}
		if (!frameHeight.equalsIgnoreCase("automatic")){
			ht = Math.min(ht, Integer.parseInt(frameHeight));
		}
		dim.setSize(wid,ht);
		setSize(dim);
		//setSize(new java.awt.Dimension(1000, 700));
	}

	/**
	 * 
	 */
	private void initTree() {
		treeScroller.setVisible(true);
		treeScroller.setSize(new java.awt.Dimension(SkinkConstants.BIG_TREE,
				32767));
		treeScroller.setMaximumSize(new java.awt.Dimension(
				SkinkConstants.BIG_TREE, 32767));
		treeScroller.setPreferredSize(new java.awt.Dimension(
				SkinkConstants.BIG_TREE, 32767));
		treeScroller
		.setToolTipText("Select an individual tune from the tree display");

		treeScroller.getViewport().add(tuneBookTree);

		tuneBookTree.setVisible(true);
		tuneBookTree.setCursor(java.awt.Cursor
				.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		tuneBookTree
		.setToolTipText("Select an individual tune from the tree display");
		tuneBookTree
		.getSelectionModel()
		.setSelectionMode(
				javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tuneBookTree.setCellRenderer(new TuneTreeRenderer());
	}

	/**
	 * 
	 */
	private void initAbcMenu() {
		jMenuAbc.setHorizontalTextPosition(4);
		jMenuAbc.setVisible(true);
		jMenuAbc.setText("Abc");
		jMenuAbc.setRequestFocusEnabled(false);
		jMenuAbc.setHorizontalAlignment(2);
		jMenuAbc.add(jMenuAbcCompile);
		jMenuAbc.add(jMenuAbcPlay);
		jMenuAbc.add(jMenuAbcStopPlay);
		jMenuAbc.add(jMenuAbcWriteMidi);
		jMenuAbc.add(jMenuAbcInvokeTool);

		initMenuItem(jMenuAbc,jMenuAbcCompile,"Show Tunes","Process the abc file and build tunes",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuAbcCompileActionPerformed(e);
			}
		});

		initMenuItem(jMenuAbc,jMenuAbcPlay,"Play Tunes","Play the selected tune(s)",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuAbcPlayActionPerformed(e);
			}
		});

		initMenuItem(jMenuAbc,jMenuAbcStopPlay,"Stop Playing","Stop Playing Tunes",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuAbcStopPlayActionPerformed(e);
			}
		});		

		initMenuItem(jMenuAbc, jMenuAbcWriteMidi,"Export to MIDI...","Export selected tune to MIDI file",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuAbcWriteMidiActionPerformed(e);
			}
		});

		initMenuItem(jMenuAbc, jMenuAbcInvokeTool, "Export to abcm2ps...","Format file using abcm2ps",
				new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jMenuAbcInvokeToolActionPerformed(e);
			}
		});



	}

	/**
	 * 
	 */
	private void initFileMenu() {
		jMenuFile.setHorizontalTextPosition(4);
		jMenuFile.setVisible(true);
		jMenuFile.setText("File");
		jMenuFile.setRequestFocusEnabled(false);
		jMenuFile.setHorizontalAlignment(2);
		jMenuFile.add(jMenuFileNew);
		jMenuFile.add(jMenuFileOpen);
		jMenuFile.add(jMenuFileFetch);
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileSaveAs);
		jMenuFile.add(jMenuFileClose);

		jMenuFile.add(jMenuFileSeparator1);
		jMenuFile.add(jMenuFilePageSetup);
		jMenuFile.add(jMenuFilePrint);
		if (!MRJAdapter.isAboutAutomaticallyPresent()) {
			jMenuFile.add(jMenuFileSeparator1);
			jMenuFile.add(jMenuFileAbout);
		}
		if (!MRJAdapter.isQuitAutomaticallyPresent()){
			jMenuFile.add(jMenuFileExit);
		}
		jMenuFileNew.setHorizontalTextPosition(4);
		jMenuFileNew.setVisible(true);
		jMenuFileNew.setText("New");
		jMenuFileNew.setActionCommand("new");
		jMenuFileNew.setToolTipText("Clear the abc to create a new tune");
		jMenuFileNew.setHorizontalAlignment(2);
		jMenuFileNew.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuFileOpen.setHorizontalTextPosition(4);
		jMenuFileOpen.setToolTipText("Open local abc file");
		jMenuFileOpen.setVisible(true);
		jMenuFileOpen.setText("Open...");
		jMenuFileOpen.setActionCommand("open");
		jMenuFileOpen.setHorizontalAlignment(2);
		jMenuFileOpen.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuFileFetch.setHorizontalTextPosition(4);
		jMenuFileFetch.setToolTipText("Fetch abc file from the internet");
		jMenuFileFetch.setVisible(true);
		jMenuFileFetch.setText("Fetch...");
		jMenuFileFetch.setActionCommand("fetch");
		jMenuFileFetch.setHorizontalAlignment(2);

		jMenuFileSave.setHorizontalTextPosition(4);
		jMenuFileSave.setVisible(true);
		jMenuFileSave.setText("Save");
		jMenuFileSave.setToolTipText("Save the current file");
		jMenuFileSave.setActionCommand("save");
		jMenuFileSave.setHorizontalAlignment(2);
		jMenuFileSave.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuFileSaveAs.setHorizontalTextPosition(4);
		jMenuFileSaveAs.setVisible(true);
		jMenuFileSaveAs.setText("Save As...");
		jMenuFileSaveAs
		.setToolTipText("Save the current file contents under a new name");
		jMenuFileSaveAs.setActionCommand("saveAs");
		jMenuFileSaveAs.setHorizontalAlignment(2);

		jMenuFileClose.setHorizontalTextPosition(4);
		jMenuFileClose.setVisible(true);
		jMenuFileClose.setText("Close");
		jMenuFileClose.setActionCommand("close");
		jMenuFileClose.setHorizontalAlignment(2);
		jMenuFileClose.setToolTipText("Close the current file");

		jMenuFilePrint.setHorizontalTextPosition(4);
		jMenuFilePrint.setVisible(true);
		jMenuFilePrint.setText("Print");
		jMenuFilePrint.setActionCommand("Print");
		jMenuFilePrint.setHorizontalAlignment(2);
		jMenuFilePrint.setToolTipText("Print the currently selected tunes");
		jMenuFilePrint.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_P, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuFilePageSetup.setHorizontalTextPosition(4);
		jMenuFilePageSetup.setVisible(true);
		jMenuFilePageSetup.setText("Page setup...");
		jMenuFilePageSetup.setActionCommand("Page Setup");
		jMenuFilePageSetup.setHorizontalAlignment(2);
		jMenuFilePageSetup.setToolTipText("Set up page printing layout");

		jMenuFileSeparator1.setVisible(true);

		jMenuFileExit.setHorizontalTextPosition(4);
		jMenuFileExit.setVisible(true);
		jMenuFileExit.setText("Exit");
		jMenuFileExit.setActionCommand("exit");
		jMenuFileExit.setHorizontalAlignment(2);
		jMenuFileExit.setToolTipText("Close the current file and exit");
		jMenuFileExit.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuFileAbout.setHorizontalTextPosition(4);
		jMenuFileAbout.setVisible(true);
		jMenuFileAbout.setText("About Skink...");
		jMenuFileAbout.setActionCommand("about");
		jMenuFileAbout.setHorizontalAlignment(2);
		jMenuFileAbout.setToolTipText("About Five Line Skink");

		jMenuFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileActionPerformed(e);
			}
		});
		jMenuFileNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileNewActionPerformed(e);
			}
		});

		jMenuFileOpen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileOpenActionPerformed(e);
			}
		});
		jMenuFileFetch.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileFetchActionPerformed(e);
			}
		});
		jMenuFileSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileSaveActionPerformed(e);
			}
		});
		jMenuFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileSaveAsActionPerformed(e);
			}
		});
		// close is the same as exit for now
		jMenuFileClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileCloseActionPerformed(e);
			}
		});

		jMenuFilePageSetup
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFilePageSetupActionPerformed(e);
			}
		});

		jMenuFilePrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFilePrintActionPerformed(e);
			}
		});

		jMenuFileExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuFileExitActionPerformed(e);
			}
		});
		jMenuFileAbout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doAbout();
			}
		});


	}

	/**
	 * 
	 */
	private void initEditMenu() {
		jMenuEdit.setHorizontalTextPosition(4);
		jMenuEdit.setVisible(true);
		jMenuEdit.setText("Edit");
		jMenuEdit.setRequestFocusEnabled(false);
		jMenuEdit.setHorizontalAlignment(2);

		jMenuEdit.add(jMenuEditUndo);
		jMenuEdit.add(jMenuEditRedo);
		jMenuEdit.add(jMenuEditCut);
		jMenuEdit.add(jMenuEditCopy);
		jMenuEdit.add(jMenuEditPaste);
		jMenuEdit.add(jMenuEditSelectAll);

		// undo, redo, cut copy paste selectAll
		jMenuEditUndo.setHorizontalTextPosition(4);
		jMenuEditUndo.setVisible(true);
		jMenuEditUndo.setText("Undo");
		jMenuEditUndo.setHorizontalAlignment(2);
		jMenuEditUndo.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuEditRedo.setHorizontalTextPosition(4);
		jMenuEditRedo.setVisible(true);
		jMenuEditRedo.setText("Redo");
		jMenuEditRedo.setHorizontalAlignment(2);
		jMenuEditRedo.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Y, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuEditCut.setHorizontalTextPosition(4);
		jMenuEditCut.setVisible(true);
		jMenuEditCut.setText("Cut");
		jMenuEditCut.setHorizontalAlignment(2);
		jMenuEditCut.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuEditCopy.setHorizontalTextPosition(4);
		jMenuEditCopy.setVisible(true);
		jMenuEditCopy.setText("Copy");
		jMenuEditCopy.setHorizontalAlignment(2);
		jMenuEditCopy.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuEditPaste.setHorizontalTextPosition(4);
		jMenuEditPaste.setVisible(true);
		jMenuEditPaste.setText("Paste");
		jMenuEditPaste.setHorizontalAlignment(2);
		jMenuEditPaste.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		jMenuEditSelectAll.setHorizontalTextPosition(4);
		jMenuEditSelectAll.setVisible(true);
		jMenuEditSelectAll.setText("Select All");
		jMenuEditSelectAll.setHorizontalAlignment(2);
		jMenuEditSelectAll.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		initMenuItem(jMenuEdit,jMenuEditGoTo,"Go To Line",
				"Move the cursor to a particular line number",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e){
				jMenuEditGoToActionPerformed(e);
			}
		});


		jMenuEditGoTo.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_G, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		if (!(MRJAdapter.isPreferencesAutomaticallyPresent())){
			initMenuItem(jMenuEdit,jMenuEditPreferences,"Preferences...",
					"Modify Skink preferences",
					new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e){
					doPreferences();
				}
			});
		} 

		jMenuEditUndo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditUndoActionPerformed(e);
			}
		});

		jMenuEditRedo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditRedoActionPerformed(e);
			}
		});

		jMenuEditCut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditCutActionPerformed(e);
			}
		});

		jMenuEditCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditCopyActionPerformed(e);
			}
		});
		jMenuEditPaste.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditPasteActionPerformed(e);
			}
		});
		jMenuEditSelectAll
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuEditSelectAllActionPerformed(e);
			}
		});




	}
	private void jMenuEditGoToActionPerformed(java.awt.event.ActionEvent e){
		String uString = (String) JOptionPane.showInputDialog(this,
				"Go To Line",
				"Line Number ", JOptionPane.QUESTION_MESSAGE, null, null,
		"0");
		if (uString != null) {
			doGoTo(Integer.parseInt(uString));
		}

	}
	private void initEditText() {
		abcEdit.setText(initText);
		saveNeeded = false;
		getRootPane().putClientProperty("windowModified", Boolean.FALSE);
	}

	private void initDisplayMenu() {

		final javax.swing.JMenuItem jMenuDisplayToggleTree = new javax.swing.JMenuItem();
		final javax.swing.JMenuItem jMenuDisplayZoomIn = new javax.swing.JMenuItem();
		final javax.swing.JMenuItem jMenuDisplayZoomOut = new javax.swing.JMenuItem();

		jMenuDisplay.setHorizontalTextPosition(4);
		jMenuDisplay.setVisible(toolsMenuVisible);
		jMenuDisplay.setText("Display");
		jMenuDisplay.setRequestFocusEnabled(false);
		jMenuDisplay.setHorizontalAlignment(2);
		jMenuDisplay.add(jMenuDisplayToggleTree);

		initMenuItem(jMenuDisplay, jMenuDisplayToggleTree, "Expand staff pane",
				"Grow/shrink the tune tree to give more space for music",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int curwidth = treeScroller.getPreferredSize().width;
				Debug.output(3, "size is" + curwidth);
				if (curwidth == SkinkConstants.BIG_TREE) {
					treeScroller
					.setPreferredSize(new java.awt.Dimension(
							SkinkConstants.SMALL_TREE, 32767));
					jMenuDisplayToggleTree.setText("Expand tune list");
				} else {
					treeScroller
					.setPreferredSize(new java.awt.Dimension(
							SkinkConstants.BIG_TREE, 32767));
					jMenuDisplayToggleTree.setText("Expand staff pane");
				}
				validate();
				repaint();
			}
		});

		initMenuItem(jMenuDisplay, jMenuDisplayContinuous, "Continuous",
				"Continuously update the staff as I type",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {

				contDisplay = jMenuDisplayContinuous.isSelected();
				Debug.output(1, "Continous display = " + contDisplay);
			}
		});

		initMenuItem(jMenuDisplay, jMenuDisplayWide, "Wide Music",
				"Wider or narrower staff display",
				new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				double curwidth = StaffPanel.getStaffWidth();
				Debug.output(3, "staff size is" + curwidth);
				if (curwidth == SkinkConstants.NARROW_STAFF_WIDTH) {
					StaffPanel.setStaffWidth(SkinkConstants.WIDE_STAFF_WIDTH);
					jMenuDisplayWide.setText("Narrow Music");
				} else {
					StaffPanel.setStaffWidth(SkinkConstants.NARROW_STAFF_WIDTH);
					jMenuDisplayWide.setText("Wide Music");
				}
				validate();
				repaint();
			}
		});

		initMenuItem(jMenuDisplay, jMenuDisplayZoomIn, "Zoom In",
				"Magnify staff", new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				staffPanel.zoomIn();
				validate();
				repaint();
			}
		});
		jMenuDisplayZoomIn.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		initMenuItem(jMenuDisplay, jMenuDisplayZoomOut, "Zoom Out",
				"Shrink staff", new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				staffPanel.zoomOut();
				validate();
				repaint();
			}
		});
		jMenuDisplayZoomOut.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));

		// init plugin tools (eventually)
		/* final AbcTool theTool = new DoubleSpaceTool();
		 JMenuItem toolItem = new JMenuItem();
		 initMenuItem(jMenuDisplay, toolItem, theTool.getMenuItem());
		 toolItem.addActionListener(new java.awt.event.ActionListener() {
		 public void actionPerformed(java.awt.event.ActionEvent e){
		 try {
		 theTool.doTool("test",null);
		 }
		 catch (Exception ex){
		 //ErrorDialog.show(this,ex);
		  }
		  }
		  });  */
	}

	public void updateUndoMenu() {
		if (edit != null) {
			jMenuEditUndo.setEnabled(edit.canUndo());
			jMenuEditRedo.setEnabled(edit.canRedo());
			jMenuEditUndo.setText(edit.getUndoPresentationName());
			jMenuEditRedo.setText(edit.getRedoPresentationName());
		} else {
			jMenuEditUndo.setEnabled(false);
			jMenuEditRedo.setEnabled(false);
			jMenuEditUndo.setText("Undo");
			jMenuEditRedo.setText("Redo");
		}
	}

	public void clearEdits() {
		if (edit != null) {
			edit.die();

			updateUndoMenu();
		}
	}

	private boolean mShown = false;

	public void addNotify() {
		super.addNotify();

		if (mShown)
			return;

		// resize frame to account for menubar
		JMenuBar jMenuBar = getJMenuBar();
		if (jMenuBar != null) {
			int jMenuBarHeight = jMenuBar.getPreferredSize().height;
			Dimension dimension = getSize();
			dimension.height += jMenuBarHeight;
			setSize(dimension);
		}

		mShown = true;
	}

	// Close the window when the close box is clicked
	void thisWindowClosing(java.awt.event.WindowEvent e) {
		doClose();
	}

	public void jMenuFileActionPerformed(java.awt.event.ActionEvent e)

	{
		Debug.output(1, "File menu" + e.getActionCommand());

	}

	public void jMenuDebugActionPerformed(java.awt.event.ActionEvent e) {
		Debug.output(1, "Debug menu" + e.getActionCommand());
	}

	public void jMenuFileCloseActionPerformed(java.awt.event.ActionEvent e) {
		Debug.output(1, "File menu close item " + e.getActionCommand());
		doClose();
	}

	public void jMenuFileExitActionPerformed(java.awt.event.ActionEvent e) {
		Debug.output(1, "File menu exit item " + e.getActionCommand());
		doExit();
	}

	public void jMenuFileNewActionPerformed(java.awt.event.ActionEvent e) {
		if (useMulti) {
			doNew();
		} else {
			if (!checkSaveNeeded()) // returns false if cancelled FIXME should be in doNew
				doNew();
		}
	}

	public void jMenuFileOpenActionPerformed(java.awt.event.ActionEvent e) {
		if (useMulti){
			MainFrame frame;
			try {
				frame = MainFrame.newMainFrame();
				frame.initComponents();
				frame.setVisible(true);
				frame.openFileDialog();
			} catch (Exception ex) {
				ErrorDialog.show(this, ex);
			}


		} else {
			if (!checkSaveNeeded()) // returns false if cancelled
				openFileDialog();
		}
	}

	public void jMenuFileSaveAsActionPerformed(java.awt.event.ActionEvent e) {
		saveFileDialog();
	}

	public void jMenuFileSaveActionPerformed(java.awt.event.ActionEvent e) {
		if (currentFile == null) {
			saveFileDialog();
		} else
			doSave();
	}

	public void jMenuFilePrintActionPerformed(java.awt.event.ActionEvent e) {
		doPrint();
	}

	public void jMenuFilePageSetupActionPerformed(java.awt.event.ActionEvent e) {
		doPageSetup();
	}

	// returns true if close cancelled by user
	public boolean checkSaveNeeded() {
		Debug.output(2, "Save needed?" + saveNeeded);
		boolean cancelled = false;
		if (saveNeeded) {
			String savePrompt = "The contents of this window have changed. Do you want to save?";

			if (fileName != null) 
				savePrompt = "The contents of file "+ fileName + " have changed. Do you want to save?";
			int select = JOptionPane.showConfirmDialog(this,savePrompt,
					"Save confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			Debug.output(3, "Option chosen " + select);
			switch (select) {
			case JOptionPane.YES_OPTION:
				if (currentFile == null) {
					saveFileDialog();
				} else
					doSave();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
				cancelled = true;
				break;
			default:
				break;
			}
		}
		return cancelled;
	}

	/**
	 * initialize printer paper margins
	 */
	public void initPaper() {
		// as of 25 mar 2002, only Windows allows margin setting through the dialog
		Paper paper = musicPageFormat.getPaper();
		double height = paper.getHeight();
		double width = paper.getWidth();
		double vMargin = 72 / 0.5; // vertical margin in points - half an inch
		double hMargin = 72 / 0.5; // horizontal margin
		try {
			paper.setImageableArea(hMargin, vMargin, (width - 2 * hMargin),
					(height - 2 * vMargin));
		} catch (Exception e) {
			ErrorDialog.show(this, e);
		}
	}

	/**
	 * page format dialog
	 */
	public void doPageSetup() {
		initPaper();
		musicPageFormat = PrinterJob.getPrinterJob()
		.pageDialog(musicPageFormat);
	}

	/**
	 * Print - G2D style
	 */
	public void doPrint() {
		//doPageSetup();
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(staffPanel, musicPageFormat);

		if (printJob.printDialog()) {
			try {
				//musicPageFormat = printJob.defaultPage(musicPageFormat);
				staffPanel.initPrint();
				printJob.print();
			} catch (Exception ex) {
				ErrorDialog.show(this, ex);
				ex.printStackTrace();
			}
		}
	}

	public void jMenuEditUndoActionPerformed(java.awt.event.ActionEvent e) {
		edit.undo();
		updateUndoMenu();
	}

	public void jMenuEditRedoActionPerformed(java.awt.event.ActionEvent e) {
		edit.redo();
		updateUndoMenu();
	}

	// cut      
	public void jMenuEditCutActionPerformed(java.awt.event.ActionEvent e) {
		doCut();
	}

	public void doCut() {
		Debug.output(1, "Cut action");
		abcEdit.cut();
	}

	// copy   
	public void jMenuEditCopyActionPerformed(java.awt.event.ActionEvent e) {
		doCopy();
	}

	public void doCopy() {
		Debug.output(1, "Copy action");
		abcEdit.copy();
	}

	public void jMenuEditPasteActionPerformed(java.awt.event.ActionEvent e) {
		doPaste();
	}

	public void doPaste() {
		Debug.output(1, "Paste action");
		abcEdit.paste();
	}

	public void jMenuEditSelectAllActionPerformed(java.awt.event.ActionEvent e) {
		doSelectAll();
	}

	public void doSelectAll() {
		Debug.output(1, "SelectAll action");
		abcEdit.selectAll();
	}

	public void doExit() {
		MainFrame.doCloseAll();


	}
	public void doClose() {
		if (!checkSaveNeeded()) { // returns true if cancelled
			setVisible(false);
			MainFrame.removeFrame(this);
		}

	}
	public void saveFileDialog() {
		if (useAwtFileDialogs) {
			saveFileDialogAWT();
		} else {
			saveFileDialogSwing();
		}
	}

	public void saveFileDialogAWT() {
		Debug.output(3, "save file dialog- AWT");
		FileDialog fD = null;
		fD = new FileDialog(this, "Save ABC file", FileDialog.SAVE);
		String dirString = null;
		try {
			dirString = currentDirectory.getCanonicalPath();
		} catch (IOException ioe) {
			Debug.output(3, "exception on directory canonical path" + ioe);
		}

		Debug.output(3, "current directory " + dirString);
		fD.setDirectory(dirString);
		if (currentFile == null) {
			fD.setFile("untitled.abc");
		} else {
			fD.setFile(currentFile.getName());
		}


		fD.setVisible(true);
		String fn = fD.getFile();
		String fDir = fD.getDirectory();
		Debug.output(3, "Save file as " + fn);
		if (fn != null) {

			Debug.output(3, "save file " + fn);
			try {
				currentFile = new File(fDir, fn);
				doSave();

			} catch (Exception e) {
				ErrorDialog.show(this, e);
			}
		}
	}

	public void saveFileDialogSwing() {
		Debug.output(1, "save file dialog");
		JFileChooser fD = null;
		if (currentFile == null) {
			fD = new JFileChooser("ABC file");
			if (currentDirectory != null)
				fD.setCurrentDirectory(currentDirectory);
		} else {
			fD = new JFileChooser(currentFile.getName());
			fD.setCurrentDirectory(new File(currentFile.getParent()));
		}
		int fResult = fD.showSaveDialog(this);

		if (fResult == JFileChooser.APPROVE_OPTION) {
			String fn = fD.getSelectedFile().getName();
			Debug.output(1, "save file " + fn);
			try {
				currentFile = fD.getSelectedFile();
				doSave();

			} catch (Exception e) {
				ErrorDialog.show(this, e);
			}
		}
	}

	// save the file	  
	public void doSave() {
		if (currentFile == null) {
			Debug.output(1, "trying to save to null file");
			return;
		}

		String theText = abcEdit.getText();
		try {
			FileWriter fstrm = new FileWriter(currentFile);
			fstrm.write(theText);
			fstrm.flush();
			fstrm.close();
			currentDirectory = new File(currentFile.getParent());
			getRootPane().putClientProperty("windowModified", Boolean.FALSE);
			setTitle("Five Line Skink - " + currentFile.getCanonicalFile());
			setFileName(currentFile.getName()); // in case we did saveAs
			doCompile(); // forces refresh
			getRootPane().putClientProperty("windowModified", Boolean.FALSE);
			saveNeeded = false;

		} catch (Exception e) {
			ErrorDialog.show(this, e);
		}
	}

	public void openFileDialog() {
		//useAwtFileDialogs = false;
		if (useAwtFileDialogs) {
			openFileDialogAWT();
		} else {
			openFileDialogSwing();
		}
	}

	public void openFileDialogAWT() {
		Debug.output(3, "open file dialog- AWT");
		FileDialog fD = null;
		fD = new FileDialog(this, "Open ABC file", FileDialog.LOAD);
		String dirString = null;
		try {
			dirString = currentDirectory.getCanonicalPath();
		} catch (IOException ioe) {
			Debug.output(debugLevel, "exception on directory canonical path" + ioe);
			ErrorDialog.show(this, ioe);
		}

		Debug.output(1, "current directory " + dirString);
		fD.setDirectory(dirString);


		fD.setVisible(true);
		String fn = fD.getFile();
		String fDir = fD.getDirectory();
		Debug.output(3, "Open file " + fn);
		if (fn != null) {
			Debug.output(3, "Open file " + fn);
			try {
				File theFile = new File(fDir, fn);
				doOpen(fn, theFile);
			} catch (Exception e) {
				ErrorDialog.show(this, e);
			}
		}
	}

	public void openFileDialogSwing() {
		JFileChooser fD = new JFileChooser("ABC file");
		// fD.addChoosableFileFilter(new ExampleFileFilter(new String[]{"abc","ABC"},"abc music files"));
		// not set -> current (app) directory
		// set to "." -> goes to root directory 
		// set to "~/"
		if (currentDirectory != null)
			fD.setCurrentDirectory(currentDirectory);
		// fD.setFilenameFilter(new MyFilenameFilter());  // no Swing
		int fResult = fD.showOpenDialog(this);

		if (fResult == JFileChooser.APPROVE_OPTION) {
			try {
				String fileName = fD.getSelectedFile().getName();
				doOpen(fileName, fD.getSelectedFile());
			} catch (Exception e) {
				ErrorDialog.show(this, e);
			}
		}
	}

	public void doNew() {
		// note: to properly do multi-document, I need to handle a single About box, multi-doc close,
		// multi-doc quit, window menu, etc.  Needs significant refactoring.

		if (useMulti) {
			try {
				MainFrame frame = MainFrame.newMainFrame();
				frame.initComponents();
				frame.initWindowMenu();
				frame.setVisible(true);
			} catch (Exception e) {
				ErrorDialog.show(this, e);
			}
		} else {
			Debug.output(3, "doNew");
			setFileReader(null);
			setFileName(null);
			currentFile = null;
			setTitle("Five Line Skink <new file>");
			selectedTune = null;
			abcEdit.setText("");
			clearEdits();
			doCompile();
			saveNeeded = false;
			getRootPane().putClientProperty("windowModified", Boolean.FALSE);
		}
	}

	public void doOpen(String inFile) throws IOException {

		Debug.output(1, "Opening by name " + inFile);
		File canFile = (new File(inFile)).getCanonicalFile();
		Debug.output(1, "file directory " + canFile.getParent());
		doOpen(inFile, canFile);
	}

	public void doOpen(File fD) throws IOException {
		Debug.output(1, "Opening file by FD" + fD.getName());
		doOpen(fD.getName(), fD);
	}

	public void doOpen(String inFile, File fD) throws IOException {
		// check if the file is already open in another window
		MainFrame theFrame = MainFrame.getFrameForFile(fD);
		if (theFrame != null){
			doClose();
			theFrame.popToFront();
			ErrorDialog.show(theFrame,new Exception("The file " + inFile +" is already open"));

		} else {
			setOpening(true);
			Debug.output(1, "opening file name " + inFile);
			FileReader fr = new FileReader(fD);
			Debug.output(1, "got file reader");
			setFileReader(fr);
			setFileName(inFile);
			selectedTune = null;
			currentFile = fD;
			currentDirectory = new File(currentFile.getParent());
			prefsManager.setPreference("abc directory", currentDirectory.getCanonicalPath());
			setTitle("Five Line Skink - " + currentFile.getCanonicalFile());
			Debug.output(1, "directory=" + currentDirectory);
			initContents();
			saveNeeded = false;
			getRootPane().putClientProperty("windowModified", Boolean.FALSE);
		}

	}
	public void popToFront(){
		setVisible(false);
		setVisible(true);
		toFront();
	}

	public void jMenuShowAllActionPerformed(java.awt.event.ActionEvent e) {
		if (theTuneBook != null) {
			staffPanel.setTunes(theTuneBook.getTunes());
			//staffPanel.invalidate();
			validate();
			repaint();
		}
	}

	public void setFileName(String theName) {
		fileName = theName;
	}

	public String getFileText(BufferedReader br) {
		// String retString = "";
		StringBuffer retString = new StringBuffer();
		try {
			while (br.ready()) {
				retString.append(br.readLine());
				retString.append("\n"); // here could convert line endings
				//retString += br.readLine()+"\n";
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		//return retString;
		return retString.toString();
	}

	public void setFileReader(FileReader fr) {

		theFR = fr;
		theBR = null;
		if (theFR != null)
			theBR = new BufferedReader(fr);
	}

	public void initContents() {

		abcEdit.setText(getFileText(theBR));
		clearEdits();
		doCompile();
		doGoTo(1);
		initWindowMenu();
		setVisible(false);
		setVisible(true);

	}


	public void initWindowMenu(){
		setVisible(false);
		windowMenu = new WindowMenu();
		menuBar.add(windowMenu);
		windowMenu.populateMenu();
		windowMenu.setVisible(true);

	}

	public void doExportMidi(File midiFile) {
		Player p = PlayerFactory.getPlayer();
		if (p == null) {
			ErrorDialog.show(this, new Exception(
			"Can't export MIDI - no player installed"));
		} else {
			p.stopPlay(); // stop if started
			try {
				p.write(midiFile, getTreeSelectedTunes(), 1);
			} catch (Exception e) {
				ErrorDialog.show(this, e);
				e.printStackTrace();
			}
		}
	}

	public void doPlay() {
		Player p = PlayerFactory.getPlayer();
		if (p == null) {
			ErrorDialog.show(this, new Exception(
			"Can't play tunes - no player installed"));
		} else {
			p.stopPlay(); // stop if started
			try {
				p.play(getTreeSelectedTunes(), 1);
			} catch (Exception e) {
				ErrorDialog.show(this, e);
				e.printStackTrace();
			}
		}
	}

	public void doStopPlay() {
		Player p = PlayerFactory.getPlayer();
		if (p == null) {
			ErrorDialog.show(this, new Exception("no player installed"));
		} else {
			p.stopPlay();
		}
	}

	public void doCompile() {

		staffPanel.setTunes(null);
		validate();
		repaint();
		parseText(abcEdit.getText());

		validate();
		repaint();
	}

	public void doDump() {
		if (theTuneBook != null) {
			theTuneBook.dumpTunes();
		}
	}

	public void parseText(String theText) {
		theTuneBook = null; // get rid of old tunebook to reduce memory footprint
		treeRoot.removeAllChildren();
		treeRoot.setUserObject(null);
		parser = new AbcParser(new StringReader(theText));
		if (!debugParser)
			parser.disable_tracing();
		else
			parser.enable_tracing();
		try {
			parser.AbcFile();
			theTuneBook = parser.getTuneBook();
			theTuneBook.setName(fileName);
			treeRoot.setUserObject(theTuneBook);
			treeRoot.removeAllChildren(); // get rid of old children
			int index = 0;
			for (ListIterator i = theTuneBook.getTunes().listIterator(); i
			.hasNext();) {
				Tune thisTune = (Tune) i.next();
				if (thisTune.isErrorDetected()) {
					int errorIndex = 0;
					DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(
							thisTune, true);
					treeModel.insertNodeInto(thisNode, treeRoot, index++);
					for (ListIterator j = thisTune.getErrors().listIterator(); j
					.hasNext();) {
						treeModel.insertNodeInto(new DefaultMutableTreeNode(j
								.next(), false), thisNode, errorIndex++);
					}
				} else {
					treeModel.insertNodeInto(new DefaultMutableTreeNode(
							thisTune, false), treeRoot, index++);
				}
			}
			// now redisplay
			treeModel.reload();
		}

		catch (TokenMgrError e) {
			ErrorDialog.show(this, e);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.show(this, e);
		}
	}

	// redisplay staff on input - experimental

	public void redisplayStaff(int atLine) {
		// existing selection?
		if (contDisplay) {
			// recompile
			staffPanel.setTunes(null);
			parseText(abcEdit.getText());

			//doCompile();   
			if (theTuneBook != null)
				selectEditedTunes(atLine);
		}
	}

	// on continuous display, tune is selected by the current line
	// showTunes(tunes) will be called from tuneBookTreeValueChanged;
	boolean continuousEditInProgress;
	public void selectEditedTunes(int atLine) {
		// note: if you don't get the existing root from the tree model but make a new one, the
		// path selection will appear to work, but will actually be selecting a path not in the
		// visible tree.
		TreePath newSelectionPath = new TreePath(treeModel.getRoot());
		Debug.output(debugLevel, "mainFrame.showTunes: showing tune at line "+atLine);
		continuousEditInProgress = true;

		int index = theTuneBook.getTuneAtLine(atLine);
		Tune atTune = theTuneBook.getTuneAt(index);
		Debug.output(debugLevel, debugString+".showTunes: tune number "+index+  " is "+atTune);
		if (!(atTune==null)){

			DefaultMutableTreeNode tuneNode = 
				(DefaultMutableTreeNode) treeModel.getChild(treeModel.getRoot(), index);

			newSelectionPath = newSelectionPath.pathByAddingChild(tuneNode);
		}
		//tuneBookTree.setScrollsOnExpand(true);
		tuneBookTree.setSelectionPath(newSelectionPath);

		tuneBookTree.makeVisible(newSelectionPath);
		tuneBookTree.scrollPathToVisible(newSelectionPath);

	}

	public void showTunes(java.util.List<Tune> selTunes) {
		staffPanel.setTunes(selTunes);
		if (!continuousEditInProgress) {
			if (selTunes.size() > 0) {
				scrollAbc( selTunes.get(0));
			} else {
				doGoTo(1, 1);
			}
		}
		continuousEditInProgress = false;
		validate();
		repaint();
	}

	// selection changed - new set of tunes selected?	
	public void tuneBookTreeValueChanged(javax.swing.event.TreeSelectionEvent e) {
		java.util.List tunes = getTreeSelectedTunes();
		Debug.output(debugLevel, debugString + ".tuneBookTreeValueChanged" +e);
		if (tunes != null) {
			showTunes(tunes);
		}
	}

	// get the selected tunes
	public java.util.List getTreeSelectedTunes() {
		java.util.List tunes = new ArrayList();
		TreePath[] selPaths = tuneBookTree.getSelectionPaths();
		DefaultMutableTreeNode theNode = null;
		boolean tuneSelected = false;
		if (selPaths != null) {
			// turn the selection paths into tunes
			for (int i = 0; i < selPaths.length; i++) {
				theNode = (DefaultMutableTreeNode) selPaths[i]
				                                            .getLastPathComponent();
				if (theNode != null) {
					// is the node the tunebook
					if (theNode.getUserObject() instanceof TuneBook)
						return theTuneBook.getTunes();
					// is the node a Tune?
					if (theNode.getUserObject() instanceof Tune) {
						tunes.add(theNode.getUserObject()); // use addElement -> 1.1
						tuneSelected = true;
					}
					if (theNode.getUserObject() instanceof TuneErrorItem) {
						scrollAbc((TuneErrorItem) (theNode.getUserObject()));
						abcEdit.requestFocus();
					} else {
						Debug.output(1, "selected non-tune");
					}
				}

			}
		}
		if (tuneSelected)
			return tunes;
		return null;
	}

	// get the selected TreeNode
	public DefaultMutableTreeNode getSelectedNode() {
		TreePath selPath = tuneBookTree.getSelectionPath();

		if (selPath != null)
			return (DefaultMutableTreeNode) selPath.getLastPathComponent();
		return null;
	}

	public void jMenuAbcCompileActionPerformed(java.awt.event.ActionEvent e) {
		doCompile();
	}

	private void jMenuAbcInvokeToolActionPerformed(ActionEvent e) {
		// make sure there is an installed copy of abcm2ps
		Debug.output(3, debugString+ ".jMenuAbcInvokeToolActionPerformed: file is "+fileName);
		// ensure the current file is saved - might still be null

		if(!checkSaveNeeded()){
			if(currentFile!=null){

				try {
					abcExternalTool = prefsManager.getPreference("abcm2ps", 
							System.getProperty("ca.celticmusic.skink.abctool",SkinkConstants.defaultToolDirectory+SkinkConstants.defaultTool),true);
					abcFormatFile = prefsManager.getPreference("abcm2ps format file", "", true);

					String filePath = currentFile.getCanonicalPath();
					ArrayList<String> args = new ArrayList<String>();
					args.add(abcExternalTool);
					args.add(filePath);
					args.add("-O");
					args.add("=");
					abcFormatFile = prefsManager.getPreference("abcm2ps format file", "", true);
					if ((abcFormatFile != null) && (abcFormatFile.length()!=0)){
						args.add("-F");
						args.add(abcFormatFile);
					}
					console.execProcess(currentDirectory,args);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ErrorDialog.show(this,new Exception("Unable to invoke abcm2ps - "+abcExternalTool));
				}
			}
			else{
				ErrorDialog.show(this,new Exception("Please save the current abc to a file."));
			}
		}


	}

	public void jMenuAbcPlayActionPerformed(java.awt.event.ActionEvent e) {
		doPlay();
	}

	public void jMenuAbcStopPlayActionPerformed(java.awt.event.ActionEvent e) {
		doStopPlay();
	}

	public void jMenuAbcWriteMidiActionPerformed(java.awt.event.ActionEvent e) {
		Debug.output(1, "export midi file dialog");
		JFileChooser fD = null;
		fD = new JFileChooser("MIDI file");
		if (currentDirectory != null)
			fD.setCurrentDirectory(currentDirectory);
		int fResult = fD.showSaveDialog(this);

		if (fResult == JFileChooser.APPROVE_OPTION) {
			File midiFile = fD.getSelectedFile();
			try {
				doExportMidi(midiFile);
			} catch (Exception ex) {
				ErrorDialog.show(this, ex);
			}
		}
	}

	// go to a particular line

	// scroll the abc text pane to the correct spot
	// note: the text ends up at the bottom of the pane - how to fix?
	// for now, add 1 so that the title and index are displayed
	public void scrollAbc(TuneTreeItem item) {
		doGoTo(item.getStartLine(), item.getStartColumn());
	}

	// scroll the staff pane  (repainting is done by the caller)     
	public void scrollStaff() {
		JViewport vp = tunePanel.getViewport();
		vp.setViewPosition(new Point(0, 0));
	}

	public void doGoTo(int line) {
		doGoTo(line, 1);
	}

	public void doGoTo(int line, int col) {
		int lineStart = 0;
		try {
			lineStart = abcEdit.getLineStartOffset(line - 1);
			JViewport vp = editScroller.getViewport();
			Rectangle r = abcEdit.modelToView(lineStart);
			vp.setViewPosition(new Point(r.x, r.y));

			Debug.output(1, "scrolling to row " + (line - 1) + " - offset is "
					+ lineStart);
		} catch (Exception e) {
			ErrorDialog.show(this, e);
		}
		abcEdit.setCaretPosition(lineStart + col);
		abcEdit.getCaret().setVisible(true);
		//

		abcEdit.repaint();
	}

	/**
	 *  fetch an abc file by url
	 */

	public void jMenuFileFetchActionPerformed(java.awt.event.ActionEvent e) {
		// create a dialog box for the url
		String uString = (String) JOptionPane.showInputDialog(this,
				"Please enter the internet address [http://www...]",
				"Fetch URL ", JOptionPane.QUESTION_MESSAGE, null, null,
		"http://www.");
		if (uString != null) {
			doFetch(uString);
		}
	}

	public void doFetch(String uString) {
		Debug.output(1, "trying to open url: " + uString);
		try {
			URL abcUrl = new URL(uString);

			Debug.output(1, "trying to read contents");
			setTitle(uString);
			setFileName(null);
			currentFile = null;
			selectedTune = null;
			abcEdit.read(new InputStreamReader(abcUrl.openStream()), null);

		} catch (Exception ex) {
			ErrorDialog.show(this, ex);
		}
	}

	public void jMenuDebugDumpActionPerformed(java.awt.event.ActionEvent e) {
		doDump();
	}

	public void doAbout() {
		String msgString = "<html>"
			+"Five Line Skink version "
			+ SkinkConstants.skinkVersion
			+ "<br>"
			+ SkinkConstants.skinkCopyright
			+ "<br>"
			+ SkinkConstants.skinkUrl
			+"<br>"
			+ "MRJAdapter Version "+ MRJAdapter.VERSION
			+ "<br>"
			+ "Java VM version: " + System.getProperty("java.version")
			+ " on " + System.getProperty("os.name") 

			+ "</html>";
		JOptionPane.showMessageDialog(this, msgString, "About Skink",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void doShowSettings() {
		String msgString = "Scale Settings:" + "\nN=" + StaffPanel.getScaleN()
		+ "\nD=" + StaffPanel.getScaleD() + "\nH="
		+ StaffPanel.getScaleH() + "\nW=" + StaffPanel.getStaffWidth();
		JOptionPane.showMessageDialog(this, msgString, "Settings",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void initDebugMenu() {
		menuBar.add(jMenuDebug);

		jMenuDebug.setHorizontalTextPosition(4);
		jMenuDebug.setVisible(debugMenuVisible);
		jMenuDebug.setText("Debug");
		jMenuDebug.setRequestFocusEnabled(false);
		jMenuDebug.setHorizontalAlignment(2);
		jMenuDebug.add(jMenuDebugDump);

		jMenuDebugDump.setHorizontalTextPosition(4);
		jMenuDebugDump.setVisible(true);
		jMenuDebugDump.setText("Dump");
		jMenuDebugDump.setHorizontalAlignment(2);

		initMenuItem(jMenuDebug, jMenuDebugShowAll, "Show All");
		initMenuItem(jMenuDebug, jMenuDebugShowSettings, "Show settings");

		initMenuItem(jMenuDebug, jMenuDebugIncrN, "N++");
		initMenuItem(jMenuDebug, jMenuDebugDecrN, "N--");
		initMenuItem(jMenuDebug, jMenuDebugIncrD, "D++");
		initMenuItem(jMenuDebug, jMenuDebugDecrD, "D--");
		initMenuItem(jMenuDebug, jMenuDebugIncrH, "H++");
		initMenuItem(jMenuDebug, jMenuDebugDecrH, "H--");
		initMenuItem(jMenuDebug, jMenuDebugIncrW, "W++");
		initMenuItem(jMenuDebug, jMenuDebugDecrW, "W--");
		initMenuItem(jMenuDebug, jMenuDebugDecrW, "W--");
		initMenuItem(jMenuDebug, jMenuDebugParser, "Debug Parser");

		jMenuDebug.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuDebugActionPerformed(e);
			}
		});
		jMenuDebugDump.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuDebugDumpActionPerformed(e);
			}
		});
		jMenuDebugShowAll
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				jMenuShowAllActionPerformed(e);
			}
		});

		jMenuDebugIncrN.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleN(StaffPanel.getScaleN() + 1);
				tunePanel.repaint();
			}
		});
		jMenuDebugDecrN.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleN(StaffPanel.getScaleN() - 1);
				tunePanel.repaint();
			}
		});
		jMenuDebugIncrD.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleD(StaffPanel.getScaleD() + 1);
				tunePanel.repaint();
			}
		});
		jMenuDebugDecrD.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleD(StaffPanel.getScaleD() - 1);
				tunePanel.repaint();
			}
		});

		jMenuDebugIncrH.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleH(StaffPanel.getScaleH() + 1);
				tunePanel.repaint();
			}
		});

		jMenuDebugDecrH.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setScaleH(StaffPanel.getScaleH() - 1);
				tunePanel.repaint();
			}
		});
		jMenuDebugIncrW.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setStaffWidth(StaffPanel.getStaffWidth() + 10);
				tunePanel.repaint();
			}
		});
		jMenuDebugDecrW.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				StaffPanel.setStaffWidth(StaffPanel.getStaffWidth() - 1);
				tunePanel.repaint();
			}
		});
		jMenuDebugShowSettings
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doShowSettings();

			}
		});
		jMenuDebugParser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				debugParser = jMenuDebugParser.getState();
				;
			}

		});
	}

	public void initMenuItem(JMenu menu, JMenuItem mItem, String text) {
		mItem.setHorizontalTextPosition(4);
		mItem.setVisible(true);
		mItem.setText(text);
		mItem.setHorizontalAlignment(2);
		menu.add(mItem);
	}

	public void initMenuItem(JMenu menu, JMenuItem mItem, String text,
			String toolTipText, java.awt.event.ActionListener theListener) {
		initMenuItem(menu, mItem, text);
		mItem.setToolTipText(toolTipText);
		mItem.addActionListener(theListener);
	}

	public void doPreferences() {
		Debug.output(debugLevel, "doPrefs");
		if(prefsManager.editPreferences()){
			Debug.output(debugLevel, "updating prefs");
		}

	}
}
