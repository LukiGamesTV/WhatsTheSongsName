package de.jerome.whatsthesongsname.spigot.manager;

import de.jerome.whatsthesongsname.spigot.WTSNMain;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class DatabaseManager {

    private static final ConfigManager configManager = WTSNMain.getInstance().getConfigManager();

    private Connection connection;
    private Statement statement;

    public DatabaseManager() {
        reload();
    }

    public void reload() {
        if (!configManager.isDatabaseEnable()) return;
        disconnect();
        connect();
        checkTables();
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + configManager.getDatabaseHost() + ":" + configManager.getDatabasePort() + "/" + configManager.getDatabaseDatabase(), configManager.getDatabaseUsername(), configManager.getDatabasePassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if (connection == null) return;
            connection.close();
            connection = null;
            statement = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkTables() {
        try {
            Statement statement = getStatement();
            if (statement == null) return;
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS wtsn_players (UUID VARCHAR (37), POINTS INT, GUESSED_CORRECTLY INT, GUESSED_WRONG INT, PLAYS INT,PRIMARY KEY (UUID))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable Connection getConnection() {
        try {
            if (configManager.isDatabaseEnable())
                if (connection == null || connection.isClosed() || !connection.isValid(2))
                    connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public @Nullable Statement getStatement() {
        try {
            Connection connection = getConnection();
            if (connection == null) return null;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return statement;
    }
}
