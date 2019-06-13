package protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protobuf.proto.Rpc;

public class ClientIoHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("Session Open");
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("Session Close");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        InputMessage msg = (InputMessage) message;

        Rpc.RpcMeta meta = Rpc.RpcMeta.parseFrom(msg.getMeta().array());

        long callId = meta.getCorrelationId();

        Controller controller = Controller.getController(callId);
        if(controller == null) {
            throw new RpcException("can not find controller by callId");
        }

        Rpc.RpcResponseMeta responseMeta = meta.getResponse();

        if(responseMeta.hasErrorText()) {
            controller.setFailed(responseMeta.getErrorText());
            return;
        }

        if(controller.getResponse() != null) {
            try {
                Message response = controller.getResponse().newBuilderForType().mergeFrom(msg.getPayload().array()).build();
                controller.setResponse(response);
            }
            catch (InvalidProtocolBufferException e) {
                controller.setFailed("fail to parse response message");
                return;
            }
        }

        controller.onRpcReturned();

    }

}
