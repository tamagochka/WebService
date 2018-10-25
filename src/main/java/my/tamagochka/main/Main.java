package my.tamagochka.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Map<SocketChannel, Thread> threads = new HashMap<>();

    private static class Sender implements Runnable {

        private SocketChannel sc;
        private Server server;

        public Sender(SocketChannel sc, Server server) {
            this.sc = sc;
            this.server = server;
        }

        @Override
        public void run() {
            for(int i = 0; i < 100000; i++) {
                if(Thread.interrupted()) {
                    System.out.println("!!!!!thread interrupted!");
                    break;
                }
                String str = Integer.toString(i);
                server.send(sc, str.getBytes());
            }
            server.close(sc);
        }

    }


    public static void main(String[] args) {
        new NIOServer().run();
/*
        Server server = new Server(5050, 100);
        Thread serverThread = new Thread(server);
*/



        // работающий код отправки сообщений подключившемуся клиенту

/*
        server.onAccept(sc -> {
            Sender sender = new Sender(sc, server);
            Thread thread = new Thread(sender);
            threads.put(sc, thread);
            thread.start();
        });
        server.onWriteException((sc, notReceived, notSended) -> {
            server.halt(sc);
        });
        server.onSendQueueException((sc, notReceived, notSended) -> {
            threads.get(sc).interrupt();
        });
*/



        // работающий код для приема сообщений через события
/*
        server.onRecieve((sc, data) -> {
            try {
                System.out.println(((InetSocketAddress) sc.getRemoteAddress()).getAddress() + ":" + ((InetSocketAddress) sc.getRemoteAddress()).getPort() + " - " + new String(data));
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        server.onReadException((sc, notReceived, notSended) -> {
            System.out.printf("suck my dick!");
            server.halt(sc);
        });
*/





/*
        serverThread.start();


        try {
            serverThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
*/

/*
      // работающий код для приема сообщений от клиента
        while(true) {
            server.waitData();
            int n = 0;
            for(SocketChannel sc : server.channels()) {
                n++;
                byte data[] = server.receive(sc);
                while(data != null) {
                    System.out.println(n + " - " + new String(data));
                    data = server.receive(sc);
                }
            }


        }
*/











    }
}
