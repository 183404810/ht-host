package ht.plugin.util;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class Tools {
	private static MessageConsoleStream consoleStream = null;
	static { 
		IConsoleManager consoleManager=ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] consoles=consoleManager.getConsoles();
		if(consoles!=null && consoles.length>0){
			IConsole console=consoles[0];
			if(console instanceof MessageConsole)
				consoleStream=((MessageConsole)console).newMessageStream();
		}
		else{
			MessageConsole console=new MessageConsole("autoMyBatis",null);
			consoleManager.addConsoles(new IConsole[]{console});
			consoleStream=console.newMessageStream();
			consoleManager.showConsoleView(console);
		}
	}
	
	public static void println(String message){
		if(consoleStream!=null)
			consoleStream.println(message);
	}
	public static void printlin(){
		if(consoleStream!=null)
			consoleStream.println();
	}
}
