import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	
	public static int UDP_PORT = 6000;
	public static DatagramSocket socket;	//the UDP datagram which will be transmitted until the server found a client 

	public static void main(String[] args) {
		
		try
		{
			Logger logger = Logger.getLoggerInstance();
			logger.logEnabled = false;
			
			try {
				socket = new DatagramSocket(UDP_PORT);
				socket.setBroadcast(true);
				socket.setSoTimeout(1000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Client client = new Client(socket);
			//Thread clientThread = new Thread(client);
			//clientThread.start();//start looking for a remote server to connect to 
			
			Server server = new Server(client, socket);
			Thread serverThread = new Thread(server);
			serverThread.start();//the server will create a publisher and wait for a client connection
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("log file creation failed, exiting");
			return;
		}
		

	}

}
