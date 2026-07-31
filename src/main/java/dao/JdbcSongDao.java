package dao;

import model.Song;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Song entity.
 * Handles all database operations for gospel songs using PreparedStatement.
 * @author D00276269
 */
public class JdbcSongDao implements Dao<Song, Integer> {

    private final Connection connection;

    /**
     * Constructs a JdbcSongDao using the shared database connection.
     */
    public JdbcSongDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Retrieves all songs from the database.
     * @return List of all Song objects, empty list if none found
     */
    @Override
    public List<Song> getAll() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM song";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Song song = mapRowToSong(resultSet);
                songs.add(song);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all songs", e);
        }

        return songs;
    }

    /**
     * Retrieves a song by its ID.
     * @param id the song_id to search for
     * @return Optional containing the Song if found, empty Optional if not
     */
    @Override
    public Optional<Song> getById(Integer id) {
        String sql = "SELECT * FROM song WHERE song_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToSong(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving song by id " + id, e);
        }

        return Optional.empty();
    }

    /**
     * Inserts a new song into the database and returns it with the generated ID.
     * @param entity the Song to insert (song_id is ignored)
     * @return the inserted Song with the auto-generated song_id populated
     * @throws RuntimeException if the insert fails or no ID is generated
     */
    @Override
    public Song insert(Song entity) {
        String sql = "INSERT INTO song (title, artist, album, year_released, duration_seconds, bpm) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getArtist());
            statement.setString(3, entity.getAlbum());
            statement.setInt(4, entity.getYearReleased());
            statement.setInt(5, entity.getDurationSeconds());
            statement.setDouble(6, entity.getBpm());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Song(newId, entity.getTitle(), entity.getArtist(), entity.getAlbum(),
                            entity.getYearReleased(), entity.getDurationSeconds(), entity.getBpm());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting song", e);
        }

        throw new RuntimeException("Insert failed — no ID generated");
    }

    /**
     * Updates an existing song in the database.
     * @param id the song_id of the record to update
     * @param entity the Song containing the updated field values
     * @return the updated Song object
     * @throws RuntimeException if the update fails
     */
    @Override
    public Song update(Integer id, Song entity) {
        String sql = "UPDATE song SET title = ?, artist = ?, album = ?, year_released = ?, duration_seconds = ?, bpm = ? WHERE song_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getArtist());
            statement.setString(3, entity.getAlbum());
            statement.setInt(4, entity.getYearReleased());
            statement.setInt(5, entity.getDurationSeconds());
            statement.setDouble(6, entity.getBpm());
            statement.setInt(7, id);

            statement.executeUpdate();

            return new Song(id, entity.getTitle(), entity.getArtist(), entity.getAlbum(),
                    entity.getYearReleased(), entity.getDurationSeconds(), entity.getBpm());

        } catch (SQLException e) {
            throw new RuntimeException("Error updating song with id " + id, e);
        }
    }

    /**
     * Deletes a song from the database by its ID.
     * @param id the song_id of the record to delete
     * @return true if a record was deleted, false if no record matched the ID
     * @throws RuntimeException if the delete fails
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM song WHERE song_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting song with id " + id, e);
        }
    }

    /**
     * Returns all songs that match the given filter predicate.
     * @param filter a Predicate lambda used to test each Song
     * @return List of Songs that pass the filter test
     */
    @Override
    public List<Song> findByFilter(Predicate<Song> filter) {
        List<Song> allSongs = getAll();
        List<Song> filteredSongs = new ArrayList<>();

        for (Song song : allSongs) {
            if (filter.test(song)) {
                filteredSongs.add(song);
            }
        }

        return filteredSongs;
    }

    /**
     * Maps the current row of a ResultSet to a Song object.
     * @param resultSet the ResultSet positioned at the current row
     * @return a Song object built from the current row's column values
     * @throws SQLException if a column value cannot be retrieved
     */
    private Song mapRowToSong(ResultSet resultSet) throws SQLException {
        return new Song(
                resultSet.getInt("song_id"),
                resultSet.getString("title"),
                resultSet.getString("artist"),
                resultSet.getString("album"),
                resultSet.getInt("year_released"),
                resultSet.getInt("duration_seconds"),
                resultSet.getDouble("bpm")
        );
    }
}