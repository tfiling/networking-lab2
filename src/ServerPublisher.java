import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class ServerPublisher implements Runnable {
	
	private Server server;			//the server which is being published	
	private Logger logger;			//the logger
	private DatagramSocket socket;	//the UDP datagram which will be transmitted until the server found a client 
	
	public static int UDP_PORT = 6000;
	public static String BROADCAST_HOST = "132.72.255.255";
	
	private static String className = "ServerPublisher";
	
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
	

	@Override
	public void run() {
		
		try 
		{
			//the address instance of the broadcast address
			InetAddress address = InetAddress.getByName(ServerPublisher.BROADCAST_HOST);
			//the required string in the udp datagram mentioned on the instructions
			String requiredString = "Networking17";
			String port = Integer.toString(this.server.getPort());
			//concatenation of port and required string
			String request = port + requiredString;
			byte[] temp = request.getBytes();
			//allocate the byte array which will be sent as the udp datagram's data
			byte[] requestBytes = new byte[20];
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
			DatagramPacket dp = new DatagramPacket(requestBytes, requestBytes.length, address, ServerPublisher.UDP_PORT);
			this.logger.printLogMessage(className, "created UDP broadcast datagram");
			//create socket for sending the datagram
			this.socket = new DatagramSocket(ServerPublisher.UDP_PORT);
			this.logger.printLogMessage(className, "created socket for sending request message");
			
			while(this.server.isServerAvailable())
			{
				try
				{//send UDP datagram request
					this.socket.send(dp);
					this.logger.printLogMessage(className, "sent request message");
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{//TODO
					this.logger.printLogMessage(className, e);
				}
			}
			
		} catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			this.logger.printLogMessage(className, e);
		} catch (SocketException e)
		{//TODO stop program or something
			this.logger.printLogMessage(className, e);
		} catch (IOException e)
		{//TODO stop program or something
			this.logger.printLogMessage(className, e);
		}
		


	}

}
