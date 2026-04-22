package org.project;

import org.project.controller.ProductController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductServerApplication {
    private static int PORT = 8081;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(50);
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Product Service is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                new Thread(new ProductController(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}