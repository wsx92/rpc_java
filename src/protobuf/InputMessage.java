package protobuf;

import org.apache.mina.core.buffer.IoBuffer;

public class InputMessage {

    private IoBuffer meta;

    private IoBuffer payload;

    public InputMessage(IoBuffer meta, IoBuffer payload) {
        this.meta = meta;
        this.payload = payload;
    }

    public IoBuffer getMeta() {
        return meta;
    }

    public void setMeta(IoBuffer meta) {
        this.meta = meta;
    }

    public IoBuffer getPayload() {
        return payload;
    }

    public void setPayload(IoBuffer payload) {
        this.payload = payload;
    }
}
