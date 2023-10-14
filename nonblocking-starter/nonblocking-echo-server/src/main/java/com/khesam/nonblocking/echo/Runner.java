package com.khesam.nonblocking.echo;

import com.khesam.nonblocking.echo.server.NonblockingEchoServer;

import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws IOException {
        System.out.println("[" + Thread.currentThread().getName() + "]" +  ": Start single-thread nonblocking echo server");
        NonblockingEchoServer.getInstance().startServer();
    }
}
