import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * The Class Client.
 */
public class Client implements Runnable {

	/** The socket that is used for sending connection requests and offers. */
	private DatagramSocket requestSocket;	//the UDP datagram which will be transmitted until the server found a client 

	/** The udp port for request and offer messages. */
	public static int UDP_PORT = 6000;
	
	/** The broadcast host. */
	public static String BROADCAST_HOST = "255.255.255.255";
	
	/** The request packet size. */
	public static final int requestPacketSize = 20;
	
	/** The offer packet size. */
	public static final int offerPacketSize = 26;

	/** indicates whether the client is connected to a server. */
	private boolean isConnected;
	
	/** indication weather the client should keep searching for a server. */
	//false when the server found client or when the client found a server, otherwise true
	private boolean keepRunning;
	
	/** indication whenever the server are listening to the client. */
	private boolean isServerConnected;

	/** The logger. */
	private Logger logger;

	/** The server socket. */
	private Socket serverSocket;
	
	/** The publishing udp socket. */
	//private DatagramSocket publishingUdpSocket;  //we didnt used it.....
	
	/** sends the message to the server. */
	private DataOutputStream out;

	/** The Constant className. */
	public static final String className = "Client";
	
	private String myIp;

	/**
	 * Instantiates a new client.
	 * @param socket 
	 */
	public Client(DatagramSocket socket)
	{
		this.isConnected = false;
		this.keepRunning = true;
		this.isServerConnected = false;
		this.requestSocket = socket;
		try {
			this.logger = Logger.getLoggerInstance(); 			
			this.myIp = InetAddress.getLocalHost().getHostAddress();
			printLogMessage(className, "Just get my PC Ip" + this.myIp, LogLevel.IMPORTANT);
			
		} catch (UnknownHostException e1) {
			printLogMessage(this.className, e1);
			printLogMessage(className, "Couldn't get my PC Ip", LogLevel.IMPORTANT);
		}
		catch(FileNotFoundException e)
		{//could not get a logger instance, the operation will not be fully logged
			e.printStackTrace();
			System.out.println("could not get logger instance");
		}	
	}

	Scanner sc = new Scanner(System.in);

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try 
		{
			//the address instance of the broadcast address
			InetAddress address = InetAddress.getByName(BROADCAST_HOST);
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
			
			while(this.keepRunning)
			{

				this.requestSocket.send(requestDP);	//send connection request
				printLogMessage(className, "sent request message", LogLevel.NOTE);

				//allocate packet for connection offer message
				byte[] offerPacket = new byte[offerPacketSize];
				DatagramPacket offerDP = new DatagramPacket(offerPacket, offerPacket.length);

				try
				{//wait for offer message
					do {
						this.requestSocket.receive(offerDP);
						printLogMessage(className, "received offer from " + offerDP.getAddress().getHostAddress(), LogLevel.IMPORTANT);
					}
					while (offerDP.getLength() == 20);
						
				}
				catch (SocketTimeoutException e)
				{//connection offer message was not received within 1 sec
					printLogMessage(this.className, "did not receive any offer", LogLevel.NOTE);
					continue;//send another connection request message
				}
				//checks its another PC	

				printLogMessage(className, "found an offer from remote server", LogLevel.IMPORTANT);
					int port = parsePort(offerDP);
					String serverAddress = parseAddress(offerDP);
					
					if (port >= 6000 && port <= 7000 && serverAddress != null && this.keepRunning)
					{//if connection offer is valid - create the socket and writing channel
						this.serverSocket = new Socket(serverAddress, port);
						this.out = new DataOutputStream(this.serverSocket.getOutputStream());	
					}
					if (this.serverSocket != null && this.out != null)
					{//failed to create the server connection try sending another request
						this.keepRunning = false;
						this.isConnected = true;
						printLogMessage(this.className, "Found a server, stop looking for one. server address: " + this.serverSocket.getInetAddress().toString(), LogLevel.IMPORTANT);
						continue;
					}
				
			}
			while (!this.isServerConnected)
			{
				printLogMessage(this.className, "client module connected to a remote server\n you can send messages via console", LogLevel.IMPORTANT);
				//String inputUser = sc.nextLine();
				
				this.sendMessage(JOptionPane.showInputDialog("Enter input: "));
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
		String str = String.format("%d.%d.%d.%d", (offerDP.getData()[20] & 0xFFL), (offerDP.getData()[21] & 0xFFL), (offerDP.getData()[22] & 0xFFL), (offerDP.getData()[23] & 0xFFL)); 
		return str;
	}
	/**
	 * Parses the port.
	 *
	 * @param offerDP the offer message
	 * @return the int
	 */
	private int parsePort(DatagramPacket offerDP) {
		byte[] arr = {offerDP.getData()[24], offerDP.getData()[25] };
		ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
		short num = wrapped.getShort(); 
		return num;
	}



	/**
	 * Send message.
	 *
	 * @param message the message that will be sent to the server
	 */
	public void sendMessage(String message)
	{
		if (!this.isConnected)
		{
			System.out.println(message);
			printLogMessage(this.className, "printed message: " + message + " to console since not connected to remote server", LogLevel.NOTE);
			return;
		}
		else if (this.out == null)
		{
			printLogMessage(this.className, "sendMessage was invoked but the client has not output stream via socket", LogLevel.ERROR);
			return;
		}
		try {
			this.out.writeBytes(message + '\n');
		} catch (IOException e) {
			printLogMessage(this.className, e);
			printLogMessage(className, "failed writing a message", LogLevel.ERROR);
		}
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
	
	public void setServerConnected(boolean isServerConnected)
	{
		this.isServerConnected = isServerConnected;
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
	
	public boolean isFromMySelf(InetAddress address) {
	    String rawAddress = address.toString();
	    int idx = rawAddress.indexOf('/');
	    rawAddress = rawAddress.substring(idx + 1, rawAddress.length());
	    if(rawAddress.equals(this.myIp)) return true;
	    else return false;
	}


}