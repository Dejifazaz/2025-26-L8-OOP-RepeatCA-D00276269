package model;

/**
 * Represents a gospel song in the catalogue.
 * @author D00276269
 */
public class Song {

    private int songId;
    private String title;
    private String artist;
    private String album;
    private int yearReleased;
    private int durationSeconds;
    private double bpm;

    // Constructor
    public Song(int songId, String title, String artist, String album, int yearReleased, int durationSeconds, double bpm) {
        setSongId(songId);
        setTitle(title);
        setArtist(artist);
        setAlbum(album);
        setYearReleased(yearReleased);
        setDurationSeconds(durationSeconds);
        setBpm(bpm);
    }

    // Getters
    public int getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getYearReleased() { return yearReleased; }
    public int getDurationSeconds() { return durationSeconds; }
    public double getBpm() { return bpm; }

    // Setters with validation
    public void setSongId(int songId) {
        if (songId < 0) throw new IllegalArgumentException("Song ID cannot be negative");
        this.songId = songId;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be blank");
        this.title = title.trim();
    }

    public void setArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) throw new IllegalArgumentException("Artist cannot be blank");
        this.artist = artist.trim();
    }

    public void setAlbum(String album) {
        if (album == null || album.trim().isEmpty()) throw new IllegalArgumentException("Album cannot be blank");
        this.album = album.trim();
    }

    public void setYearReleased(int yearReleased) {
        if (yearReleased < 1800 || yearReleased > 2100) throw new IllegalArgumentException("Year must be between 1800 and 2100");
        this.yearReleased = yearReleased;
    }

    public void setDurationSeconds(int durationSeconds) {
        if (durationSeconds <= 0) throw new IllegalArgumentException("Duration must be greater than 0");
        this.durationSeconds = durationSeconds;
    }

    public void setBpm(double bpm) {
        if (bpm <= 0) throw new IllegalArgumentException("BPM must be greater than 0");
        this.bpm = bpm;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", yearReleased=" + yearReleased +
                ", durationSeconds=" + durationSeconds +
                ", bpm=" + bpm +
                '}';
    }
}