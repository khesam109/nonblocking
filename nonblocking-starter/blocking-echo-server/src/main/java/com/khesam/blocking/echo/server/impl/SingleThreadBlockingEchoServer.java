package com.khesam.blocking.echo.server.impl;

import com.khesam.blocking.echo.server.EchoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadBlockingEchoServer implements EchoServer {

    private static final int PORT = 8585;

    private static SingleThreadBlockingEchoServer INSTANCE;
    private final ServerSocket serverSocket;

    private SingleThreadBlockingEchoServer() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
    }

    public synchronized static SingleThreadBlockingEchoServer getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new SingleThreadBlockingEchoServer();
        }
        return INSTANCE;
    }

    @Override
    public void startServer() {
        while (true) {
            try (
                    Socket clientSocket = this.serverSocket.accept();
                    BufferedReader incomingBuffer = new BufferedReader(
                            new InputStreamReader(
                                    clientSocket.getInputStream()
                            )
                    );
                    PrintWriter outgoingBuffer = new PrintWriter(
                            clientSocket.getOutputStream(), true
                    );
            ) {
                System.out.println("[" + Thread.currentThread().getName() + "]" + ": A client connected to echo server");
                String line;
                while ((line = incomingBuffer.readLine()) != null) {
                    System.out.println("[" + Thread.currentThread().getName() + "]" + ": message received from client: " + line + "\n\tEchoing message...");

                    outgoingBuffer.println(line);

                    if (line.equalsIgnoreCase("fin")) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
