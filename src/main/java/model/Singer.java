package model;

/**
 * Represents a gospel singer in the catalogue.
 * @author D00276269
 */
public class Singer {

    private int singerId;
    private String name;
    private String vocalType;
    private String homeChurch;
    private int yearActive;

    // No-arg constructor required for Gson deserialisation
    public Singer() {
        this.name = "";
        this.vocalType = "";
        this.homeChurch = "";
        this.yearActive = 1800;
    }

    // Constructor
    public Singer(int singerId, String name, String vocalType, String homeChurch, int yearActive) {
        setSingerId(singerId);
        setName(name);
        setVocalType(vocalType);
        setHomeChurch(homeChurch);
        setYearActive(yearActive);
    }

    // Getters
    public int getSingerId() { return singerId; }
    public String getName() { return name; }
    public String getVocalType() { return vocalType; }
    public String getHomeChurch() { return homeChurch; }
    public int getYearActive() { return yearActive; }

    // Setters with validation
    public void setSingerId(int singerId) {
        if (singerId < 0) throw new IllegalArgumentException("Singer ID cannot be negative");
        this.singerId = singerId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be blank");
        this.name = name.trim();
    }

    public void setVocalType(String vocalType) {
        if (vocalType == null || vocalType.trim().isEmpty()) throw new IllegalArgumentException("Vocal type cannot be blank");
        this.vocalType = vocalType.trim();
    }

    public void setHomeChurch(String homeChurch) {
        if (homeChurch == null || homeChurch.trim().isEmpty()) throw new IllegalArgumentException("Home church cannot be blank");
        this.homeChurch = homeChurch.trim();
    }

    public void setYearActive(int yearActive) {
        if (yearActive < 1800 || yearActive > 2100) throw new IllegalArgumentException("Year active must be between 1800 and 2100");
        this.yearActive = yearActive;
    }

    @Override
    public String toString() {
        return "Singer{" +
                "singerId=" + singerId +
                ", name='" + name + '\'' +
                ", vocalType='" + vocalType + '\'' +
                ", homeChurch='" + homeChurch + '\'' +
                ", yearActive=" + yearActive +
                '}';
    }
}