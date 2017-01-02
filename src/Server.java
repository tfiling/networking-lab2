import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;




public class Server implements Runnable {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private ServerPublisher publisher;
	private int port = 6000;
	private boolean available = true;
	
	private Logger logger;
	
	public Server()
	{
		try
		{
			Logger.logEnabled = false;
			this.logger = Logger.getLoggerInstance();
			createServerSocket();
			this.publisher = new ServerPublisher(this, this.port);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("could not get logger instance");
		}
	}

	@Override
	public void run() {
		Thread publisherthread = new Thread(this.publisher);
		publisherthread.start();
		try {
			Thread.sleep(10000);//sleep for 10 secs for publisher testing
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			//TODO stop program
		}
	}

	public boolean isServerAvailable()
	{
		//TODO
		return false;
	}
	
	public int getPort()
	{
		return this.port;
	}
}
