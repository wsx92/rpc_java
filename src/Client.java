import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protobuf.*;
import protobuf.proto.Echo;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) {
        NioSocketConnector connector = new NioSocketConnector();
        connector.getFilterChain().addLast("protoBuf", new ProtocolCodecFilter(new ProtoBufEncoder(), new ProtoBufDecoder()));
        connector.setHandler(new ClientIoHandler());

        ConnectFuture future = connector.connect(new InetSocketAddress("localhost", 1234));

        future.awaitUninterruptibly();

        IoSession session = future.getSession();

        Channel channel = new Channel();
        Controller controller = new Controller();
        controller.session = session;

        Echo.EchoRequest request = Echo.EchoRequest.newBuilder().setMessage("hello").build();
        Echo.EchoService.Stub stub = Echo.EchoService.newStub(channel);
        stub.echo(controller, request, response -> System.out.println(response.getMessage()));

    }
}
