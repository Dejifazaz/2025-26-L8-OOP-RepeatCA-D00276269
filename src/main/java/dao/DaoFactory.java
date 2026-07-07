package dao;

import model.Performance;
import model.Singer;
import model.Song;

/**
 * Factory class responsible for creating DAO instances.
 * Decouples the rest of the application from specific JDBC implementations.
 * @author D00276269
 */
public class DaoFactory {

    // Private constructor prevents instantiation — this is a utility class
    private DaoFactory() {}

    /**
     * Returns a DAO for Song operations.
     * @return Dao implementation for Song
     */
    public static Dao<Song, Integer> getSongDao() {
        return new JdbcSongDao();
    }

    /**
     * Returns a DAO for Singer operations.
     * @return Dao implementation for Singer
     */
    public static Dao<Singer, Integer> getSingerDao() {
        return new JdbcSingerDao();
    }

    /**
     * Returns a DAO for Performance operations.
     * @return Dao implementation for Performance
     */
    public static Dao<Performance, Integer> getPerformanceDao() {
        return new JdbcPerformanceDao();
    }
}