import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
	public static String BROADCAST_HOST = "255.255.255.255";//TODO find the correct address for broadcast publishing

	/** The class name. */
	private static String className = "ServerPublisher";

	/** The request packet size. */
	public static final int requestPacketSize = 20;

	/** The offer packet size. */
	public static final int offerPacketSize = 26;

	private int port;

	private String ip;

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
			this.ip = InetAddress.getLocalHost().getHostAddress();
			printLogMessage(className, "Just get my PC Ip" + this.ip, LogLevel.NOTE);

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			printLogMessage(className, "Couldn't get my PC Ip", LogLevel.IMPORTANT);

		}


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
		printLogMessage(className, "allocate UDP broadcast datagram to get the request", LogLevel.NOTE);

		while (true)
		{
			try
			{
				this.socket.receive (requestDP);
				printLogMessage(className, "Received request massage from: " + requestDP.getAddress () + ":" +
						requestDP.getPort (), LogLevel.IMPORTANT);
				if (!requestIsValid(requestDP.getData()))
				{
					continue;
				}
				byte[] offerPacket = new byte[this.offerPacketSize];
				copyFirst20Bytes(requestDP.getData(), offerPacket);
				addIpToPacket(offerPacket);
				addPortToPacket(offerPacket);
				DatagramPacket offerDP = new DatagramPacket(offerPacket, offerPacket.length, address, UDP_PORT);

				printLogMessage(className, "Send offer massage to: " + requestDP.getAddress () + ":" +
						requestDP.getPort (), LogLevel.IMPORTANT);
				if (!sameIP(offerDP))
				{
					socket.send(offerDP);
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
		byte[] bytes = {offerDP.getData()[20], offerDP.getData()[21], offerDP.getData()[22], offerDP.getData()[23]};
		String str;
		str = bytes[0] + "." + bytes[1] + "." + bytes[2] + "." + bytes[3]; 
		return str;
	}

	private boolean sameIP(DatagramPacket offerDP) {
		String str = parseAddress(offerDP);
		if (this.ip.equals(str))
		return false;
		else
			return true;
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
//		short A = (short) (this.port / 256);
//		short B = (short) (this.port % 256);
		offerBuffer[24] = b2;
		offerBuffer[25] = b1;


	}


	private void addIpToPacket(byte[] offerBuffer) {
		String[] parts = this.ip.split("\\.");
		
		offerBuffer[20] = parts[0];				
		offerBuffer[21] =  Byte.valueOf(parts[1]);
		offerBuffer[22] =  Byte.valueOf(parts[2]);
		offerBuffer[23] =  Byte.valueOf(parts[3]);
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