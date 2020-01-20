package ru.motrichkin.cloudstorage.server;

import ru.motrichkin.cloudstorage.utils.AuthMaker;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Random;

public class DatabaseServer implements AuthMaker {
    private static DatabaseServer databaseServer = null;

    private Connection connection;
    private Statement statement;
    private Random random = new Random();

    private final String DB_URL = "jdbc:h2:./main";
    private final String DB_Driver = "org.h2.Driver";

    private DatabaseServer() {
    }

    public static DatabaseServer getDatabaseServer() {
        if (databaseServer == null) {
            databaseServer = new DatabaseServer();
        }
        return databaseServer;
    }

    public void connect() throws SQLException {
        try {
            Class.forName(DB_Driver);
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getToken(String login, String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        String query = String.format("SELECT ID FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            int user_id = resultSet.getInt(1);
            query = String.format("SELECT TOKEN FROM TOKENS WHERE USER_ID = '%s'", user_id);
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                String token = generateToken();
                query = String.format("INSERT INTO TOKENS (USER_ID, TOKEN) VALUES ('%s', '%s')", user_id, token);
                int result = statement.executeUpdate(query);
                if (result > 0) {
                    return token;
                }
            }
        }
        return null;
    }

    public String generateToken() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return getHash(String.valueOf(random.nextInt()) + random.nextInt() + random.nextInt());
    }

    public String getHash(String value) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] salt = {Byte.parseByte("0"), Byte.parseByte("1")};
        KeySpec keyspec = new PBEKeySpec(value.toCharArray(), salt, 1000, 128);
        Key key = factory.generateSecret(keyspec);
        byte[] byteKey = key.getEncoded();
        String hash = "";
        for(byte b : byteKey) {
            hash = hash + Integer.toHexString(b + 128);
        }
        return hash;
    }

    public String getFolderNameForToken(String token) {
        String query =
                String.format("SELECT LOGIN FROM USERS INNER JOIN TOKENS ON USERS.ID = TOKENS.USER_ID WHERE TOKEN = '%s'", token);
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
