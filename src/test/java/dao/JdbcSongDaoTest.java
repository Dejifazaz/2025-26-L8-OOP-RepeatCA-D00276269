package dao;

import model.Song;
import org.junit.jupiter.api.*;
import com.google.gson.Gson;
import exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for JdbcSongDao.
 * @author D00276269
 */
class JdbcSongDaoTest {

    private JdbcSongDao songDao;

    @BeforeEach
    void setUp() {
        songDao = new JdbcSongDao();
    }
    @Test
    void getAll_returnsList_withTenOrMoreSongs() {
        List<Song> songs = songDao.getAll();
        assertNotNull(songs);
        assertTrue(songs.size() >= 10);
    }
    @Test
    void getById_returnsSong_whenIdExists() {
        Optional<Song> result = songDao.getById(1);
        assertTrue(result.isPresent());
        assertEquals("Warrior", result.get().getTitle());
    }

    @Test
    void getById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Song> result = songDao.getById(999);
        assertFalse(result.isPresent());
    }
    @Test
    void insert_returnsSong_withGeneratedId() {
        Song newSong = new Song(0, "Way Maker", "Sinach", "Way Maker", 2016, 300, 75.0);
        Song inserted = songDao.insert(newSong);

        assertTrue(inserted.getSongId() > 0);
        assertEquals("Way Maker", inserted.getTitle());
        assertEquals("Sinach", inserted.getArtist());

        // Clean up — delete the inserted song so it doesn't affect other tests
        songDao.deleteById(inserted.getSongId());
    }
    @Test
    void update_returnsUpdatedSong_whenIdExists() {
        // First insert a song to update
        Song newSong = new Song(0, "Alpha", "Elevation Worship", "Graves Into Gardens", 2020, 300, 75.0);
        Song inserted = songDao.insert(newSong);

        // Now update it
        Song updatedSong = new Song(0, "Alpha Omega", "Elevation Worship", "Graves Into Gardens", 2020, 300, 75.0);
        Song result = songDao.update(inserted.getSongId(), updatedSong);

        assertEquals("Alpha Omega", result.getTitle());
        assertEquals(inserted.getSongId(), result.getSongId());

        // Clean up
        songDao.deleteById(result.getSongId());
    }
    @Test
    void deleteById_returnsTrue_whenSongExists() {
        // Insert a song to delete
        Song newSong = new Song(0, "Oceans", "Hillsong United", "Zion", 2013, 348, 68.0);
        Song inserted = songDao.insert(newSong);

        // Delete it
        boolean result = songDao.deleteById(inserted.getSongId());
        assertTrue(result);

        // Confirm it's gone
        Optional<Song> deleted = songDao.getById(inserted.getSongId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteById_returnsFalse_whenSongDoesNotExist() {
        boolean result = songDao.deleteById(999);
        assertFalse(result);
    }
    @Test
    void findByFilter_returnsSongs_withBpmOver80() {
        List<Song> result = songDao.findByFilter(song -> song.getBpm() > 80);
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (Song song : result) {
            assertTrue(song.getBpm() > 80);
        }
    }

    @Test
    void findByFilter_returnsSongs_byArtist() {
        List<Song> result = songDao.findByFilter(song -> song.getArtist().equals("Elevation Worship"));
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (Song song : result) {
            assertEquals("Elevation Worship", song.getArtist());
        }
    }
    @Test
    void jsonRoundTrip_song_serialisesAndDeserialises() {
        Gson gson = new Gson();
        Song original = new Song(1, "Warrior", "Theophilus Sunday", "The Fathers House", 2018, 312, 78.0);

        // Convert to JSON
        String json = gson.toJson(original);

        // Convert back to Song
        Song result = gson.fromJson(json, Song.class);

        assertEquals(original.getSongId(), result.getSongId());
        assertEquals(original.getTitle(), result.getTitle());
        assertEquals(original.getArtist(), result.getArtist());
        assertEquals(original.getAlbum(), result.getAlbum());
        assertEquals(original.getYearReleased(), result.getYearReleased());
        assertEquals(original.getDurationSeconds(), result.getDurationSeconds());
        assertEquals(original.getBpm(), result.getBpm());
    }

    @Test
    void entityNotFoundException_throwsCorrectly_whenSongNotFound() {
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> songDao.getById(999)
                        .orElseThrow(() -> new EntityNotFoundException("Song", 999))
        );

        assertEquals("Song not found for id 999", exception.getMessage());
        assertEquals(999, exception.getEntityId());
        assertEquals("Song", exception.getEntityType());
    }
}