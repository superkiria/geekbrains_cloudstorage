package ru.motrichkin.cloudstorage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import ru.motrichkin.cloudstorage.utils.FileMessage;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.util.List;

public class BytesToFilesAndMessagesDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 8) {
            in.markReaderIndex();
            int size_message = in.readInt();
            int size_file = in.readInt();
            System.out.println(size_message + " " + size_file);
            if (in.readableBytes() >= size_message + size_file) {
                byte[] data = new byte[size_message];
                in.readBytes(data);
                Object message = (new ObjectDecoderInputStream(new ByteArrayInputStream(data))).readObject();
                if (size_file > 0) {
                    FileMessage fileMessage = (FileMessage) message;
                    RandomAccessFile file = new RandomAccessFile("server_storage/" + DatabaseServer.getFolderNameForToken(fileMessage.getToken()) + "/" + fileMessage.getFilename(), "rw");
                    file.seek(fileMessage.getPosition());
                    for (int i = 0; i < size_file; i++) {
                        file.write(in.readByte());
                    }
                }
                out.add(message);
                in.discardReadBytes();
            } else {
                in.resetReaderIndex();
                return;
            }
        } else {
            return;
        }
    }
}
