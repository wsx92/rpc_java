package protobuf;

import com.google.protobuf.*;
import org.apache.mina.core.service.IoConnector;

import java.net.InetSocketAddress;

public class Channel implements RpcChannel {

    private IoConnector connector;
    private InetSocketAddress serverAddress;

    private LoadBalance loadBalance;

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

        byte[] requestBuf;
        try {
            requestBuf = Compress.serializeAsCompressedData(controller.getRequestCompressType(), message);
        } catch (Exception e) {
            controller.setFailed(e.getMessage());
            if (rpcCallback != null) {
                rpcCallback.run(message1);
            }
            return;
        }
        controller.setRequestBuf(requestBuf);

        controller.issueRpc();

    }

    public boolean singleServer() {
        return loadBalance == null;
    }

    public void init(IoConnector connector, InetSocketAddress serverAddress) {
        this.connector = connector;
        this.serverAddress = serverAddress;
    }
}
