package protobuf;

public class RpcException extends Exception {

    RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
