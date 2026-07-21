package server;

import dao.DaoFactory;
import dto.ClientRequest;
import dto.ServerResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded socket server for the Gospel Music Catalogue.
 * Accepts multiple simultaneous client connections using ExecutorService.
 * @author D00276269
 */
public class Server {

    private static final int PORT = 5001;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;

    /**
     * Initialises the server socket and thread pool.
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newCachedThreadPool();
            running = true;
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server on port " + PORT, e);
        }
    }

    /**
     * Starts listening for client connections.
     * Each client is handled on a separate thread.
     */
    public void start() {
        System.out.println("Waiting for clients...");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Shuts down the server and releases all resources.
     */
    public void stop() {
        running = false;
        threadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        System.out.println("Server stopped.");
    }

    /**
     * Main entry point — starts the server.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}