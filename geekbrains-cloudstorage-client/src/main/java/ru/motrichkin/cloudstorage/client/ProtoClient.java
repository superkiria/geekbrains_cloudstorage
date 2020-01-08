package ru.motrichkin.cloudstorage.client;

public class ProtoClient {

    public static void main(String[] args) {
        Network.start();
        Interactions.authentificate("user1", "p1");
        Interactions.receiveFile("main.trace.db");
        Interactions.sendFile("main.mv.db");
        Interactions.authentificate("user2", "p2");
        Interactions.sendFile("main.mv.db");
        Interactions.removeFile("main.mv.db");
        Network.stop();
    }
}
