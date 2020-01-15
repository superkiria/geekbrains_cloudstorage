package ru.motrichkin.cloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.motrichkin.cloudstorage.utils.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequestMessage) {
                // не нашёл, как обрабатывать это сообщение вне хэндлера, так как нужна возможность отправлять в ответ много сообщений
                FileRequestMessage message = (FileRequestMessage) msg;
                String operatingFolder = "server_storage/" + DatabaseServer.getFolderNameForToken(message.getToken());
                String fileName = operatingFolder + "/" + message.getFilename();
                if (Files.exists(Paths.get(fileName))) {
                    RandomAccessFile file = null;
                    try {
                        file = new RandomAccessFile(fileName, "r");
                        FileMessage fileMessage;
                        long pos = 0;
                        while (pos < file.length()) {
                            long increment = Math.min(256, file.length() - pos);
                            fileMessage = new FileMessage(Paths.get(fileName), pos, increment, file.length(), operatingFolder);
                            channelHandlerContext.writeAndFlush(fileMessage);
                            pos += increment;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    channelHandlerContext.writeAndFlush(new LogMessage("No such file found"));
                }
            } else {
                channelHandlerContext.writeAndFlush(MessageProcessor.process((AbstractMessage) msg));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}