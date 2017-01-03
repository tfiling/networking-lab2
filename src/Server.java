import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;

import Logger.LogLovel;




public class Server implements Runnable {
	
	//server socket that accepts tcp connections from clients
	private ServerSocket serverSocket;
	
	//the socket for the tcp connection with the client
	private Socket socket;
	
	//the publisher which will publish the tcp connection over udp broadcast requests
	private ServerPublisher publisher;
	
	//the member which will hold the port for the client tcp connections
	private int TcpPort = 6000;
	
	//true when the server waits for a client connection. 
	//will become true when the server is fully initialized and false when a client connection was created 
	private boolean available = false;
	
	private Logger logger;
	
	private static String className = "Server"; 
	
	public Server()
	{
		try
		{
			//true -> print to log file, false -> print to console(for debugging proposes)
			Logger.logEnabled = false;//TODO change to true for real world operation
			this.logger = Logger.getLoggerInstance();
			createServerSocket();
			this.logger.printLogMessage(className, "created server socket for clients on port " + this.TcpPort, LogLovel.IMPORTANT);
			this.publisher = new ServerPublisher(this);
		}
		catch(FileNotFoundException e)
		{//TODO
			this.logger.printLogMessage(className, e);
		}
	}

	@Override
	public void run() {
		Thread publisherthread = new Thread(this.publisher);
		publisherthread.start();
		try 
		{
			Thread.sleep(10000);//sleep for 10 secs for publisher testing
			return;
		} catch (InterruptedException e) {
			this.logger.printLogMessage(className, e);
		}
	}
		
	private void createServerSocket()
	{
		while (TcpPort <= 7000)
		{//try openning a TCP socket starting from port 6000 up to 7000 until you will find an available port
			try {
				this.serverSocket = new ServerSocket(TcpPort);
				break;//found port and created a socket, stop trying other ports
			} catch (IOException e) {
				this.TcpPort++;//failed to open socket with the current port, try with the next one
			}
		}
		if (TcpPort > 7000)
		{
			this.serverSocket = null;
			this.logger.printLogMessage(className, "failed to find available port for tcp clinet connection", LogLovel);
			//TODO stop program
		}
	}

	public boolean isServerAvailable()
	{
		return this.available;
	}
	
	public int getTcpPort()
	{
		return this.TcpPort;
	}
}
