package my.tamagochka.main;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static java.nio.channels.SelectionKey.OP_WRITE;

public class NIOServer {
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(100);
    private EchoWorker worker = new EchoWorker();
    private final List<ChangeRequest> changeRequests = new LinkedList<>();
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    public NIOServer() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(5050));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            new Thread(worker).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        try {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {

        SocketChannel sc = (SocketChannel) key.channel();
        buf.clear();
        int numRead = 0;
        try {
            numRead = sc.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte a[] = Arrays.copyOf(buf.array(), numRead - 2);

        System.out.println(new String(buf.array(), 0, numRead));

        if(Arrays.equals(a, new byte[] {'B', 'y', 'e', '.'})) {
                try {
                    sc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        worker.processData(this, sc, buf.array(), numRead);
    }

    private void write(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        synchronized(pendingData) {
            List<ByteBuffer> queue = pendingData.get(sc);
            while(!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                try {
                    sc.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(buf.remaining() > 0) {
                    break;
                }
                queue.remove(0);
            }
            if(queue.isEmpty()) key.interestOps(SelectionKey.OP_READ);
        }
    }

    void send(SocketChannel socket, byte[] data) {
        synchronized(changeRequests) {
            changeRequests.add(new ChangeRequest(socket, ChangeType.CHANGEOPS, OP_WRITE));
            synchronized(pendingData) {
                List<ByteBuffer> queue = pendingData.get(socket);
                if(queue == null) {
                    queue = new ArrayList<>();
                    pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }
        selector.wakeup();
    }

    public void run() {
        java.util.logging.Logger.getGlobal().info("Server started");
        while(true) {
            synchronized(changeRequests) {
                for(ChangeRequest change: changeRequests) {
                    switch(change.type) {
                        case CHANGEOPS:
                            SelectionKey key = change.socket.keyFor(selector);
                            key.interestOps(change.ops);
                            break;
                        default:
                    }
                }
                changeRequests.clear();
            }
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }



            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while(selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
//            for(SelectionKey key : selector.selectedKeys()) {
//                selector.selectedKeys().remove(key);
                if(!key.isValid()) continue;
                if(key.isAcceptable()) {
                    accept(key);
                } else if(key.isReadable()) {
                    read(key);
                } else if(key.isWritable()) {
                    write(key);
                }
            }
        }
    }

}
