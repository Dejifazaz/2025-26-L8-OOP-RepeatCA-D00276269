package dao;

import model.Singer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Singer entity.
 * @author D00276269
 */
public class JdbcSingerDao implements Dao<Singer, Integer> {

    private final Connection connection;

    public JdbcSingerDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Singer> getAll() {
        List<Singer> singers = new ArrayList<>();
        String sql = "SELECT * FROM singer";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Singer singer = mapRowToSinger(resultSet);
                singers.add(singer);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all singers", e);
        }

        return singers;
    }

    @Override
    public Optional<Singer> getById(Integer id) {
        String sql = "SELECT * FROM singer WHERE singer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToSinger(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving singer by id " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public Singer insert(Singer entity) {
        String sql = "INSERT INTO singer (name, vocal_type, home_church, year_active) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getVocalType());
            statement.setString(3, entity.getHomeChurch());
            statement.setInt(4, entity.getYearActive());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Singer(newId, entity.getName(), entity.getVocalType(),
                            entity.getHomeChurch(), entity.getYearActive());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting singer", e);
        }

        throw new RuntimeException("Insert failed — no ID generated");
    }

    @Override
    public Singer update(Integer id, Singer entity) {
        String sql = "UPDATE singer SET name = ?, vocal_type = ?, home_church = ?, year_active = ? WHERE singer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getVocalType());
            statement.setString(3, entity.getHomeChurch());
            statement.setInt(4, entity.getYearActive());
            statement.setInt(5, id);

            statement.executeUpdate();

            return new Singer(id, entity.getName(), entity.getVocalType(),
                    entity.getHomeChurch(), entity.getYearActive());

        } catch (SQLException e) {
            throw new RuntimeException("Error updating singer with id " + id, e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM singer WHERE singer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting singer with id " + id, e);
        }
    }

    @Override
    public List<Singer> findByFilter(Predicate<Singer> filter) {
        List<Singer> allSingers = getAll();
        List<Singer> filteredSingers = new ArrayList<>();

        for (Singer singer : allSingers) {
            if (filter.test(singer)) {
                filteredSingers.add(singer);
            }
        }

        return filteredSingers;
    }

    private Singer mapRowToSinger(ResultSet resultSet) throws SQLException {
        return new Singer(
                resultSet.getInt("singer_id"),
                resultSet.getString("name"),
                resultSet.getString("vocal_type"),
                resultSet.getString("home_church"),
                resultSet.getInt("year_active")
        );
    }
}