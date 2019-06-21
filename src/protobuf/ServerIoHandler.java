package protobuf;

import com.google.protobuf.*;
import log.Log;
import log.LogCategory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protobuf.proto.Rpc;

import java.util.HashMap;
import java.util.Map;

public class ServerIoHandler extends IoHandlerAdapter {

    private Map<String, Service> serviceMap = new HashMap<>();

    @Override
    public void sessionOpened(IoSession session) {
        Log.trace(LogCategory.Server, "Session Open");
    }

    @Override
    public void sessionClosed(IoSession session) {
        Log.trace(LogCategory.Server, "Session Close");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        InputMessage msg = (InputMessage) message;
        Rpc.RpcMeta requestMeta = Rpc.RpcMeta.parseFrom(msg.getMeta().array());

        Rpc.RpcRequestMeta rpcRequestMeta = requestMeta.getRequest();

        Log.trace(LogCategory.Server, "service:{},method:{}", rpcRequestMeta.getServiceName(), rpcRequestMeta.getMethodName());

        Service service = serviceMap.get(rpcRequestMeta.getServiceName());
        if (service == null) {
            throw new RpcException("service not exist");
        }

        Descriptors.MethodDescriptor method = service.getDescriptorForType().findMethodByName(rpcRequestMeta.getMethodName());
        if (method == null) {
            throw new RpcException("method not exist");
        }

        Message request = Compress.parseFromCompressedData(requestMeta.getCompressType(), service.getRequestPrototype(method).newBuilderForType(), msg.getPayload().array());

        Controller controller = new Controller();

        service.callMethod(method, controller, request, response -> {
            Rpc.RpcMeta.Builder metaBuilder = Rpc.RpcMeta.newBuilder();

            Rpc.RpcResponseMeta.Builder responseMetaBuilder = Rpc.RpcResponseMeta.newBuilder();
            if (controller.failed()) {
                responseMetaBuilder.setErrorText(controller.errorText());
            }
            metaBuilder.setResponse(responseMetaBuilder.build());

            byte[] responseByte = null;
            try {
                responseByte = Compress.serializeAsCompressedData(controller.getResponseCompressType(), response);
            } catch (Exception e) {
                controller.setFailed(e.getMessage());
            }

            metaBuilder.setCorrelationId(requestMeta.getCorrelationId());
            metaBuilder.setCompressType(controller.getResponseCompressType());
            Rpc.RpcMeta responseMeta = metaBuilder.build();

            int responseLength = 0;
            if (responseByte != null) {
                responseLength = responseByte.length;
            }
            int metaLength = responseMeta.getSerializedSize();

            IoBuffer responseBuf = IoBuffer.allocate(12 + metaLength + responseLength);

            responseBuf.put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('R').byteValue()).put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('C').byteValue());
            responseBuf.putInt(metaLength + responseLength);
            responseBuf.putInt(metaLength);
            responseBuf.put(responseMeta.toByteArray());
            if (responseByte != null) {
                responseBuf.put(responseByte);
            }

            responseBuf.flip();
            session.write(responseBuf);
        });
    }

    public void addService(Service service) {
        serviceMap.put(service.getDescriptorForType().getFullName(), service);
    }

}
