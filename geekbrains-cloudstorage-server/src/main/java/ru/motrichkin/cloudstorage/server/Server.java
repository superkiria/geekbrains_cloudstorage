package ru.motrichkin.cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.motrichkin.cloudstorage.utils.handlers.BytesToFilesAndMessagesDecoder;
import ru.motrichkin.cloudstorage.utils.handlers.FilesAndMessagesToBytesEncoder;

public class Server {
    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DatabaseServer.getDatabaseServer().connect();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new BytesToFilesAndMessagesDecoder(MessageProcessor.getOperatingFolder(), DatabaseServer.getDatabaseServer()),
                                    new FilesAndMessagesToBytesEncoder(),
                                    new MainHandler()
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(8189).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            DatabaseServer.getDatabaseServer().disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }

}
