package net.saikatsune.uhc.manager;

import net.saikatsune.uhc.Game;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {

    private Game game = Game.getInstance();

    private Connection connection;

    private String host = game.getConfig().getString("MYSQL.HOST"),
                   database = game.getConfig().getString("MYSQL.DATABASE"),
                   username = game.getConfig().getString("MYSQL.USERNAME"),
                   password = game.getConfig().getString("MYSQL.PASSWORD");

    private int port = game.getConfig().getInt("MYSQL.PORT");

    public void connectToDatabase() throws ClassNotFoundException, SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" +
                    this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
        }
    }

    public void disconnectFromDatabase() throws SQLException {
        if(!connection.isClosed()) connection.close();
    }

    public void createTable() throws SQLException {
        Statement statement = connection.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS STATS(USERNAME VARCHAR(100), UUID VARCHAR(100), " +
                "KILLS VARCHAR(100), DEATHS VARCHAR(100), WINS VARCHAR(100))");
    }

    public boolean isPlayerRegistered(OfflinePlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void registerPlayer(Player player) {
        try {
            if(!this.isPlayerRegistered(player)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO STATS(USERNAME, UUID, KILLS, DEATHS, WINS) VALUE (?,?,?,?,?)");
                statement.setString(1, player.getName());
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);

                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getKills(OfflinePlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");

            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("KILLS");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getDeaths(OfflinePlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");

            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("DEATHS");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getWins(OfflinePlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");

            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("WINS");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void addWins(OfflinePlayer player, int wins) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE STATS SET WINS=? WHERE UUID=?");

            statement.setInt(1, getWins(player) + wins);
            statement.setString(2, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addKills(Player player, int kills) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE STATS SET KILLS=? WHERE UUID=?");

            statement.setInt(1, getKills(player) + kills);
            statement.setString(2, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addDeaths(OfflinePlayer player, int deaths) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE STATS SET DEATHS=? WHERE UUID=?");

            statement.setInt(1, getDeaths(player) + deaths);
            statement.setString(2, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removeDeaths(Player player, int deaths) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE STATS SET DEATHS=? WHERE UUID=?");

            statement.setInt(1, getDeaths(player) - deaths);
            statement.setString(2, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
