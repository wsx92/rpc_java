package example.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import example.proto.Echo;
import log.Log;
import log.LogCategory;

public class EchoServiceImpl extends Echo.EchoService {
    @Override
    public void echo(RpcController controller, Echo.EchoRequest request, RpcCallback<Echo.EchoResponse> done) {
        Log.info(LogCategory.Server, request.getMessage());
        Echo.EchoResponse.Builder builder = Echo.EchoResponse.newBuilder();
        builder.setMessage(request.getMessage());
        done.run(builder.build());
    }
}
