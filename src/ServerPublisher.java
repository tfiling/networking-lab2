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
	public ServerPublisher(int serverPort)
	{
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
		try
		{
			this.socket = new DatagramSocket(this.port);
			//this.socket.connect(UDP_PORT);
		}
		catch( Exception ex )
		{
			printLogMessage(this.className, ex);
			printLogMessage(className, "Problem creating socket on port: " + this.port, LogLevel.ERROR);

		}
		//allocate the byte array for request message
		byte[] requstPacket = new byte[requestPacketSize];
		try
		{
			InetAddress address = InetAddress.getByName(BROADCAST_HOST);
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
				DatagramPacket offerDP = new DatagramPacket(offerPacket, offerPacket.length);

				printLogMessage(className, "Send offer massage to: " + requestDP.getAddress () + ":" +
						requestDP.getPort (), LogLevel.IMPORTANT);
				socket.send (offerDP);
			}
			catch (IOException ie)
			{
				ie.printStackTrace();
				printLogMessage(className, "Problem with sending offer massage to: " + requestDP.getAddress () + ":" +
						requestDP.getPort (), LogLevel.IMPORTANT);
			}
		}

	}

	private boolean requestIsValid(byte[] r) {
		String data = r.toString();
		if (data.contains("Networking17"))
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
		byte[] port = new byte[2];
		for (int i = 0; i < 2; i++) {
			port[i] = (byte)(this.port >>> (i * 8));
		}
		offerBuffer[24] = port[0];
		offerBuffer[25] = port[1];


	}


	private void addIpToPacket(byte[] offerBuffer) {
		byte[] Ip = new byte[4];
		Ip = this.ip.getBytes(StandardCharsets.UTF_8);
		offerBuffer[20] = Ip[0];
		offerBuffer[21] = Ip[1];
		offerBuffer[22] = Ip[2];
		offerBuffer[23] = Ip[3];
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