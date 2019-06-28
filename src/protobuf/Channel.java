package protobuf;

import com.google.protobuf.*;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class Channel implements RpcChannel {

    private IoConnector connector;
    private InetSocketAddress serverAddress;

    private LoadBalance loadBalance;
    private IoSession session;

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

        controller.setChannel(this);

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

    public void init(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("protoBuf", new ProtocolCodecFilter(new ProtoBufEncoder(), new ProtoBufDecoder()));
        connector.setHandler(new ClientIoHandler());
    }

    public IoSession getSession() throws RpcException {
        if (singleServer()) {
            if (session == null || !session.isActive()) {
                synchronized (this) {
                    if (session != null && !session.isActive()) {
                        session.closeNow();
                        session = null;
                    }
                    if (session == null) {
                        ConnectFuture future = connector.connect(serverAddress);
                        future.awaitUninterruptibly();
                        if (future.isConnected()) {
                            session = future.getSession();
                        } else {
                            throw new RpcException("connect server " + serverAddress + " failed");
                        }
                    }
                }
            }
        }
        return session;
    }
}
