import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.Random;

/**
 * The Class ServerPublisher.
 */
public class ServerPublisher implements Runnable {

	/** The logger. */
	private Logger logger;			//the logger

	/** The socket. */
	private DatagramSocket socket;	//the UDP datagram which will be transmitted until the server found a client 

	/** The udp port. */
	public static int UDP_PORT = 6000;

	/** The broadcast host. */
	public static String BROADCAST_HOST = "0.0.0.0";

	/** The class name. */
	private static String className = "ServerPublisher";

	/** The request packet size. */
	public static final int requestPacketSize = 20;

	/** The offer packet size. */
	public static final int offerPacketSize = 26;

	private int port;

	private String ip;
	
	private boolean keepPublishing;

	/**
	 * Instantiates a new server publisher.
	 *
	 * @param server the server
	 */
	public ServerPublisher(int serverPort, DatagramSocket socket)
	{
		this.socket = socket;
		this.port = serverPort;
		try {
			this.keepPublishing = true;
			this.logger = Logger.getLoggerInstance(); 			
			this.ip = InetAddress.getLocalHost().getHostAddress();
			printLogMessage(className, "Just get my PC Ip" + this.ip, LogLevel.NOTE);

		} catch (UnknownHostException e1) {
			printLogMessage(className, e1);
			printLogMessage(className, "Couldn't get my PC Ip", LogLevel.IMPORTANT);

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
		//allocate the byte array for request message
		byte[] requstPacket = new byte[requestPacketSize];
		InetAddress address = null;
		try
		{
			 address = InetAddress.getByName(BROADCAST_HOST);
		}
		catch (Exception e)
		{
			printLogMessage(this.className, e);
		}
		DatagramPacket requestDP = new DatagramPacket(requstPacket, requstPacket.length, address, UDP_PORT);
		printLogMessage(className, "allocate UDP broadcast datagram to get the request", LogLevel.IMPORTANT);

		while (keepPublishing)
		{
			try
			{
				this.socket.receive (requestDP);
//				if (!requestIsValid(requestDP.getData()))
//				{
//					continue;
//				}
				byte[] offerPacket = new byte[this.offerPacketSize];
				copyFirst20Bytes(requestDP.getData(), offerPacket);
				addIpToPacket(offerPacket);
				addPortToPacket(offerPacket);
				DatagramPacket offerDP = new DatagramPacket(offerPacket, offerPacket.length, requestDP.getAddress(), UDP_PORT);

				if (!requestDP.getAddress().getHostAddress().equals(this.ip))
				{
					socket.send(offerDP);
					
					printLogMessage(className, "Received request massage from: " + requestDP.getAddress () + ":" +
							requestDP.getPort (), LogLevel.IMPORTANT);
					
					printLogMessage(className, "Send offer massage to: " + requestDP.getAddress () + ":" +
							requestDP.getPort (), LogLevel.IMPORTANT);
				}
			}
			catch (SocketTimeoutException e){
				printLogMessage(this.className, "Recieve socket timeout", LogLevel.NOTE);
			}
			catch (IOException ie)
			{
				ie.printStackTrace();
				printLogMessage(className, "Problem with sending offer massage to: " + requestDP.getAddress () + ":" +
						requestDP.getPort (), LogLevel.IMPORTANT);
			}
			
		}

	}
	
	private String parseAddress(DatagramPacket offerDP) {
		String str = String.format("%d.%d.%d.%d", (offerDP.getData()[20] & 0xFFL), (offerDP.getData()[21] & 0xFFL), (offerDP.getData()[22] & 0xFFL), (offerDP.getData()[23] & 0xFFL)); 
		return str;
	}

	private boolean sameIP(DatagramPacket offerDP) {
		String str = parseAddress(offerDP);
		if (this.ip.equals(str))
			return true;
		else
			return false;
	}

	private boolean requestIsValid(byte[] r) {
		//String data = r.toString();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < r.length; i++)
		{
			str.append((char)r[i]);
		}
		String data = str.toString(); 
		if (data.contains("Networking17") && data.length() == requestPacketSize)
			return true;
		else
			return false;
	}

	private void copyFirst20Bytes(byte[] requestData, byte[] offerBuffer) {
		for (int i=0; i<20; i++){
			offerBuffer[i]=requestData[i];
		}

	}


	private void addPortToPacket(byte[] offerBuffer) {
		short s = (short)this.port;
		byte b1 = (byte)s;
		byte b2 = (byte)((s >> 8) & 0xff);
		offerBuffer[24] = b2;
		offerBuffer[25] = b1;
	}


	private void addIpToPacket(byte[] offerBuffer) {
		String[] parts = this.ip.split("\\.");
		for (int j = 0; j < 4; j++)
		{
			short total = 0;
			for(int i = 0; i < parts[j].length(); i++)
			{
				total = (short) (total * 10);
				char c = parts[j].charAt(i);
				total =  (short) (total + (byte)(c - '0'));
			}			
			offerBuffer[20 + j] = (byte)total;				
			
		}
	}
	
	public void stopPublishing()
	{
		this.keepPublishing = false;
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