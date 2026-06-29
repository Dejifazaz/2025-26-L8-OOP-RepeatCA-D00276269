package dao;

import model.Song;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * JDBC implementation of Dao for the Song entity.
 * @author D00276269
 */
public class JdbcSongDao implements Dao<Song, Integer> {

    private final Connection connection;

    public JdbcSongDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

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
}