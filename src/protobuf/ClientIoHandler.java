package protobuf;

import com.google.protobuf.Message;
import log.Log;
import log.LogCategory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protobuf.proto.Rpc;

public class ClientIoHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) {
        Log.trace(LogCategory.Client, "Session Open");
    }

    @Override
    public void sessionClosed(IoSession session) {
        Log.trace(LogCategory.Client, "Session Close");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        InputMessage msg = (InputMessage) message;

        Rpc.RpcMeta meta = Rpc.RpcMeta.parseFrom(msg.getMeta().array());

        long callId = meta.getCorrelationId();

        Controller controller = Controller.getController(callId);
        if (controller == null) {
            throw new RpcException("can not find controller by callId");
        }

        Rpc.RpcResponseMeta responseMeta = meta.getResponse();

        if (responseMeta.hasErrorText()) {
            controller.setFailed(responseMeta.getErrorText());
        }

        if (controller.getResponse() != null) {
            try {
                Message response = Compress.parseFromCompressedData(meta.getCompressType(), controller.getResponse().newBuilderForType(), msg.getPayload().array());
                controller.setResponse(response);
            } catch (Exception e) {
                controller.setFailed("fail to parse response message " + e.getMessage());
            }
        }

        controller.onRpcReturned();

    }

}
