package dao;

import model.Performance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Performance entity.
 * Handles all database operations for gospel performances using PreparedStatement.
 * @author D00276269
 */
public class JdbcPerformanceDao implements Dao<Performance, Integer> {

    private final Connection connection;

    /**
     * Constructs a JdbcPerformanceDao using the shared database connection.
     */
    public JdbcPerformanceDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Retrieves all performances from the database.
     * @return List of all Performance objects, empty list if none found
     */
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

    /**
     * Retrieves a performance by its ID.
     * @param id the performance_id to search for
     * @return Optional containing the Performance if found, empty Optional if not
     */
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

    /**
     * Inserts a new performance into the database and returns it with the generated ID.
     * @param entity the Performance to insert (performance_id is ignored)
     * @return the inserted Performance with the auto-generated performance_id populated
     * @throws RuntimeException if the insert fails or no ID is generated
     */
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

    /**
     * Updates an existing performance in the database.
     * @param id the performance_id of the record to update
     * @param entity the Performance containing the updated field values
     * @return the updated Performance object
     * @throws RuntimeException if the update fails
     */
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

    /**
     * Deletes a performance from the database by its ID.
     * @param id the performance_id of the record to delete
     * @return true if a record was deleted, false if no record matched the ID
     * @throws RuntimeException if the delete fails
     */
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

    /**
     * Returns all performances that match the given filter predicate.
     * @param filter a Predicate lambda used to test each Performance
     * @return List of Performances that pass the filter test
     */
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

    /**
     * Maps the current row of a ResultSet to a Performance object.
     * @param resultSet the ResultSet positioned at the current row
     * @return a Performance object built from the current row's column values
     * @throws SQLException if a column value cannot be retrieved
     */
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