import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class Logger.
 */
public class Logger {
	
	/** The log enabled - if false will not print to file. */
	public static boolean logEnabled = false;
	
	/** The log writer. */
	private PrintWriter logWriter;
	
	/** The singleton instance. */
	private static Logger instance = null;
	
	
	/**
	 * Gets the logger singleton instance.
	 *
	 * @param logEnabled the log enabled
	 * @return the logger instance
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Logger getLoggerInstance(boolean logEnabled) throws FileNotFoundException
	{
		if (instance == null)
		{
			Logger.instance = new Logger();
		}
		Logger.logEnabled = logEnabled;
		return instance;
	}
	
	/**
	 * Gets the logger singleton instance.
	 *
	 * @return the logger instance
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Logger getLoggerInstance() throws FileNotFoundException
	{
		if (instance == null)
		{
			Logger.instance = new Logger();
		}
		return instance;
	}
	
	/**
	 * Instantiates a new logger.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	private Logger() throws FileNotFoundException
	{
		String date = getCurrentTime();
		//path of new log file: pwd(absolute path)\log\<date+time>.txt
		String logName = Paths.get(".").toAbsolutePath().normalize().toString() + "\\" + date + ".txt";
		this.logWriter = new PrintWriter(new FileOutputStream(logName), true);
	}
	
	/**
	 * Prints the log message.
	 *
	 * @param sender the sender
	 * @param message the message
	 * @param level the level
	 */
	public synchronized void printLogMessage(String sender, String message, LogLevel level)
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
	
	/**
	 * Prints the log message.
	 *
	 * @param sender the sender
	 * @param e the e
	 */
	public synchronized void printLogMessage(String sender, Exception e)
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
	
	/**
	 * Gets the current time by yyyy-MM-dd_HH-mm-ss format.
	 *
	 * @return the current time
	 */
	private synchronized String getCurrentTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

}
