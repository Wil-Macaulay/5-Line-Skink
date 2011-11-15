package ca.celticmusic.skink;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.ArrayList;



@SuppressWarnings("serial")
public class ConsoleProcess extends JFrame{
	Process p;
	JTextArea consoleArea;
	JScrollPane consoleScroller;
	int debugLevel = 3;
	String debugString = "ConsoleProcess";

	public ConsoleProcess(String title){
		super(title);
		getContentPane().setLayout(new BoxLayout(getContentPane(), 0));//FlowLayout());


		consoleArea = new JTextArea("",20,80);
		consoleArea.setLineWrap(false);
		consoleArea.setEditable(false);
		consoleScroller = new JScrollPane(consoleArea);
		consoleScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(consoleScroller);
		pack();
		//consoleScroller.add(consoleArea);
	}
	public void execProcess(File directory,List<String> command){
		setVisible(false);
		setVisible(true);
		toFront();
		Date d = new Date();
		consoleArea.append("\n--"+d+"--\n");
		String commandLine = "Executing command:\n";
		for (String cmd : command){
			commandLine = commandLine + " "+ cmd;
		}
		consoleArea.append(commandLine);


		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			consoleArea.append("\nworking directory: "+directory.getCanonicalPath());
			pb.redirectErrorStream(true);
			pb.directory(directory);
			p = pb.start();
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String outLine;
			while((outLine = br.readLine()) != null){
				Debug.output(debugLevel, debugString + outLine);
				consoleArea.append("\n"+outLine);
				//FIXME: scroll to bottom
			}
			p.waitFor();
		} catch (Exception e1) {
			ErrorDialog.show(this,e1);
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void execProcess(String abcExternalTool,File currentFile){
		setVisible(true);
		try {
			String filePath = currentFile.getCanonicalPath();

			ProcessBuilder pb = new ProcessBuilder(abcExternalTool,filePath,"-O","=");
			pb.redirectErrorStream(true);
			p = pb.start();
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String outLine;
			Date d = new Date();
			consoleArea.append("--"+d+"--");
			while((outLine = br.readLine()) != null){
				Debug.output(debugLevel, debugString + outLine);
				consoleArea.append("\n"+outLine);
				//FIXME: scroll to bottom
			}
			p.waitFor();
		} catch (Exception e1) {
			ErrorDialog.show(this,e1);
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
