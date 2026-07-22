package model;

/**
 * Represents a gospel performance linking a singer to a song at a church.
 * @author D00276269
 */
public class Performance {

    private int performanceId;
    private int singerId;
    private int songId;
    private String churchName;
    private String performanceDate;

    // No-arg constructor required for Gson deserialisation
    public Performance() {
        this.singerId = 1;
        this.songId = 1;
        this.churchName = "";
        this.performanceDate = "";
    }

    // Constructor
    public Performance(int performanceId, int singerId, int songId, String churchName, String performanceDate) {
        setPerformanceId(performanceId);
        setSingerId(singerId);
        setSongId(songId);
        setChurchName(churchName);
        setPerformanceDate(performanceDate);
    }

    // Getters
    public int getPerformanceId() { return performanceId; }
    public int getSingerId() { return singerId; }
    public int getSongId() { return songId; }
    public String getChurchName() { return churchName; }
    public String getPerformanceDate() { return performanceDate; }

    // Setters with validation
    public void setPerformanceId(int performanceId) {
        if (performanceId < 0) throw new IllegalArgumentException("Performance ID cannot be negative");
        this.performanceId = performanceId;
    }

    public void setSingerId(int singerId) {
        if (singerId <= 0) throw new IllegalArgumentException("Singer ID must be greater than 0");
        this.singerId = singerId;
    }

    public void setSongId(int songId) {
        if (songId <= 0) throw new IllegalArgumentException("Song ID must be greater than 0");
        this.songId = songId;
    }

    public void setChurchName(String churchName) {
        if (churchName == null || churchName.trim().isEmpty()) throw new IllegalArgumentException("Church name cannot be blank");
        this.churchName = churchName.trim();
    }

    public void setPerformanceDate(String performanceDate) {
        if (performanceDate == null || performanceDate.trim().isEmpty()) throw new IllegalArgumentException("Performance date cannot be blank");
        this.performanceDate = performanceDate.trim();
    }

    @Override
    public String toString() {
        return "Performance{" +
                "performanceId=" + performanceId +
                ", singerId=" + singerId +
                ", songId=" + songId +
                ", churchName='" + churchName + '\'' +
                ", performanceDate='" + performanceDate + '\'' +
                '}';
    }
}