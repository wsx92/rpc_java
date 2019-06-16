import log.Log;
import log.LogCategory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protobuf.*;
import example.proto.Echo;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) {
        NioSocketConnector connector = new NioSocketConnector();
        connector.getFilterChain().addLast("protoBuf", new ProtocolCodecFilter(new ProtoBufEncoder(), new ProtoBufDecoder()));
        connector.setHandler(new ClientIoHandler());

        Channel channel = new Channel();
        channel.init(connector, new InetSocketAddress("localhost", 8000));

        while (true) {
            Controller controller = new Controller();

            Echo.EchoRequest request = Echo.EchoRequest.newBuilder().setMessage("hello world").build();
            Echo.EchoService.Stub stub = Echo.EchoService.newStub(channel);
            stub.echo(controller, request, response -> {
                if (!controller.failed()) {
                    Log.info(LogCategory.Client, response.getMessage());
                } else {
                    Log.error(LogCategory.Client, controller.errorText());
                }
            });
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
