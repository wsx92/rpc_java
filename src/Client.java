import log.Log;
import log.LogCategory;
import protobuf.*;
import example.proto.Echo;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) {

        Channel channel = new Channel();
        channel.init(new InetSocketAddress("localhost", 8000));

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
