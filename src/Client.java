import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;


/**
 * The Class Client.
 */
public class Client implements Runnable {

	/** The socket that is used for sending connection requests and offers. */
	private DatagramSocket requestSocket;	//the UDP datagram which will be transmitted until the server found a client 

	/** The udp port for request and offer messages. */
	public static int UDP_PORT = 6000;
	
	/** The broadcast host. */
	public static String BROADCAST_HOST = "255.255.255.255";//TODO find the correct address for broadcast publishing
	
	/** The request packet size. */
	public static final int requestPacketSize = 20;
	
	/** The offer packet size. */
	public static final int offerPacketSize = 26;

	/** indicates wheather the client is connected to a server. */
	private boolean isConnected;
	
	/** indication weather the client should keep searching for a server. */
	//false when the server found client or when the client found a server, otherwise true
	private boolean keepRunning;

	/** The logger. */
	private Logger logger;

	/** The server socket. */
	private Socket serverSocket;
	
	/** The publishing udp socket. */
	private DatagramSocket publishingUdpSocket;
	
	/** sends the message to the server. */
	private PrintWriter out;

	/** The Constant className. */
	public static final String className = "Client";

	/**
	 * Instantiates a new client.
	 */
	public Client()
	{
		this.isConnected = false;
		this.keepRunning = true;
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

		try 
		{
			//the address instance of the broadcast address
			InetAddress address = InetAddress.getByName(ServerPublisher.BROADCAST_HOST);
			String request = "Networking17----";			
			byte[] temp = request.getBytes();
			//allocate the byte array which will be sent as the udp datagram's data
			byte[] requestBytes = new byte[requestPacketSize];
			//copy the prefix pf the data
			for (int i = 0; i < 16; i++)
			{
				requestBytes[i] = temp[i];
			}

			Integer randomInteger = new Integer(new Random().nextInt());
			//copy the random integer to the end of the udp datagram's data
			byte[] randomIntegerBytes = ByteBuffer.allocate(4).putInt(randomInteger).array(); 
			requestBytes[16] = randomIntegerBytes[0];
			requestBytes[17] = randomIntegerBytes[1];
			requestBytes[18] = randomIntegerBytes[2];
			requestBytes[19] = randomIntegerBytes[3];

			//assembly all the above properties to udp datagram 
			DatagramPacket requestDP = new DatagramPacket(requestBytes, requestBytes.length, address, ServerPublisher.UDP_PORT);
			printLogMessage(className, "created UDP broadcast datagram", LogLevel.NOTE);
			//create socket for sending the datagram
			this.requestSocket = new DatagramSocket(ServerPublisher.UDP_PORT);
			this.requestSocket.setBroadcast(true);
			this.requestSocket.setSoTimeout(1000);	//set the timeout for 1 sec
			printLogMessage(className, "created socket for sending request message", LogLevel.NOTE);

			while(this.keepRunning)
			{

				this.requestSocket.send(requestDP);	//send connection request
				printLogMessage(className, "sent request message", LogLevel.NOTE);

				//allocate packet for connection offer message
				byte[] offerPacket = new byte[offerPacketSize];
				DatagramPacket offerDP = new DatagramPacket(offerPacket, offerPacket.length);

				try
				{//wait for offer message
					this.requestSocket.receive(offerDP);					
				}
				catch (SocketTimeoutException e)
				{//connection offer message was not received within 1 sec
					printLogMessage(this.className, "did not receive any offer", LogLevel.NOTE);
					continue;//send another connection request message
				}
				
				int port = parsePort(offerDP);
				String serverAddress = parseAddress(offerDP);
				if (port >= 6000 && port <= 7000 && serverAddress != null)
				{//if connection offer is valid - create the socket and writing channel
					this.serverSocket = new Socket(serverAddress, port);
					this.out = new PrintWriter(this.serverSocket.getOutputStream());
				}
				if (this.serverSocket != null && this.out != null)
				{//failed to create the server connection try sending another request
					this.keepRunning = false;
					this.isConnected = true;
					continue;
				}
			}

		} catch (UnknownHostException e) 
		{
			printLogMessage(className, e);
		} catch (IOException e)
		{
			printLogMessage(className, e);
		}		
	}

	/**
	 * Parses the address.
	 *
	 * @param offerDP the offer message
	 * @return the string
	 */
	private String parseAddress(DatagramPacket offerDP) {
		// TODO when server publisher ready
		return null;
	}



	/**
	 * Parses the port.
	 *
	 * @param offerDP the offer message
	 * @return the int
	 */
	private int parsePort(DatagramPacket offerDP) {
		// TODO when server publisher ready
		return -1;
	}



	/**
	 * Send message.
	 *
	 * @param message the message that will be sent to the server
	 */
	public void sendMessage(String message)
	{//TODO send message to the server you are connected to or to the console
		if (!this.isConnected)
		{
			printLogMessage(this.className, "sendMessage was invoked but the client is not connected to the server", LogLevel.ERROR);
			return;
		}
		else if (this.out == null)
		{
			printLogMessage(this.className, "sendMessage was invoked but the client has not output stream via socket", LogLevel.ERROR);
			return;
		}
		this.out.print(message);
	}

	/**
	 * Checks if connected to server.
	 *
	 * @return true, if is connected to server
	 */
	public boolean isConnected()
	{
		return this.isConnected;
	}


	/**
	 * Stop searching for a server.
	 */
	public void StopSearching() {
		this.keepRunning = false;

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
