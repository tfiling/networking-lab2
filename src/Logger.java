import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {
	
	public static boolean logEnabled = false;
	private PrintWriter logWriter;
	private static Logger instance = null;
	
	
	public static Logger getLoggerInstance(boolean logEnabled) throws FileNotFoundException
	{
		if (instance == null)
		{
			Logger.instance = new Logger();
		}
		Logger.logEnabled = logEnabled;
		return instance;
	}
	
	public static Logger getLoggerInstance() throws FileNotFoundException
	{
		if (instance == null)
		{
			Logger.instance = new Logger();
		}
		return instance;
	}
	
	private Logger() throws FileNotFoundException
	{
		String date = getCurrentTime();
		//path of new log file: pwd(absolute path)\log\<date+time>.txt
		String logName = Paths.get(".").toAbsolutePath().normalize().toString() + "\\log\\" + date + ".txt";
		this.logWriter = new PrintWriter(new FileOutputStream(logName), true);
	}
	
	public void printLogMessage(String sender, String message, LogLevel level)
	{
		String date = getCurrentTime();
		String stringLevel = "";
		switch (level)
		{
		case NOTE:
			stringLevel = "NOTE";
			break;
		case IMPORTANT:
			stringLevel = "IMPORTANT";
			break;
		case ERROR:
			stringLevel = "ERROR";
			break;

		default:
			stringLevel = "NOTE";
			break;
		}	
		String newLogMessage = "<" + date + ">" + stringLevel + "\n" + sender + ": " + message + "\n"; 
		if (!logEnabled)
		{//logger not enabled -> print to console
			System.out.println(newLogMessage);
		}
		else
		{//logger enabled -> print to log file and console
			System.out.println(newLogMessage);
			logWriter.println(newLogMessage); 
		}	
	}
	
	public void printLogMessage(String sender, Exception e)
	{
		String date = getCurrentTime();
		String newLogMessage = "<" + date + ">CRITICAL\n" + sender + ":\n";
		if (!logEnabled)
		{//logger not enabled -> print to console
			System.out.println(newLogMessage);
			e.printStackTrace();
		}
		else
		{//logger enabled -> print to log file and console
			System.out.println(newLogMessage);
			e.printStackTrace();
			logWriter.println(newLogMessage);
			e.printStackTrace(logWriter);
		}
		
	}
	
	private String getCurrentTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

}
