package ch.unibe.ese.server.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ch.unibe.ese.share.requests.Request;

/**
 * Driver for the shopping list server
 * It just waits for connections and refers them to new ClientThreads which can be handled in parallel
 * Note: conflicts can occur if you do it in parallel
 * @author Stephan
 *
 */
public class ShoppingListServer {

	/**
	 * Configuration
	 */
	public static boolean WIPE_DATABSE_ON_STARTUP = true;
	private static int PORT = 1337;
	/**
	 * \Configuration 
	 */
	
	private ServerSocket serverSocket;
	private RequestHandler requestHandler;
	
	public ShoppingListServer() {
		this.requestHandler = new RequestHandler();
		this.initServerSocket();
		this.waitForConnection();
	}
	
	private void waitForConnection() {
		do {
			accept();
		} while (true);
	}


	private void accept() {
		try {
			// Get socket from client
			Socket socket = serverSocket.accept();
			// Start new thread to handle client request
			new ClientThread(socket,requestHandler).start();
		} catch (IOException e) {
			System.err.println("IOException in accept()");
			e.printStackTrace(System.err);
		}
	}

	private void initServerSocket() {
		try {
			this.serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Failed to init socket on port " + PORT);
		}
	}
	
	public static void main(String[] args) {
		new ShoppingListServer();
	}
	
}
