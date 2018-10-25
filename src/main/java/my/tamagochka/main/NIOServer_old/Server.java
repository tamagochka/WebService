package my.tamagochka.main.NIOServer_old;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server implements Runnable {

    private final int port;
    private Selector selector;
    private int bufferSize = 100;

    private Sender sender;
    private Thread senderThread;





    FileOutputStream fout = null;




    public final Map<SocketChannel, List<byte[]>> receivedData = new HashMap<>();
    public final Map<SocketChannel, List<byte[]>> dataToSend = new HashMap<>();

    public Server(int port, int bufferSize) {





        try {
            fout = new FileOutputStream("file.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




        this.port = port;
        this.bufferSize = bufferSize;
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(port));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender = new Sender();
        senderThread = new Thread(sender);
        senderThread.start();
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
    
    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        int numRead = 0;
        try {
            if((numRead = sc.read(buffer)) == -1) {
                throw new IOException();
            }
            fout.write((System.currentTimeMillis() + "read sc.read" + System.lineSeparator()).getBytes()); fout.flush();
        } catch(IOException e) {
            fout.write((System.currentTimeMillis() + " read sc.read exception!" + System.lineSeparator()).getBytes()); fout.flush();
            closeConnection(sc);
            return;
        }
        byte dataCopy[] = new byte[numRead];
        System.arraycopy(buffer.array(), 0, dataCopy, 0, numRead);
        fout.write((System.currentTimeMillis() + "read sync receivedData" + System.lineSeparator()).getBytes()); fout.flush();
        synchronized(receivedData) {
            fout.write((System.currentTimeMillis() + "read receivedData.get" + System.lineSeparator()).getBytes()); fout.flush();
            List<byte[]> queue = receivedData.get(sc);
            if(queue == null) {
                fout.write((System.currentTimeMillis() + "read receivedData queue new ArrayList" + System.lineSeparator()).getBytes()); fout.flush();
                queue = new ArrayList<>();
                fout.write((System.currentTimeMillis() + "read receivedData.put" + System.lineSeparator()).getBytes()); fout.flush();
                receivedData.put(sc, queue);
            }
            fout.write((System.currentTimeMillis() + "read receivedData queue add dataCopy" + System.lineSeparator()).getBytes()); fout.flush();
            queue.add(dataCopy);
            fout.write((System.currentTimeMillis() + "read receivedData.notify" + System.lineSeparator()).getBytes()); fout.flush();
            receivedData.notifyAll();
        }
    }

    public void closeConnection(SocketChannel sc) {
        synchronized(dataToSend) {
            dataToSend.remove(sc);
            try {
                sc.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitMessages() throws IOException {
        fout.write((System.currentTimeMillis() + "waitMessages sync receivedData" + System.lineSeparator()).getBytes()); fout.flush();
//        synchronized(receivedData) {
            fout.write((System.currentTimeMillis() + "waitMessages receivedData.isEmpty" + System.lineSeparator()).getBytes()); fout.flush();
            while(receivedData.isEmpty()) {
                try {
                    fout.write((System.currentTimeMillis() + "waitMessages receivedData.wait" + System.lineSeparator()).getBytes()); fout.flush();
                    receivedData.wait();
                } catch(InterruptedException e) {
                    fout.write((System.currentTimeMillis() + " !!! waitMessages exception" + System.lineSeparator()).getBytes()); fout.flush();
                    e.printStackTrace();
                }
            }
//        }
    }

    public void send(SocketChannel sc, byte data[]) throws IOException {
        fout.write((System.currentTimeMillis() + "send sync dataToSend" + System.lineSeparator()).getBytes()); fout.flush();
        synchronized(dataToSend) {
            fout.write((System.currentTimeMillis() + "send dataToSend.get" + System.lineSeparator()).getBytes()); fout.flush();
            List<byte[]> queue = dataToSend.get(sc);
            if(queue == null) {
                fout.write((System.currentTimeMillis() + "send dataToSend queue new ArrayList" + System.lineSeparator()).getBytes()); fout.flush();
                queue = new ArrayList<>();
                fout.write((System.currentTimeMillis() + "send dataToSend.put" + System.lineSeparator()).getBytes()); fout.flush();
                dataToSend.put(sc, queue);
            }
            fout.write((System.currentTimeMillis() + "send dataToSend queue add" + System.lineSeparator()).getBytes()); fout.flush();
            queue.add(data);
            fout.write((System.currentTimeMillis() + "send sender.addSendRequest" + System.lineSeparator()).getBytes()); fout.flush();
            sender.addSendRequest(sc);
            fout.write((System.currentTimeMillis() + "send dataToSend.notify" + System.lineSeparator()).getBytes()); fout.flush();
            dataToSend.notifyAll();
        }
    }

    public boolean hasData(SocketChannel sc) throws IOException {
//        synchronized(receivedData) {
            fout.write((System.currentTimeMillis() + "hasData receivedData.get" + System.lineSeparator()).getBytes()); fout.flush();
            if(receivedData.get(sc) == null) {
                fout.write((System.currentTimeMillis() + "hasData returned false" + System.lineSeparator()).getBytes()); fout.flush();
                return false;
            }
            fout.write((System.currentTimeMillis() + "hasData returned " + !receivedData.get(sc).isEmpty() + System.lineSeparator()).getBytes()); fout.flush();
            return !receivedData.get(sc).isEmpty();
//        }
    }

    public byte[] receive(SocketChannel sc) throws IOException {
//        synchronized(receivedData) {
        fout.write((System.currentTimeMillis() + "receive receivedData.get" + System.lineSeparator()).getBytes()); fout.flush();
        byte[] data = receivedData.get(sc).remove(0);
            if(receivedData.get(sc).isEmpty())
                receivedData.remove(sc);
            return data;
//        }
    }

    public Set<SocketChannel> getChannels() throws IOException {
        fout.write((System.currentTimeMillis() + "getChannels" + System.lineSeparator()).getBytes()); fout.flush();
        return receivedData.keySet();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        fout.write((System.currentTimeMillis() + "write sync dataToSend" + System.lineSeparator()).getBytes()); fout.flush();
        synchronized(dataToSend) {
            fout.write((System.currentTimeMillis() + "write dataToSend.get" + System.lineSeparator()).getBytes()); fout.flush();
            List<byte[]> queue = dataToSend.get(sc);
            while(!queue.isEmpty()) {
                buffer.clear();
                buffer.put(queue.get(0));
                buffer.flip();
                fout.write((System.currentTimeMillis() + "write sc.write" + System.lineSeparator()).getBytes()); fout.flush();
                try {
                    sc.write(buffer);
                    if(buffer.remaining() > 0) break;
                } catch(IOException e) {
                    fout.write((System.currentTimeMillis() + " !!! write exception" + System.lineSeparator()).getBytes()); fout.flush();
                    e.printStackTrace();
                }
                fout.write((System.currentTimeMillis() + "write queue.remove" + System.lineSeparator()).getBytes()); fout.flush();
                queue.remove(0);
            }
//            if(queue.isEmpty()) {
            fout.write((System.currentTimeMillis() + "write key.interestOps" + System.lineSeparator()).getBytes()); fout.flush();

            key.interestOps(SelectionKey.OP_READ);
            fout.write((System.currentTimeMillis() + "write dataToSend.remove" + System.lineSeparator()).getBytes()); fout.flush();

            dataToSend.remove(sc);
//            }
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                fout.write((System.currentTimeMillis() + "run selector.select" + System.lineSeparator()).getBytes()); fout.flush();
                selector.select();
            } catch(IOException e) {
                try {
                    fout.write((System.currentTimeMillis() + " !!! run exception 1" + System.lineSeparator()).getBytes()); fout.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            try {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    fout.write((System.currentTimeMillis() + "run iterator.next" + System.lineSeparator()).getBytes()); fout.flush();
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    fout.write((System.currentTimeMillis() + "run key.isValid" + System.lineSeparator()).getBytes()); fout.flush();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept(key);
                    else if (key.isReadable()) read(key);
                    else if (key.isWritable()) write(key);
                    else fout.write((System.currentTimeMillis() + "run oops" + System.lineSeparator()).getBytes()); fout.flush();
                }
            } catch(IOException e) {
                try {
                    fout.write((System.currentTimeMillis() + " !!! run exception 2" + System.lineSeparator()).getBytes()); fout.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private class Sender implements Runnable {

        private List<SocketChannel> sendRequests = new LinkedList<>();

        public void addSendRequest(SocketChannel sc) {
            sendRequests.add(sc);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    fout.write((System.currentTimeMillis() + "sender run sync dataToSend" + System.lineSeparator()).getBytes()); fout.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized(dataToSend) {
                    try {
                        fout.write((System.currentTimeMillis() + "sender run sendRequests.isEmpty" + System.lineSeparator()).getBytes()); fout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while(sendRequests.isEmpty()) {
                        try {
                            try {
                                fout.write((System.currentTimeMillis() + "sender run dataToSend.wait" + System.lineSeparator()).getBytes()); fout.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dataToSend.wait();
                        } catch(InterruptedException e) {
                            try {
                                fout.write((System.currentTimeMillis() + " !!! run exception 3" + System.lineSeparator()).getBytes()); fout.flush();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }
                    try {
                        fout.write((System.currentTimeMillis() + "sender run sc.keyFor" + System.lineSeparator()).getBytes()); fout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(SocketChannel sc : sendRequests) {
                        sc.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    }
                    try {
                        fout.write((System.currentTimeMillis() + "sender run sendRequests.clear" + System.lineSeparator()).getBytes()); fout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendRequests.clear();
                    try {
                        fout.write((System.currentTimeMillis() + "sender run selector.wakeup" + System.lineSeparator()).getBytes()); fout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selector.wakeup();
                }
            }
        }
    }

}
