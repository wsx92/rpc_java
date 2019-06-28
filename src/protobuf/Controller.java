package protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import protobuf.proto.Rpc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Controller implements RpcController {

    private static AtomicLong callId = new AtomicLong(0);

    private String errorText;
    private boolean failed;

    private Message response;

    private RpcCallback<Message> done;

    private int requestCompressType;
    private int responseCompressType;

    private byte[] requestBuf;
    private Descriptors.MethodDescriptor method;

    private long currentId;

    private static ConcurrentMap<Long, Controller> rpcMap = new ConcurrentHashMap<>();

    private Channel channel;

    @Override
    public void reset() {

    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String errorText() {
        return errorText;
    }

    @Override
    public void startCancel() {

    }

    @Override
    public void setFailed(String s) {
        errorText = s;
        failed = true;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void notifyOnCancel(RpcCallback<Object> rpcCallback) {

    }


    long callId() {
        return callId.incrementAndGet();
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }

    public RpcCallback<Message> getDone() {
        return done;
    }

    public void setDone(RpcCallback<Message> done) {
        this.done = done;
    }

    public int getRequestCompressType() {
        return requestCompressType;
    }

    public void setRequestCompressType(int requestCompressType) {
        this.requestCompressType = requestCompressType;
    }

    public int getResponseCompressType() {
        return responseCompressType;
    }

    public void setResponseCompressType(int responseCompressType) {
        this.responseCompressType = responseCompressType;
    }

    public byte[] getRequestBuf() {
        return requestBuf;
    }

    public void setRequestBuf(byte[] requestBuf) {
        this.requestBuf = requestBuf;
    }

    public Descriptors.MethodDescriptor getMethod() {
        return method;
    }

    public void setMethod(Descriptors.MethodDescriptor method) {
        this.method = method;
    }

    public long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(long currentId) {
        this.currentId = currentId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static Controller getController(long callId) {
        return rpcMap.get(callId);
    }

    private IoBuffer packRequest() {
        setCurrentId(callId());
        Rpc.RpcMeta.Builder metaBuilder = Rpc.RpcMeta.newBuilder();
        metaBuilder.setCorrelationId(getCurrentId());
        metaBuilder.setCompressType(getRequestCompressType());
        Rpc.RpcRequestMeta.Builder requestMetaBuilder = Rpc.RpcRequestMeta.newBuilder();
        requestMetaBuilder.setServiceName(method.getService().getFullName());
        requestMetaBuilder.setMethodName(method.getName());
        metaBuilder.setRequest(requestMetaBuilder.build());
        Rpc.RpcMeta requestMeta = metaBuilder.build();

        int requestLength = requestBuf.length;
        int metaLength = requestMeta.getSerializedSize();

        IoBuffer request = IoBuffer.allocate(12 + metaLength + requestLength);

        request.put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('R').byteValue()).put(Integer.valueOf('P').byteValue()).put(Integer.valueOf('C').byteValue());
        request.putInt(metaLength + requestLength);
        request.putInt(metaLength);
        request.put(requestMeta.toByteArray());
        request.put(requestBuf);
        request.flip();

        return request;
    }

    public void issueRpc() {
        //make request
        IoBuffer request = packRequest();

        try {
            IoSession session = channel.getSession();
            session.write(request);
        } catch (RpcException e) {
            setFailed(e.getMessage());
            if (done != null) {
                done.run(response);
            }
        }

        rpcMap.put(getCurrentId(), this);
    }

    public void onRpcReturned() {
        endRpc();
    }

    public void endRpc() {
        if (done != null) {
            done.run(response);
        }
    }

}
