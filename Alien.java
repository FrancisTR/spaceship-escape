public class Alien {
    private int health;
    private int numEyes;
    private int numArms;
    private String color;

    public Alien() {
        this.health = (int)(Math.random() * 100) +1;
        this.numEyes = (int)(Math.random() * 10) +1;;
        this.numArms = (int)(Math.random() * 5) +1;;
        this.color = switch ((int)(Math.random() * 5) +1) {
            case 1 -> "green";
            case 2 -> "orange";
            case 3 -> "blue";
            case 4 -> "purple";
            default -> "pink";
        };
    }


    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getNumEyes() {
        return numEyes;
    }
    public void setNumEyes(int numEyes) {
        this.numEyes = numEyes;
    }
    public int getNumArms() {
        return numArms;
    }
    public void setNumArms(int numArms) {
        this.numArms = numArms;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    
}
