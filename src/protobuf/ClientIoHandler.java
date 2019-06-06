package protobuf;

import com.google.protobuf.*;
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

        Rpc.RpcMeta meta = (Rpc.RpcMeta) message;
        Rpc.Response rpcResponse = meta.getResponse();

        System.out.println(rpcResponse.getErrorCode() + rpcResponse.getErrorText());

    }

    public static void request(IoSession session,Descriptors.MethodDescriptor method, Message message) {
        Rpc.Request.Builder request = Rpc.Request.newBuilder();
        request.setServiceName(method.getService().getFullName());
        request.setMethodName(method.getName());
        request.setProto(message.toByteString());
        Rpc.RpcMeta meta = Rpc.RpcMeta.newBuilder().setRequest(request.build()).build();
        session.write(meta);
    }

    public static void write(IoSession session, Object object) {
        session.write(object);
    }

}
