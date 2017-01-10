import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
/**
 * The Class Server.
 */
public class Server implements Runnable {

	/** The server socket - used for creating a socket for client connection. */
	//server socket that accepts tcp connections from clients
	private ServerSocket serverSocket;

	/** The socket for a client connection. */
	//the socket for the tcp connection with the client
	private Socket socket;

	/** The publisher. */
	//the publisher which will publish the tcp connection over udp broadcast requests
	private ServerPublisher publisher;

	/** The Tcp port the server listens on. */
	//the member which will hold the port for the client tcp connections
	private int TcpPort = 6000;

	//true when the server waits for a client connection. 
	/** true when the server waits for a client connection. 
	 * will become true when the server is fully initialized and false when a client connection was created*/
	//will become true when the server is fully initialized and false when a client connection was created 
	private boolean available = false;

	/** The logger. */
	private Logger logger;

	/** The client module that sends messages to its remote server. */
	//a client instance which will receive input from the server
	private Client client;

	/** The Constant className. */
	public static final String className = "Server";
	
	/** this is the value of the first readable char on the ascii table. */
	public static final int firstReadableChar = 33;
	
	/** how many readable chars for the random char replacement. */
	public static final int readableCharCount = 93;

	/**
	 * Instantiates a new server.
	 *
	 * @param client the client
	 */
	public Server(Client client)
	{
		try
		{
			//true -> print to log file, false -> print to console(for debugging proposes)
			this.logger = Logger.getLoggerInstance();
			createServerSocket();
			printLogMessage(className, "created server socket for clients on port " + this.TcpPort, LogLevel.IMPORTANT);
			this.publisher = new ServerPublisher(this.TcpPort);
			this.client = client;
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("could not get logger instance");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Thread publisherthread = new Thread(this.publisher);
		this.available = true;		//server is ready - make it available for the publisher to publish it
		publisherthread.start();	//start publishing the server
		printLogMessage(this.className, "started publishing the server", LogLevel.IMPORTANT);
		try 
		{
			while (!this.socket.isConnected())
			{//TODO what happens when the client is connected - the publisher should return the socket, should check if the client is connected
				this.socket = this.serverSocket.accept();	//wait for a client to connect				
			}
			this.client.StopSearching();
			this.client.setServerConnected(true);
			this.available = false;						//found a client, stop publishing the server
			printLogMessage(this.className, "server found a remote client", LogLevel.IMPORTANT);

			BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String clientInput, newString;
			int clientInputLength, replacedCharIndex;
			char randomChar;
			while (true)
			{
				clientInput = in.readLine();
				clientInputLength = clientInput.length();
				if (clientInputLength >= 1 && client.isConnected())
				{//rx-on tx-on
					//for safty, what happens when the remote client sends an empty string
					//TODO make sure the end of line "\n" is part of the string, if not then change to 0
					newString = replaceRandomChar(clientInput, clientInputLength);
					printLogMessage(this.className, "original client input - " + 
							clientInput + 
							" ,new string - " + newString, LogLevel.NOTE);
					this.client.sendMessage(newString);
				}
				else if (!client.isConnected())
				{//rx-on tx-off
					System.out.println(clientInput);
				}
			}
		} 
		catch (Exception e) {
			printLogMessage(className, e);
		}

	}

	/**
	 * Replace random char in the message received from the remote client.
	 *
	 * @param clientInput the remote client input
	 * @param clientInputLength the remote client input length
	 * @return the new "disturbed" message
	 */
	private String replaceRandomChar(String clientInput, int clientInputLength) 
	{
		String newString;
		int replacedCharIndex;//the index of the character that will be replace
		char randomChar;
		replacedCharIndex = new Random().nextInt(clientInputLength);//return a random number between 0 to clientInputLength - 1
		randomChar = (char)(new Random().nextInt(firstReadableChar) + readableCharCount);//the random character that will written instead one of the string chars

		if (replacedCharIndex == 0)
		{//will replace the 1st char
			newString = randomChar + clientInput.substring(1);
		}
		else if (replacedCharIndex == clientInputLength - 1)
		{//will replace the last char
			newString = clientInput.substring(0, clientInputLength - 1) + randomChar;
		}
		else
		{//will replace a char in the middle of the original string
			newString = clientInput.substring(0, replacedCharIndex) + 
					randomChar + 
					clientInput.substring(replacedCharIndex + 1, clientInputLength);						
		}
		return newString;
	}

	/**
	 * Creates the server socket - finds an available port .
	 */
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
			printLogMessage(className, "failed to find available port for tcp clinet connection", LogLevel.ERROR);
			//TODO stop program
		}
	}

	/**
	 * Checks if the server waits for remote client connection.
	 *
	 * @return 
	 */
	public boolean isServerAvailable()
	{
		return this.available;
	}

	/**
	 * Gets the tcp port.
	 *
	 * @return the tcp port
	 */
	public int getTcpPort()
	{
		return this.TcpPort;
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
