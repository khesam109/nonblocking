package com.khesam.blocking.echo.server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final String clientName;

    public ClientHandler(Socket clientSocket, int id) throws IOException {
        this.clientSocket = clientSocket;
        this.clientName = "client-" + id;
    }

    @Override
    public void run() {
        String line;

        try (
                BufferedReader incomingBuffer =   new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()
                        )
                );
                PrintWriter outgoingBuffer = new PrintWriter(
                        clientSocket.getOutputStream(), true
                );
        ) {
            while ((line = incomingBuffer.readLine()) != null) {
                System.out.println("[" + Thread.currentThread().getName() + "]" + ": message received from " + clientName + ": " + line + "\n\tEchoing message...");

                outgoingBuffer.println(line);

                if (line.equalsIgnoreCase("fin")) {
                    break;
                }
            }
            System.out.println("[" + Thread.currentThread().getName() + "]" + ": " + clientName + " disconnected from server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (this.clientSocket != null) {
                    this.clientSocket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
