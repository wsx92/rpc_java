package protobuf;

import com.google.protobuf.*;
import org.apache.mina.core.buffer.IoBuffer;
import protobuf.proto.Rpc;

public class Channel implements RpcChannel {
    @Override
    public void callMethod(Descriptors.MethodDescriptor methodDescriptor, RpcController rpcController, Message message, Message message1, RpcCallback<Message> rpcCallback) {

        Controller controller = (Controller) rpcController;

        if(message == null) {
            controller.setFailed("request is null");
            return;
        }
        if(methodDescriptor == null) {
            controller.setFailed("method is null");
            return;
        }

        long callId = controller.callId();
        controller.setResponse(message1);
        controller.setDone(rpcCallback);

        Rpc.RpcMeta.Builder metaBuilder = Rpc.RpcMeta.newBuilder();
        metaBuilder.setCorrelationId(callId);
        Rpc.RpcRequestMeta.Builder requestMetaBuilder = Rpc.RpcRequestMeta.newBuilder();
        requestMetaBuilder.setServiceName(methodDescriptor.getService().getFullName());
        requestMetaBuilder.setMethodName(methodDescriptor.getName());
        metaBuilder.setRequest(requestMetaBuilder.build());
        Rpc.RpcMeta requestMeta = metaBuilder.build();

        int requestLength = message.getSerializedSize();
        int metaLength = requestMeta.getSerializedSize();

        IoBuffer requestBuf = IoBuffer.allocate(12 + metaLength + requestLength);

        requestBuf.put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('R').byteValue()).put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('C').byteValue());
        requestBuf.putInt(metaLength + requestLength);
        requestBuf.putInt(metaLength);
        requestBuf.put(requestMeta.toByteArray());
        requestBuf.put(message.toByteArray());
        requestBuf.flip();

        controller.session.write(requestBuf);

        controller.issueRpc(callId);

    }
}
