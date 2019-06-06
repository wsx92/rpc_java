package protobuf;

import com.google.protobuf.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ProtoBufEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        Message message = (Message) o;
        byte[] bytes = message.toByteArray();
        int length = bytes.length;
        IoBuffer buffer = IoBuffer.allocate(length + 4);
        buffer.putInt(length);
        buffer.put(bytes);
        buffer.flip();
        protocolEncoderOutput.write(buffer);
    }
}
