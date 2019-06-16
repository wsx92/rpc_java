package example.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import log.Log;
import log.LogCategory;
import example.proto.Login;

public class LoginServiceImpl extends Login.LoginService {
    @Override
    public void login(RpcController controller, Login.LoginRequest request, RpcCallback<Login.LoginResponse> done) {
        Log.info(LogCategory.Client, request.getUser());
        Log.info(LogCategory.Client, request.getPassword());
        Login.LoginResponse.Builder builder = Login.LoginResponse.newBuilder();
        builder.setResult("success");
        done.run(builder.build());
    }
}
