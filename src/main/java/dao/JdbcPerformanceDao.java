package dao;

import model.Performance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Performance entity.
 * @author D00276269
 */
public class JdbcPerformanceDao implements Dao<Performance, Integer> {

    private final Connection connection;

    public JdbcPerformanceDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Performance> getAll() {
        List<Performance> performances = new ArrayList<>();
        String sql = "SELECT * FROM performance";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Performance performance = mapRowToPerformance(resultSet);
                performances.add(performance);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all performances", e);
        }

        return performances;
    }

    @Override
    public Optional<Performance> getById(Integer id) {
        String sql = "SELECT * FROM performance WHERE performance_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToPerformance(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving performance by id " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public Performance insert(Performance entity) {
        String sql = "INSERT INTO performance (singer_id, song_id, church_name, performance_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, entity.getSingerId());
            statement.setInt(2, entity.getSongId());
            statement.setString(3, entity.getChurchName());
            statement.setString(4, entity.getPerformanceDate());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Performance(newId, entity.getSingerId(), entity.getSongId(),
                            entity.getChurchName(), entity.getPerformanceDate());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting performance", e);
        }

        throw new RuntimeException("Insert failed — no ID generated");
    }

    @Override
    public Performance update(Integer id, Performance entity) {
        String sql = "UPDATE performance SET singer_id = ?, song_id = ?, church_name = ?, performance_date = ? WHERE performance_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, entity.getSingerId());
            statement.setInt(2, entity.getSongId());
            statement.setString(3, entity.getChurchName());
            statement.setString(4, entity.getPerformanceDate());
            statement.setInt(5, id);

            statement.executeUpdate();

            return new Performance(id, entity.getSingerId(), entity.getSongId(),
                    entity.getChurchName(), entity.getPerformanceDate());

        } catch (SQLException e) {
            throw new RuntimeException("Error updating performance with id " + id, e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM performance WHERE performance_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting performance with id " + id, e);
        }
    }

    @Override
    public List<Performance> findByFilter(Predicate<Performance> filter) {
        List<Performance> allPerformances = getAll();
        List<Performance> filteredPerformances = new ArrayList<>();

        for (Performance performance : allPerformances) {
            if (filter.test(performance)) {
                filteredPerformances.add(performance);
            }
        }

        return filteredPerformances;
    }

    private Performance mapRowToPerformance(ResultSet resultSet) throws SQLException {
        return new Performance(
                resultSet.getInt("performance_id"),
                resultSet.getInt("singer_id"),
                resultSet.getInt("song_id"),
                resultSet.getString("church_name"),
                resultSet.getString("performance_date")
        );
    }
}