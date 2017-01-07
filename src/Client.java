import java.io.FileNotFoundException;
import java.net.*;


public class Client implements Runnable {
	
	private boolean isConnected;
	private boolean keepRunning = true;
	
	private Logger logger;
	
	private Socket serverSocket;
	private DatagramSocket publishingUdpSocket;
	
	public static final String className = "Client";
	
	public Client()
	{
		this.isConnected = false;
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
	
	

	@Override
	public void run() {
		try {
			this.publishingUdpSocket = new DatagramSocket(ServerPublisher.UDP_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendMessage(String message)
	{//TODO send message to the server you are connected to or to the console
		if (!this.isConnected)
		{
			printLogMessage(this.className, "sendMessage was invoked but the client is not connected to the server", LogLevel.ERROR);
			return;
		}
		
	}
	
	public boolean isConnected()
	{//TODO is this needed?
		return this.isConnected;
	}


	public void StopSearching() {
		this.keepRunning = false;
		
	}
	
	private void printLogMessage(String sender, String message, LogLevel level)
	{
		if (this.logger != null)
		{
			this.logger.printLogMessage(sender, message, level);
		}
	}
	
	private void printLogMessage(String sender, Exception e)
	{
		if (this.logger != null)
		{
			this.logger.printLogMessage(sender, e);
		}
	}


}
