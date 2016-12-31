import java.io.FileNotFoundException;


public class Main {

	public static void main(String[] args) {
		Client client = new Client();
		Thread clientThread = new Thread(client);
		
		Server server = new Server();
		Thread serverThread = new Thread(server);
		
		try
		{
			Logger logger = new Logger(false);			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("log file creation failed, exiting");
			return;
		}
		

	}

}
