package example.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import example.proto.Echo;
import org.slf4j.LoggerFactory;


public class EchoServiceImpl extends Echo.EchoService {
    @Override
    public void echo(RpcController controller, Echo.EchoRequest request, RpcCallback<Echo.EchoResponse> done) {
        LoggerFactory.getLogger(EchoServiceImpl.class).info(request.getMessage());
        Echo.EchoResponse.Builder builder = Echo.EchoResponse.newBuilder();
        builder.setMessage(request.getMessage());
        done.run(builder.build());
    }
}
