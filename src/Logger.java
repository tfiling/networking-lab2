import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {
	
	public static boolean logEnabled;
	private PrintWriter logWriter;
	private static Logger instance = null;
	
	public Logger getLoggerInstance(boolean logEnabled) throws FileNotFoundException
	{
		if (instance == null)
		{
			Logger.instance = new Logger();
		}
		Logger.logEnabled = logEnabled;
		return instance;
	}
	
	private Logger() throws FileNotFoundException
	{
		String date = getCurrentTime();
		String logName = Paths.get(".").toAbsolutePath().normalize().toString() + "\\" + date + ".txt";
		this.logWriter = new PrintWriter(new FileOutputStream(logName), true);
	}
	
	public void printLogMessage(String message)
	{
		if (!logEnabled)
		{
			return;
		}
		String date = getCurrentTime();
		logWriter.println(date + ": " + message); 
	}
	
	private String getCurrentTime()
	{
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}

}
