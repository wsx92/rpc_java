import log.Log;
import log.LogCategory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import protobuf.ProtoBufDecoder;
import protobuf.ProtoBufEncoder;
import protobuf.ServerIoHandler;
import example.impl.EchoServiceImpl;
import example.impl.LoginServiceImpl;

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
            acceptor.bind(new InetSocketAddress(8000));
        } catch (Exception e) {
            Log.error(LogCategory.Server, e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        Log.info(LogCategory.Server, "Init Success");
    }
}
