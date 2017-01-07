import java.io.FileNotFoundException;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		try
		{
			Logger logger = Logger.getLoggerInstance();
			logger.logEnabled = false;
			
			Client client = new Client();
			Thread clientThread = new Thread(client);
			clientThread.start();//start looking for a remote server to connect to 
			
			Server server = new Server(client);
			Thread serverThread = new Thread(server);
			//serverThread.start();//the server will create a publisher and wait for a client connection
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("log file creation failed, exiting");
			return;
		}
		

	}

}
