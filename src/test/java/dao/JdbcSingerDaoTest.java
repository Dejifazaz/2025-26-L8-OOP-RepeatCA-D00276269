package dao;

import com.google.gson.Gson;
import exception.EntityNotFoundException;
import model.Singer;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for JdbcSingerDao.
 * @author D00276269
 */
class JdbcSingerDaoTest {

    private JdbcSingerDao singerDao;

    @BeforeEach
    void setUp() {
        singerDao = new JdbcSingerDao();
    }

    @Test
    void getAll_returnsList_withTenOrMoreSingers() {
        List<Singer> singers = singerDao.getAll();
        assertNotNull(singers);
        assertTrue(singers.size() >= 10);
    }

    @Test
    void getById_returnsSinger_whenIdExists() {
        Optional<Singer> result = singerDao.getById(1);
        assertTrue(result.isPresent());
        assertEquals("Theophilus Sunday", result.get().getName());
    }

    @Test
    void getById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Singer> result = singerDao.getById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void insert_returnsSinger_withGeneratedId() {
        Singer newSinger = new Singer(0, "Tasha Cobbs", "Soprano", "Jesup New Life Ministries", 2010);
        Singer inserted = singerDao.insert(newSinger);

        assertTrue(inserted.getSingerId() > 0);
        assertEquals("Tasha Cobbs", inserted.getName());

        singerDao.deleteById(inserted.getSingerId());
    }

    @Test
    void update_returnsUpdatedSinger_whenIdExists() {
        Singer newSinger = new Singer(0, "Travis Greene", "Tenor", "Forward City Church", 2008);
        Singer inserted = singerDao.insert(newSinger);

        Singer updatedSinger = new Singer(0, "Travis Greene", "Baritone", "Forward City Church", 2008);
        Singer result = singerDao.update(inserted.getSingerId(), updatedSinger);

        assertEquals("Baritone", result.getVocalType());
        assertEquals(inserted.getSingerId(), result.getSingerId());

        singerDao.deleteById(result.getSingerId());
    }

    @Test
    void deleteById_returnsTrue_whenSingerExists() {
        Singer newSinger = new Singer(0, "Marvin Sapp", "Baritone", "Lighthouse Full Life Center", 1996);
        Singer inserted = singerDao.insert(newSinger);

        boolean result = singerDao.deleteById(inserted.getSingerId());
        assertTrue(result);

        Optional<Singer> deleted = singerDao.getById(inserted.getSingerId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteById_returnsFalse_whenSingerDoesNotExist() {
        boolean result = singerDao.deleteById(999);
        assertFalse(result);
    }

    @Test
    void findByFilter_returnsSingers_withVocalTypeSoprano() {
        List<Singer> result = singerDao.findByFilter(singer -> singer.getVocalType().equals("Soprano"));
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (Singer singer : result) {
            assertEquals("Soprano", singer.getVocalType());
        }
    }

    @Test
    void jsonRoundTrip_singer_serialisesAndDeserialises() {
        Gson gson = new Gson();
        Singer original = new Singer(1, "Theophilus Sunday", "Tenor", "Daystar Christian Centre Lagos", 2010);

        String json = gson.toJson(original);
        Singer result = gson.fromJson(json, Singer.class);

        assertEquals(original.getSingerId(), result.getSingerId());
        assertEquals(original.getName(), result.getName());
        assertEquals(original.getVocalType(), result.getVocalType());
        assertEquals(original.getHomeChurch(), result.getHomeChurch());
        assertEquals(original.getYearActive(), result.getYearActive());
    }

    @Test
    void entityNotFoundException_throwsCorrectly_whenSingerNotFound() {
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> singerDao.getById(999)
                        .orElseThrow(() -> new EntityNotFoundException("Singer", 999))
        );

        assertEquals("Singer not found for id 999", exception.getMessage());
        assertEquals(999, exception.getEntityId());
        assertEquals("Singer", exception.getEntityType());
    }
}