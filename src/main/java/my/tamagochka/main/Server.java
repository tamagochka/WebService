package my.tamagochka.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Server implements Runnable {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final int port;
    private final int bufferSize;

    private Selector selector;

    private final Map<SocketChannel, Queue<byte[]>> receivedData = new HashMap<>();
    private final Map<SocketChannel, Queue<byte[]>> dataToSend = new HashMap<>();

    private Sender sender = new Sender();
    private Thread threadSender;

    interface Remainder { void accept(SocketChannel channel, byte[][] notReceived, byte[][] notSended); }

    private Consumer<SocketChannel> OnAccept;
    private Remainder OnClose;
    private Remainder OnHalt;

    private BiConsumer<SocketChannel, byte[]> OnReceive;
    private BiConsumer<SocketChannel, byte[][]> OnSended;

    private Remainder OnReadException;
    private Remainder OnWriteException;
    private Remainder OnSendQueueException;


    public void onAccept(Consumer<SocketChannel> OnAccept) {
        this.OnAccept = OnAccept;
    }
    public void onClose(Remainder OnClose) {
        this.OnClose = OnClose;
    }
    public void onHalt(Remainder OnHalt) {
        this.OnHalt = OnHalt;
    }
    public void onRecieve(BiConsumer<SocketChannel, byte[]> OnReceive) {
        this.OnReceive = OnReceive;
    }
    public void onSended(BiConsumer<SocketChannel, byte[][]> OnSended) {
        this.OnSended = OnSended;
    }
    public void onReadException(Remainder OnReadException) { this.OnReadException = OnReadException; }
    public void onWriteException(Remainder OnWriteException) { this.OnWriteException = OnWriteException; }
    public void onSendQueueException(Remainder OnSendQueueException) { this.OnSendQueueException = OnSendQueueException; }

    /**/    public Server(int port) {
        this(port, DEFAULT_BUFFER_SIZE);
    }

    /**/public Server(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(port));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**/    private void accept(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        try {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            receivedData.computeIfAbsent(sc, k -> new LinkedList<>());
            if(OnAccept != null)
                OnAccept.accept(sc);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

/*
    public Set<SocketChannel> channels() {
        synchronized(receivedData) {
            if(receivedData.isEmpty()) return null;
            return new HashSet<>(receivedData.keySet());
        }
    }
*/

    public byte[] receive(SocketChannel sc) {
        synchronized(receivedData) {
            if(receivedData.get(sc).isEmpty()) {
                if(!sc.isConnected()) receivedData.remove(sc);
                return null;
            }
            return receivedData.get(sc).poll();//remove(0);
        }
    }

/**/    private boolean hasData(final Map<SocketChannel, Queue<byte[]>> data) {
        synchronized(data) {
            if(data.isEmpty()) return false;
            for(SocketChannel sc : data.keySet()) {
                if(hasDataInChannel(sc, data)) return true;
            }
            return false;
        }
    }

/**/    private boolean hasDataInChannel(SocketChannel sc, final Map<SocketChannel, Queue<byte[]>> data) {
        synchronized(data) {
            if(data.isEmpty()) return false;
            if(data.get(sc) == null) return false;
            return !data.get(sc).isEmpty();
        }
    }

    public void waitData() {
        waitData(receivedData);
    }

    /**/private void waitData(final Map<SocketChannel, Queue<byte[]>> data) {
        synchronized(data) {
            while(!hasData(data)) {
                try {
                    data.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

/**/    public byte[][] close(SocketChannel sc) {
        byte[][] notSended = null;
        synchronized(dataToSend) {
            if(dataToSend.get(sc) != null && sc.isConnected()) {
                while(!dataToSend.get(sc).isEmpty()) {
                    try {
                        dataToSend.wait();
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dataToSend.remove(sc);
            } else {
                notSended = dataToSend.get(sc) != null ? dataToSend.remove(sc).toArray(new byte[][]{}) : null;
            }
        }
        try {
            if(sc != null && sc.isConnected()) {
                sc.keyFor(selector).cancel();
                sc.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        byte[][] notReceived = null;
        synchronized(receivedData) {
            if(receivedData.get(sc) != null) {
                if(receivedData.get(sc).isEmpty()) {
                    receivedData.remove(sc);
                } else {
                    notReceived = receivedData.remove(sc).toArray(new byte[][]{});
                }
            }
        }
        if(OnClose != null) {
            OnClose.accept(sc, notReceived, notSended);
        }
        return notReceived;
    }

/**/    public byte[][] halt(SocketChannel sc) {
        byte[][] notSended = null;
        synchronized(dataToSend) {
            try {
                sc.keyFor(selector).cancel();
                sc.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            notSended = dataToSend.get(sc) != null ? dataToSend.remove(sc).toArray(new byte[][]{}) : null;
        }
        byte[][] notReceived = null;
        synchronized(receivedData) {
            if(receivedData.get(sc) != null) {
                if(receivedData.get(sc).isEmpty()) {
                    receivedData.remove(sc);
                } else {
                    notReceived = receivedData.remove(sc).toArray(new byte[][]{});
                }
            }
            if(OnHalt != null) {
                OnHalt.accept(sc, notReceived, notSended);
            }
            return notReceived;
        }
    }

/**/    private void read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        int numRead = 0;
        try {
            if((numRead = sc.read(buffer)) == -1) {
/*
                synchronized(receivedData) {
                    if(receivedData.get(sc).isEmpty())
                        receivedData.remove(sc);
                }
                sc.close();
*/
                halt(sc);
                return;
            }
        } catch(IOException e) {
            if(OnReadException != null) {
                synchronized(dataToSend) {
                    OnReadException.accept(sc,
                            receivedData.get(sc) != null ? receivedData.get(sc).toArray(new byte[][]{}) : null,
                            dataToSend.get(sc) != null ? dataToSend.get(sc).toArray(new byte[][]{}) : null);
                }
            } else {
                halt(sc);
            }
            e.printStackTrace();
            return;
        }
        byte[] dataCopy = new byte[numRead];
        System.arraycopy(buffer.array(), 0, dataCopy, 0, numRead);
        if(OnReceive != null)
            OnReceive.accept(sc, dataCopy);
        else {
            synchronized(receivedData) {
                receivedData.get(sc).add(dataCopy);
                receivedData.notify();
            }
        }
    }

    /**/private void write(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        synchronized(dataToSend) {
            //List<byte[]> queue = dataToSend.get(sc);
            Queue<byte[]> queue = dataToSend.get(sc);
            List<byte[]> sended = new ArrayList<>(queue);
            while(!queue.isEmpty()) {
                ByteBuffer buffer = ByteBuffer.wrap(queue.poll()/*queue.remove(0)*/);
                try {
                    sc.write(buffer);
                } catch(IOException e) {
                    if(OnWriteException != null) {
                        synchronized(receivedData) {
                            OnWriteException.accept(sc,
                                    receivedData.get(sc) != null ? receivedData.get(sc).toArray(new byte[][]{}) : null,
                                    receivedData.get(sc) != null ? queue.toArray(new byte[][]{}) : null);
                        }
                    } else {
                        halt(sc);
                    }
                    e.printStackTrace();
                    return;
                }
            }
            key.interestOps(SelectionKey.OP_READ);
            if(OnSended != null) OnSended.accept(sc, sended.toArray(new byte[][]{}));
            dataToSend.notify();
        }
    }

  /**/  public void send(SocketChannel sc, byte[] data) {
        if(threadSender == null) {
            threadSender = new Thread(sender);
            threadSender.start();
        }
        synchronized(dataToSend) {
            Queue<byte[]> queue = dataToSend.computeIfAbsent(sc, k -> new LinkedList<>());
            queue.add(data);
            dataToSend.notify();
        }
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                selector.select();
            } catch(IOException e) {
                e.printStackTrace();
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(!key.isValid()) continue;
                if(key.isAcceptable()) accept(key);
                else if(key.isReadable()) read(key);
                else if(key.isWritable()) write(key);
            }
        }
    }

    private class Sender implements Runnable {

        @Override
        public void run() {
            while(true) {
                synchronized(dataToSend) {
                    waitData(dataToSend);
                    for(SocketChannel sc : dataToSend.keySet()) {
//                        try {
//                        if(sc != null) {
                            SelectionKey key = sc.keyFor(selector);
                            if(key != null && key.isValid()) {
                                if(hasDataInChannel(sc, dataToSend)) {
                                    key.interestOps(SelectionKey.OP_WRITE);
//                                sc.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                                }
                            }
//                        }
/*
                        } catch(Exception e) {
                            System.out.println("oiutoeirutyoweiurtyoewiutyroitu");
                            synchronized(receivedData) {
                                if(OnSendQueueException != null) {
                                    OnSendQueueException.accept(sc, receivedData.get(sc) != null ? receivedData.remove(sc).toArray(new byte[][]{}) : null,
                                            dataToSend.get(sc) != null ? dataToSend.remove(sc).toArray(new byte[][]{}) : null);
                                }
                            }
                        }
*/
                    }
                    selector.wakeup();
                }
            }
        }

    }

}
