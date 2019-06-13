package protobuf.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import protobuf.proto.Echo;

public class EchoServiceImpl extends Echo.EchoService {
    @Override
    public void echo(RpcController controller, Echo.EchoRequest request, RpcCallback<Echo.EchoResponse> done) {
        System.out.println(request.getMessage());
        Echo.EchoResponse.Builder builder = Echo.EchoResponse.newBuilder();
        builder.setMessage(request.getMessage());
        done.run(builder.build());
    }
}
