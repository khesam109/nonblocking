package com.khesam.blocking.echo.server.impl;

import com.khesam.blocking.echo.server.EchoServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadBlockingEchoServer implements EchoServer {

    private static final int PORT = 8585;
    private static final int WORKER_THREAD_COUNT = 2;

    private static MultiThreadBlockingEchoServer INSTANCE = null;

    private final ExecutorService workerThreads;
    private final ServerSocket serverSocket;
    private final AtomicInteger clientId;

    private MultiThreadBlockingEchoServer() throws IOException {
        this.workerThreads = Executors.newFixedThreadPool(WORKER_THREAD_COUNT);
        this.serverSocket = new ServerSocket(PORT);
        this.clientId = new AtomicInteger(0);
    }

    public synchronized static MultiThreadBlockingEchoServer getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new MultiThreadBlockingEchoServer();
        }
        return INSTANCE;
    }

    @Override
    public void startServer() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();

                System.out.println("[" + Thread.currentThread().getName() + "]" + ": A client connected to echo server");

                workerThreads.submit(
                        new ClientHandler(
                                clientSocket, this.clientId.getAndIncrement()
                        )
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
