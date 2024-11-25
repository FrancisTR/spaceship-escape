import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    
    private String name;
    private boolean hasAlien;
    private boolean hasCache;
    private boolean hasLaunchCode;
    // ArrayList to hold room names
    private ArrayList<String> roomNames = new ArrayList<>(Arrays.asList(
        "Bridge",
            "Crew Quarters",
            "Galley",
            "Medical Bay",
            "Airlock",
            "Engineering Bay",
            "Cargo Bay",
            "Science Lab",
            "Hydroponics Room",
            "Recreation Room",
            "Exercise Area",
            "Control Maintenance Room",
            "Communication Hub",
            "Observation Deck",
            "Cryosleep Chamber",
            "Sanitation Room",
            "Waste Management Room",
            "Fuel Storage Area",
            "Storage Room for EVA Suits",
            "Navigation Room"
        ));
    
    public Room() {
        //When a room is instantiated, choose a random name from the list.
        int random = (int)(Math.random() * roomNames.size()) +1;
        this.name = roomNames.get(random);
        this.hasAlien = (int) (Math.random() * 2) + 1 == 1 ? true : false;
        this.hasCache = (int) (Math.random() * 2) + 1 == 1 ? true : false;
        this.hasLaunchCode = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasAlien() {
        return hasAlien;
    }

    public boolean hasCache() {
        return hasCache;
    }

    public void setHasCache(boolean val) {
        this.hasCache = val;
    }

    public void setHasAlien(boolean val) {
        this.hasAlien = val;
    }

    public boolean hasLaunchCode() {
        return hasLaunchCode;
    }

    public void setHasLaunchCode(boolean hasEscapeCode) {
        this.hasLaunchCode = hasEscapeCode;
    }

}