package ru.motrichkin.cloudstorage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.motrichkin.cloudstorage.utils.FileMessage;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FilesAndMessagesToBytesEncoder extends MessageToByteEncoder {

    private static byte[] ZERO = ByteBuffer.allocate(4).putInt(0).array();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectEncoderOutputStream oos = new ObjectEncoderOutputStream(bos);
        oos.writeObject(message);
        oos.flush();
        byte[] data = bos.toByteArray();
        byte[] size = ByteBuffer.allocate(4).putInt(data.length).array();
        out.writeBytes(size);
        if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            RandomAccessFile file = new RandomAccessFile(fileMessage.getOperatingFolder() + "/" + fileMessage.getFilename(), "r");
            file.seek(fileMessage.getPosition());
            byte[] fileSize = ByteBuffer.allocate(4).putInt((int) (fileMessage.getLength())).array();
            out.writeBytes(fileSize);
            out.writeBytes(data);
            for (int i = 0; i < fileMessage.getLength(); i++) {
                out.writeByte(file.readByte());
            }
        } else {
            out.writeBytes(ZERO);
            out.writeBytes(data);
        }
    }

}
