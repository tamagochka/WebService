package my.tamagochka.main;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
    NIOServer server;
    public SocketChannel socket;
    public byte[] data;

    ServerDataEvent(NIOServer server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
    }
}
