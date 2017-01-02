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
		String logName = Paths.get(".").toAbsolutePath().normalize().toString() + "\\log\\" + date + ".txt";
		this.logWriter = new PrintWriter(new FileOutputStream(logName), true);
	}
	
	public void printLogMessage(String message)
	{
		String date = getCurrentTime();
		String newLogMessage = date + ": " + message; 
		if (!logEnabled)
		{
			System.out.println(newLogMessage);
		}
		else
		{			
			logWriter.println(newLogMessage); 
		}
		
	}
	
	private String getCurrentTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

}
