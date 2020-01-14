package ru.motrichkin.cloudstorage.server;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Random;

public class DatabaseServer {
    private static Connection connection;
    private static Statement statement;

    private static final String DB_URL = "jdbc:h2:./main";
    private static final String DB_Driver = "org.h2.Driver";

    private static Random random = new Random();

    public static void connect() throws SQLException {
        try {
            Class.forName(DB_Driver);
            connection = DriverManager.getConnection(DB_URL, "sa", "");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getToken(String login, String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
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

    public static String generateToken() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return getHash(String.valueOf(random.nextInt()) + random.nextInt() + random.nextInt());
    }

    public static String getHash(String value) throws NoSuchAlgorithmException, InvalidKeySpecException {
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

    public static String getFolderNameForToken(String token) throws SQLException {
        String query =
                String.format("SELECT LOGIN FROM USERS INNER JOIN TOKENS ON USERS.ID = TOKENS.USER_ID WHERE TOKEN = '%s'", token);
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

}
