package dao;

import model.Singer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Singer entity.
 * Handles all database operations for gospel singers using PreparedStatement.
 * @author D00276269
 */
public class JdbcSingerDao implements Dao<Singer, Integer> {

    private final Connection connection;

    /**
     * Constructs a JdbcSingerDao using the shared database connection.
     */
    public JdbcSingerDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Retrieves all singers from the database.
     * @return List of all Singer objects, empty list if none found
     */
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

    /**
     * Retrieves a singer by their ID.
     * @param id the singer_id to search for
     * @return Optional containing the Singer if found, empty Optional if not
     */
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

    /**
     * Inserts a new singer into the database and returns them with the generated ID.
     * @param entity the Singer to insert (singer_id is ignored)
     * @return the inserted Singer with the auto-generated singer_id populated
     * @throws RuntimeException if the insert fails or no ID is generated
     */
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

    /**
     * Updates an existing singer in the database.
     * @param id the singer_id of the record to update
     * @param entity the Singer containing the updated field values
     * @return the updated Singer object
     * @throws RuntimeException if the update fails
     */
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

    /**
     * Deletes a singer from the database by their ID.
     * @param id the singer_id of the record to delete
     * @return true if a record was deleted, false if no record matched the ID
     * @throws RuntimeException if the delete fails
     */
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

    /**
     * Returns all singers that match the given filter predicate.
     * @param filter a Predicate lambda used to test each Singer
     * @return List of Singers that pass the filter test
     */
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

    /**
     * Maps the current row of a ResultSet to a Singer object.
     * @param resultSet the ResultSet positioned at the current row
     * @return a Singer object built from the current row's column values
     * @throws SQLException if a column value cannot be retrieved
     */
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