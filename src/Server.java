import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import protobuf.ProtoBufDecoder;
import protobuf.ProtoBufEncoder;
import protobuf.ServerIoHandler;
import protobuf.impl.EchoServiceImpl;
import protobuf.impl.LoginServiceImpl;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("protoBuf", new ProtocolCodecFilter(new ProtoBufEncoder(), new ProtoBufDecoder()));
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(4)));

        ServerIoHandler ioHandler = new ServerIoHandler();
        ioHandler.addService(new LoginServiceImpl());
        ioHandler.addService(new EchoServiceImpl());
        acceptor.setHandler(ioHandler);
        try {
            acceptor.bind(new InetSocketAddress(1234));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Init Success");
    }
}
