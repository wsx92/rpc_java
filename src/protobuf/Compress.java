package protobuf;

import com.google.protobuf.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Compress {
    NONE(0),
    SNAPPY(1),
    GZIP(2),
    ZLIB(3),
    LZ4(4);

    private int compressType;

    Compress(int compressType) {
        this.compressType = compressType;
    }

    public static byte[] serializeAsCompressedData(int compressType, Message message) throws Exception {
        if (compressType == NONE.compressType) {
            return compressNONE(message);
        }
        if (compressType == GZIP.compressType) {
            return compressGZIP(message);
        }
        throw new RpcException("unknown compress type");
    }

    public static Message parseFromCompressedData(int compressType, Message.Builder builder, byte[] data) throws Exception {
        if (compressType == NONE.compressType) {
            return uncompressNONE(builder, data);
        }
        if (compressType == GZIP.compressType) {
            return uncompressGZIP(builder, data);
        }
        throw new RpcException("unknown compress type");
    }

    public static byte[] compressNONE(Message message) throws Exception {
        return message.toByteArray();
    }

    public static Message uncompressNONE(Message.Builder builder, byte[] data) throws Exception {
        return builder.mergeFrom(data).build();
    }

    public static byte[] compressGZIP(Message message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(message.toByteArray());
        gzip.close();
        return out.toByteArray();
    }

    public static Message uncompressGZIP(Message.Builder builder, byte[] data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        GZIPInputStream unGzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = unGzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return builder.mergeFrom(out.toByteArray()).build();
    }
}
