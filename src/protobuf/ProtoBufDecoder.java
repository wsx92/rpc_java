package protobuf;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ProtoBufDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        if(ioBuffer.remaining() < 12){
            return false;
        }
        else {
            ioBuffer.mark();
            byte dummy1 = ioBuffer.get();
            byte dummy2 = ioBuffer.get();
            byte dummy3 = ioBuffer.get();
            byte dummy4 = ioBuffer.get();
            if(Integer.valueOf('P').byteValue() != dummy1 || Integer.valueOf('R').byteValue() != dummy2 || Integer.valueOf('P').byteValue() != dummy3 || Integer.valueOf('C').byteValue() != dummy4) {
                throw new RpcException("unsupported proto");
            }
            int bodyLength = ioBuffer.getInt();
            int metaLength = ioBuffer.getInt();
            if(metaLength > bodyLength) {
                throw new RpcException("metaLength=" + metaLength + " is bigger than bodyLength=" + bodyLength);
            }
            if(ioBuffer.remaining() < bodyLength){
                ioBuffer.reset();
                return false;
            }
            else{
                byte[] metaBytes = new byte[metaLength];
                byte[] payLoadBytes = new byte[bodyLength - metaLength];
                ioBuffer.get(metaBytes);
                ioBuffer.get(payLoadBytes);
                IoBuffer meta = IoBuffer.wrap(metaBytes);
                IoBuffer payLoad = IoBuffer.wrap(payLoadBytes);
                InputMessage message = new InputMessage(meta, payLoad);
                protocolDecoderOutput.write(message);
                return true;
            }
        }
    }
}
