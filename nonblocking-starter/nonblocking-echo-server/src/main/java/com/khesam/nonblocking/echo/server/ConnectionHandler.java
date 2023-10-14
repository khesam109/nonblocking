package com.khesam.nonblocking.echo.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

/**
 * <a href="https://gist.github.com/MaZderMind/0be1dea275ea25b39dc753583d742851">Read input line by line</a>
 */
public class ConnectionHandler {

    private static ConnectionHandler INSTANCE = null;
    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;
    private final CharsetDecoder decoder;
    private final CharsetEncoder encoder;
    private final SegmentedBuffer segmentedBuffer;

    private ConnectionHandler() {
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.charBuffer = CharBuffer.allocate(1024);
        this.decoder = StandardCharsets.UTF_8.newDecoder();
        this.encoder = StandardCharsets.UTF_8.newEncoder();
        this.segmentedBuffer = new SegmentedBuffer();
    }

    public synchronized static ConnectionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionHandler();
        }

        return INSTANCE;
    }

    public void read(SocketChannel clientSocket) throws IOException {
        this.byteBuffer.clear();

        if ((clientSocket.read(byteBuffer)) == -1) {
            //???
            clientSocket.close();
        }

        byteBuffer.flip();
        CoderResult decodeResult;
        do {
            charBuffer.clear();
            decodeResult = decoder.decode(byteBuffer, charBuffer, false);
            charBuffer.flip();

            segmentedBuffer.put(charBuffer);
        } while (decodeResult == CoderResult.OVERFLOW);

        while (segmentedBuffer.hasNext()) {
            String line = segmentedBuffer.next().trim();
            if (line.equals("fin")) {
                System.out.println("[" + Thread.currentThread().getName() + "]" + ": " + "A client is disconnected from server");
                clientSocket.close();
            } else {
                System.out.println("[" + Thread.currentThread().getName() + "]" + ": message received from client: " + line + "\n\tEchoing message...");
                clientSocket.write(encoder.encode(CharBuffer.wrap(line)));
            }
        }

    }
}
