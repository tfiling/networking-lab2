import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * The Class ServerPublisher.
 */
public class ServerPublisher implements Runnable {

	/** The server. */
	private Server server;			//the server which is being published	
	
	/** The logger. */
	private Logger logger;			//the logger
	
	/** The socket. */
	private DatagramSocket socket;	//the UDP datagram which will be transmitted until the server found a client 

	/** The udp port. */
	public static int UDP_PORT = 6000;
	
	/** The broadcast host. */
	public static String BROADCAST_HOST = "132.72.255.255";//TODO find the correct address for broadcast publishing

	/** The class name. */
	private static String className = "ServerPublisher";

	/**
	 * Instantiates a new server publisher.
	 *
	 * @param server the server
	 */
	public ServerPublisher(Server server)
	{
		this.server = server;
		try
		{
			this.logger = Logger.getLoggerInstance(); 			
		}
		catch(FileNotFoundException e)
		{//could not get a logger instance, the operation will not be fully logged
			e.printStackTrace();
			System.out.println("could not get logger instance");
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//TODO
		System.out.println();

	}

	/**
	 * Prints the log message.
	 *
	 * @param sender the sender
	 * @param message the message
	 * @param level the level
	 */
	private void printLogMessage(String sender, String message, LogLevel level)
	{
		if (this.logger != null)
		{
			this.logger.printLogMessage(sender, message, level);
		}
	}

	/**
	 * Prints the log message.
	 *
	 * @param sender the sender
	 * @param e the e
	 */
	private void printLogMessage(String sender, Exception e)
	{
		if (this.logger != null)
		{
			this.logger.printLogMessage(sender, e);
		}
	}


}
