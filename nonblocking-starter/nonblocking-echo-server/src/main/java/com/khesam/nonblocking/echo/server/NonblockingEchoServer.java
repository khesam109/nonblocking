package com.khesam.nonblocking.echo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NonblockingEchoServer {

    private static final int PORT = 8585;

    private static NonblockingEchoServer INSTANCE = null;

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;


    private NonblockingEchoServer() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();

        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public synchronized static NonblockingEchoServer getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new NonblockingEchoServer();
        }

        return INSTANCE;
    }

    public void startServer() {
        while (true) {
            try {
                // wait for events
                int available = selector.select();

                // Nothing ready yet
                if (available == 0) {
                    continue;
                }

                // We got request ready to be processed.
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    processEvent(
                            iterator.next()
                    );
                    // We sure we don't handle it twice.
                    iterator.remove();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void processEvent(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            acceptNewClient();
        } else if (key.isReadable()) {
            readAndEchoClientMessage(key);
        }
    }

    private void acceptNewClient() throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(this.selector, SelectionKey.OP_READ);
        System.out.println("[" + Thread.currentThread().getName() + "]" + ": A client connected to echo server");
    }

    private void readAndEchoClientMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ConnectionHandler.getInstance().read(clientChannel);

//        ByteBuffer payload = ByteBuffer.allocate(1024);
//        int size = clientChannel.read(payload);
//
//        if (size == -1) {
//            clientChannel.close();
//            key.cancel();
//        } else {
//            String line = new String(payload.array(), StandardCharsets.UTF_8).trim();
//
//            System.out.println("[" + Thread.currentThread().getName() + "]" + ": message received from client: " + line + "\n\tEchoing message...");
//
//            if (line.equalsIgnoreCase("fin")) {
//                System.out.println("[" + Thread.currentThread().getName() + "]" + ": " + "A client is disconnected from server");
//
//                clientChannel.close();
//            }
//            payload.rewind();
//            clientChannel.write(payload);
//        }
    }
}
