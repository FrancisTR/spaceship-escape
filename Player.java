public class Player {
    private String name;
    private int health;
    private int maxDamage;
    private boolean hasLaunchCode;
    
    public Player(String name) {
        this.name = name;
        this.health = 100;
        this.hasLaunchCode = false;
        this.maxDamage = 50;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean hasLaunchCode() {
        return hasLaunchCode;
    }

    public void setHasLaunchCode(boolean hasLaunchCode) {
        this.hasLaunchCode = hasLaunchCode;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void setMaxDamage(int damage) {
        this.maxDamage = damage;
    }

    public void getPlayerStats(){
        System.out.println("-------------------------------");
        System.out.println("Player name: "+getName());
        System.out.println("Health: "+getHealth());
        System.out.println("-------------------------------");
    }
}
