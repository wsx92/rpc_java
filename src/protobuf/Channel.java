package protobuf;

import com.google.protobuf.*;
import org.apache.mina.core.service.IoConnector;

import java.net.InetSocketAddress;

public class Channel implements RpcChannel {

    private IoConnector connector;
    private InetSocketAddress serverAddress;

    @Override
    public void callMethod(Descriptors.MethodDescriptor methodDescriptor, RpcController rpcController, Message message, Message message1, RpcCallback<Message> rpcCallback) {

        Controller controller = (Controller) rpcController;

        if (message == null) {
            controller.setFailed("request is null");
            if (rpcCallback != null) {
                rpcCallback.run(message1);
            }
            return;
        }
        if (methodDescriptor == null) {
            controller.setFailed("method is null");
            if (rpcCallback != null) {
                rpcCallback.run(message1);
            }
            return;
        }

        controller.setResponse(message1);
        controller.setDone(rpcCallback);
        controller.setMethod(methodDescriptor);

        controller.setConnector(connector);
        if (singleServer()) {
            controller.setSingleServerAddress(serverAddress);
        }

        controller.setRequestBuf(message.toByteArray());

        controller.issueRpc();

    }

    public boolean singleServer() {
        return true;
    }

    public void init(IoConnector connector, InetSocketAddress serverAddress) {
        this.connector = connector;
        this.serverAddress = serverAddress;
    }
}
