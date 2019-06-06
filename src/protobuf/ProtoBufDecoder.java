package protobuf;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import protobuf.proto.Rpc;

public class ProtoBufDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        if(ioBuffer.remaining() < 4){
            return false;
        }
        else {
            ioBuffer.mark();
            int bodyLength = ioBuffer.getInt();
            if(ioBuffer.remaining() < bodyLength){
                ioBuffer.reset();
                return false;
            }
            else{
                byte[] bodyBytes = new byte[bodyLength];
                ioBuffer.get(bodyBytes);
                Rpc.RpcMeta meta = Rpc.RpcMeta.parseFrom(bodyBytes);
                protocolDecoderOutput.write(meta);
                return true;
            }
        }
    }
}
