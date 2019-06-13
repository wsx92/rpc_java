package protobuf.impl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import protobuf.proto.Login;

public class LoginServiceImpl extends Login.LoginService {
    @Override
    public void login(RpcController controller, Login.LoginRequest request, RpcCallback<Login.LoginResponse> done) {
        System.out.println(request.getUser());
        System.out.println(request.getPassword());
        Login.LoginResponse.Builder builder = Login.LoginResponse.newBuilder();
        builder.setResult("success");
        done.run(builder.build());
    }
}
