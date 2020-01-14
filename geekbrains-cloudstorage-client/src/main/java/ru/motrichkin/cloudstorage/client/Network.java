package ru.motrichkin.cloudstorage.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.motrichkin.cloudstorage.utils.AbstractMessage;
import ru.motrichkin.cloudstorage.utils.FileMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Network {
    private static Socket socket;
    private static OutputStream out;
    private static ObjectDecoderInputStream in;
    private static String token;
    private static byte[] ZERO = ByteBuffer.allocate(4).putInt(0).array();

    public static void start() {
        try {
            socket = new Socket("localhost", 8189);
            out = socket.getOutputStream();
            in = new ObjectDecoderInputStream(socket.getInputStream(), 5 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMessage(AbstractMessage message) {
        try {
            message.setToken(token);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectEncoderOutputStream oos = new ObjectEncoderOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte[] data = bos.toByteArray();
            byte[] size = ByteBuffer.allocate(4).putInt(data.length).array();
            out.write(size);
            if (message instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) message;
                RandomAccessFile file = new RandomAccessFile(fileMessage.getFilename(), "r");
                file.seek(fileMessage.getPosition());
                byte[] size_file = ByteBuffer.allocate(4).putInt((fileMessage.getLength())).array();
                out.write(size_file);
                out.write(data);
                for (int i = 0; i < fileMessage.getLength(); i++) {
                    out.write(file.readByte());
                }
            } else {
                out.write(ZERO);
                out.write(data);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object object = in.readObject();
        return (AbstractMessage) object;
    }

    public static void setToken(String tokenToBeSet) {
        token = tokenToBeSet;
    }

}