
public class Main {

	public static void main(String[] args) {
		Client client = new Client();
		Thread clientThread = new Thread(client);
		
		Server server = new Server();
		Thread serverThread = new Thread(server);
		
		Logger logger = new Logger();
		

	}

}
