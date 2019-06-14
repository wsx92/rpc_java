package example.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import org.slf4j.LoggerFactory;
import example.proto.Login;

public class LoginServiceImpl extends Login.LoginService {
    @Override
    public void login(RpcController controller, Login.LoginRequest request, RpcCallback<Login.LoginResponse> done) {
        LoggerFactory.getLogger(LoginServiceImpl.class).info(request.getUser());
        LoggerFactory.getLogger(LoginServiceImpl.class).info(request.getPassword());
        Login.LoginResponse.Builder builder = Login.LoginResponse.newBuilder();
        builder.setResult("success");
        done.run(builder.build());
    }
}
