package protobuf;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Controller implements RpcController {

    private AtomicLong callId = new AtomicLong(0);

    private String errorText;
    private boolean failed;

    private Message response;

    private RpcCallback<Message> done;

    private static ConcurrentMap<Long, Controller> rpcMap = new ConcurrentHashMap<>();

    public IoSession session;

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

    public RpcCallback getDone() {
        return done;
    }

    public void setDone(RpcCallback done) {
        this.done = done;
    }

    public static Controller getController(long callId) {
        return rpcMap.get(callId);
    }

    public void issueRpc(long callId) {
        rpcMap.put(callId, this);
    }

    public void onRpcReturned() {
        endRpc();
    }

    public void endRpc() {
        if(done != null) {
            done.run(response);
        }
    }

}
