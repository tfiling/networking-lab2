import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




public class Server implements Runnable {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private Logger logger;
	private int port = 6000;
	
	public Server(Logger logger)
	{
		this.logger = logger;
	}

	@Override
	public void run() {
		//TODO
		
	}
		
	private void createServerSocket()
	{
		while (port <= 7000)
		{//try openning a TCP socket starting from port 6000 up to 7000 until you will find an available port
			try {
				this.serverSocket = new ServerSocket(port);
				break;//found port and created a socket, stop trying other ports
			} catch (IOException e) {
				this.port++;//failed to open socket with the current port, try with the next one
			}
		}
		if (port > 7000)
		{
			this.serverSocket = null;
		}
	}

	public boolean isServerAvailable()
	{
		//TODO
		return false;
	}
}
