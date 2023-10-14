package com.khesam.blocking.echo;

import com.khesam.blocking.echo.server.impl.MultiThreadBlockingEchoServer;
import com.khesam.blocking.echo.server.impl.SingleThreadBlockingEchoServer;

import java.io.IOException;

public class Runner {


    public static void main(String[] args) throws IOException {
//        if (args == null || args.length == 0 || !args[0].equalsIgnoreCase("m")) {
//            runSingleThreadServer();
//        } else {
//            runMultiThreadServer();
//        }
        runMultiThreadServer();
    }

    private static void runSingleThreadServer() throws IOException {
        System.out.println("[" + Thread.currentThread().getName() + "]" +  ": Start single-thread blocking echo server");

        SingleThreadBlockingEchoServer.getInstance().startServer();
    }

    private static void runMultiThreadServer() throws IOException {
        System.out.println("[" + Thread.currentThread().getName() + "]" +  ": Start multi-thread blocking echo server");

        MultiThreadBlockingEchoServer.getInstance().startServer();
    }
}
