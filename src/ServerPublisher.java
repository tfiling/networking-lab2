import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class ServerPublisher implements Runnable {
	
	private Server server;
	private int publisedPort;
	private Logger logger;
	private DatagramSocket socket;
	
	public static int UDP_PORT = 6000;
	public static String BROADCAST_HOST = "132.73.209.250";
	
	public ServerPublisher(Server server, int port)
	{
		this.server = server;
		this.publisedPort = port;
		try
		{
			this.logger = Logger.getLoggerInstance(); 			
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("could not get logger instance");
		}
	}
	

	@Override
	public void run() {
		
		InetAddress address;
		try {
			address = InetAddress.getByName(ServerPublisher.BROADCAST_HOST);
			String requiredString = "Networking17";
			String port = Integer.toString(this.server.getPort());
			String request = port + requiredString;
			byte[] temp = request.getBytes();
			byte[] requestBytes = new byte[20];
			for (int i = 0; i < 16; i++)
			{
				requestBytes[i] = temp[i];
			}
			Integer randomInteger = new Integer(new Random().nextInt());
			byte[] randomIntegerBytes = ByteBuffer.allocate(4).putInt(randomInteger).array(); 
			requestBytes[16] = randomIntegerBytes[0];
			requestBytes[17] = randomIntegerBytes[1];
			requestBytes[18] = randomIntegerBytes[2];
			requestBytes[19] = randomIntegerBytes[3];
			
			DatagramPacket dp = new DatagramPacket(requestBytes, requestBytes.length, address, ServerPublisher.UDP_PORT);
			this.socket = new DatagramSocket(ServerPublisher.UDP_PORT);
			
			for (int i = 0; i < 8; i++)
			{
				try
				{
					this.socket.send(dp);
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{//TODO
					e.printStackTrace();
				}
			}
			
		} catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e)
		{//TODO stop program or something
			e.printStackTrace();
		} catch (IOException e)
		{//TODO stop program or something
			e.printStackTrace();
		}
		


	}

}
