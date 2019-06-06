package protobuf;

import com.google.protobuf.*;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protobuf.proto.Rpc;


import java.util.HashMap;
import java.util.Map;

public class ServerIoHandler extends IoHandlerAdapter {

    private Map<String, Service> serviceMap = new HashMap<>();

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

        Rpc.RpcMeta meta = (Rpc.RpcMeta) message;
        Rpc.Request rpcRequest = meta.getRequest();

        Service service = serviceMap.get(rpcRequest.getServiceName());

        if(service == null) {
            return;
        }

        Descriptors.MethodDescriptor method = service.getDescriptorForType().findMethodByName(rpcRequest.getMethodName());

        if(method == null) {
            return;
        }

        Message.Builder builder;
        try {
            builder = service.getRequestPrototype(method).newBuilderForType().mergeFrom(rpcRequest.getProto());
            if(!builder.isInitialized()) {
                throw new Exception();
            }
        }
        catch (InvalidProtocolBufferException e) {
            throw new Exception(e);
        }

        Message request = builder.build();

        RpcController controller = null;

        service.callMethod(method, null, request, null);

        Rpc.Response.Builder responseBuilder = Rpc.Response.newBuilder();
        responseBuilder.setErrorCode(200);

        Rpc.RpcMeta.Builder metaBuilder = Rpc.RpcMeta.newBuilder();
        metaBuilder.setResponse(responseBuilder.build());

        session.write(metaBuilder.build());

    }

    public void addService(Service service) {
        serviceMap.put(service.getDescriptorForType().getFullName(), service);
    }

}
