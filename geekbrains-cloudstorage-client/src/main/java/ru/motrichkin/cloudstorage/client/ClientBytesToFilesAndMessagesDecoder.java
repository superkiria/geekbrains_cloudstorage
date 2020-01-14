package ru.motrichkin.cloudstorage.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import ru.motrichkin.cloudstorage.utils.FileMessage;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ClientBytesToFilesAndMessagesDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 8) {
            in.markReaderIndex();
            int messageSize = in.readInt();
            int fileSize = in.readInt();
            if (in.readableBytes() >= messageSize + fileSize) {
                byte[] data = new byte[messageSize];
                in.readBytes(data);
                Object message = (new ObjectDecoderInputStream(new ByteArrayInputStream(data))).readObject();
                if (fileSize > 0) {
                    FileMessage fileMessage = (FileMessage) message;
                    String filePath = fileMessage.getFilename();
                    if (fileMessage.getPosition() == 0) {
                        Files.deleteIfExists(Paths.get(filePath));
                    }
                    RandomAccessFile file = new RandomAccessFile(filePath, "rw");
                    file.seek(fileMessage.getPosition());
                    for (int i = 0; i < fileSize; i++) { //как избавиться от цикла?
                        file.write(in.readByte());
                    }
                    file.close();
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

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
