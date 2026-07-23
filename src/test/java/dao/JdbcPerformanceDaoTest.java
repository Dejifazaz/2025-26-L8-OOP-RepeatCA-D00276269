package dao;

import com.google.gson.Gson;
import exception.EntityNotFoundException;
import model.Performance;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for JdbcPerformanceDao.
 * @author D00276269
 */
class JdbcPerformanceDaoTest {

    private JdbcPerformanceDao performanceDao;

    @BeforeEach
    void setUp() {
        performanceDao = new JdbcPerformanceDao();
    }

    @Test
    void getAll_returnsList_withTenOrMorePerformances() {
        List<Performance> performances = performanceDao.getAll();
        assertNotNull(performances);
        assertTrue(performances.size() >= 10);
    }

    @Test
    void getById_returnsPerformance_whenIdExists() {
        Optional<Performance> result = performanceDao.getById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getSingerId());
    }

    @Test
    void getById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Performance> result = performanceDao.getById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void insert_returnsPerformance_withGeneratedId() {
        Performance newPerformance = new Performance(0, 1, 1, "Test Church", "2024-01-01");
        Performance inserted = performanceDao.insert(newPerformance);

        assertTrue(inserted.getPerformanceId() > 0);
        assertEquals("Test Church", inserted.getChurchName());

        performanceDao.deleteById(inserted.getPerformanceId());
    }

    @Test
    void update_returnsUpdatedPerformance_whenIdExists() {
        Performance newPerformance = new Performance(0, 1, 1, "Old Church", "2024-01-01");
        Performance inserted = performanceDao.insert(newPerformance);

        Performance updatedPerformance = new Performance(0, 1, 1, "New Church", "2024-01-01");
        Performance result = performanceDao.update(inserted.getPerformanceId(), updatedPerformance);

        assertEquals("New Church", result.getChurchName());
        assertEquals(inserted.getPerformanceId(), result.getPerformanceId());

        performanceDao.deleteById(result.getPerformanceId());
    }

    @Test
    void deleteById_returnsTrue_whenPerformanceExists() {
        Performance newPerformance = new Performance(0, 1, 1, "Test Church", "2024-01-01");
        Performance inserted = performanceDao.insert(newPerformance);

        boolean result = performanceDao.deleteById(inserted.getPerformanceId());
        assertTrue(result);

        Optional<Performance> deleted = performanceDao.getById(inserted.getPerformanceId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteById_returnsFalse_whenPerformanceDoesNotExist() {
        boolean result = performanceDao.deleteById(999);
        assertFalse(result);
    }

    @Test
    void findByFilter_returnsPerformances_byChurchName() {
        List<Performance> result = performanceDao.findByFilter(
                performance -> performance.getChurchName().contains("Lagos")
        );
        assertNotNull(result);
        assertTrue(result.size() > 0);
        for (Performance performance : result) {
            assertTrue(performance.getChurchName().contains("Lagos"));
        }
    }

    @Test
    void jsonRoundTrip_performance_serialisesAndDeserialises() {
        Gson gson = new Gson();
        Performance original = new Performance(1, 1, 1, "Daystar Christian Centre Lagos", "2023-01-15");

        String json = gson.toJson(original);
        Performance result = gson.fromJson(json, Performance.class);

        assertEquals(original.getPerformanceId(), result.getPerformanceId());
        assertEquals(original.getSingerId(), result.getSingerId());
        assertEquals(original.getSongId(), result.getSongId());
        assertEquals(original.getChurchName(), result.getChurchName());
        assertEquals(original.getPerformanceDate(), result.getPerformanceDate());
    }

    @Test
    void entityNotFoundException_throwsCorrectly_whenPerformanceNotFound() {
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> performanceDao.getById(999)
                        .orElseThrow(() -> new EntityNotFoundException("Performance", 999))
        );

        assertEquals("Performance not found for id 999", exception.getMessage());
        assertEquals(999, exception.getEntityId());
        assertEquals("Performance", exception.getEntityType());
    }
}