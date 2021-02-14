package studios.ingot.databaseapi.model.database.handler;

import studios.ingot.databaseapi.DatabaseAPI;
import studios.ingot.databaseapi.interfaces.IDatabaseHandler;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Getter
public class MySQLDatabaseHandler implements IDatabaseHandler<ResultSet> {

    private Connection connection;

    public MySQLDatabaseHandler(String user, String password, String host, int port, String database) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/"+ database + "?autoReconnect=false", user , password);
            DatabaseAPI.getInstance().getServer().getLogger().info("Connected to MySQL!");
        } catch (SQLException troubles) {
            DatabaseAPI.getInstance().getServer().getLogger().warning("Could not connect to MYSQL ( "+ troubles.getMessage() +" )");
        }
    }

    /**
     * @param query declares data which is needed to insert a column
     * @param <K>   is a type parameter to declare what type of object we got here
     */
    @SafeVarargs
    @Override
    public final <K> void insertModel(K... query) {
        DatabaseAPI.getInstance().getDatabaseService().getExecutorService().execute(() -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement((String) query[0]);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param query declares data which is needed to get a resultset
     * @param <K>   is a type parameter to declare what type of object we got here
     * @return returns a resultset async
     */
    @SafeVarargs
    @Override
    public final <K> ResultSet getAsyncModel(K... query) {
        try {
            return DatabaseAPI.getInstance().getDatabaseService().getExecutorService().submit(() -> connection.prepareStatement(String.valueOf(query[0])).executeQuery()).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    /**
     * @param query declares data which is needed to get a list of resultsets
     * @param <K>   is a type parameter to declare what type of object we got here
     * @return returns a list of resultsets async
     */
    @SafeVarargs
    @Override
    public final <K> List<ResultSet> getAsyncModels(K... query) {
        try {
            return DatabaseAPI.getInstance().getDatabaseService().getExecutorService().submit(() -> {
                List<ResultSet> resultSets = new ArrayList<>();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement((String) query[0]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        resultSets.add(resultSet);
                    }
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return resultSets;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    /**
     * @param query declares data which is needed to update a column
     * @param <K>   is a type parameter to declare what type of object we got here
     */
    @SafeVarargs
    @Override
    public final <K> void updateModel(K... query) {
        DatabaseAPI.getInstance().getDatabaseService().getExecutorService().execute(() -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement((String) query[0]);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
