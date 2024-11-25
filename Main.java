import java.util.ArrayList;
import java.util.Random;

import textio.*;

/* This program runs a single-player text-based adventure game called "Spaceship Escape". */


//ToDo - If alien is defeated in a room, remove it from the room so it can be rexplored without fighting

public class Main {

    public static void main(String[] args) {
        introduceScenario();
        startGame();
    }

    //Global variables to hold the available rooms and the current Player.
    public static ArrayList<Room> rooms = createRooms();
    public static Player player;
    public static int launchCode = (int)(Math.random() * 9000) + 1000; //set the launchcode to a random 4 digit number

    /* ---------- GAME METHODS --------- */

    //The introduction to the game
    //Explains the starting situation and objective.
    public static void introduceScenario() {
        System.out.println("********** Welcome To SPACESHIP ESCAPE! **********");
        System.out.println();
        System.out.println("SITUATION:");
        System.out.println("""
                You awake in the medical bay of a spaceship orbiting Mars.
                You have no memory of how you got there.
                The rest of the crew is nowhere to be found.
                You know there is an escape pod but you can't remeber the launch code!
                There are strange sounds coming from outside in the corridor...
                    """);
        System.out.println("ENTER to continue...");
        TextIO.getln();
        System.out.println("YOUR OBJECTIVE:");
        System.out.println("""
                Explore the ship, find the launch code, and reach the escape pod!
                  """);
        System.out.println("ENTER to START GAME...");
        TextIO.getln();
        clearConsole();
    }

    //Creates the list of available rooms.
    public static ArrayList<Room> createRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
       
        //Add 5 rooms to the list
        for (int i = 0; i < 5; i++) {
            rooms.add(new Room());
        }

        //Make sure that we don't get rooms with duplicate names, if a duplicate is found, update it.
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                if (rooms.get(i).getName().equals(rooms.get(j).getName())) {
                    // Update the duplicate to a new Room
                    Room updatedRoom = new Room();
                    rooms.set(j, updatedRoom);
                }
            }
        }

        //Checks to make sure that at least one of the rooms has a cache where the launch codes can be found so the game can end.
        //Checks to make sure at least one room contains an alien the player must fight to continue.
        boolean noRoomHasCache = true;
        boolean noRoomHasAlien = true;

        for (Room room : rooms) {
            if (room.hasCache()) {
                noRoomHasCache = false;
            }
            if (room.hasAlien()) {
                noRoomHasAlien = false;
            }
        }

        if (noRoomHasCache) {
            int random1 = (int)(Math.random() * rooms.size()) + 1;
            rooms.get(random1).setHasCache(true);
            
        }

        if (noRoomHasAlien) {
            int random2 = (int)(Math.random() * rooms.size()) + 1;
            rooms.get(random2).setHasAlien(true);
        }


        // Find all rooms with hasCache = true
        ArrayList<Room> cacheRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.hasCache()) {
                cacheRooms.add(room);
            }
        }

        // Assign hasLaunchCode = true to one of the cache rooms
        if (cacheRooms.size() == 1) {
            // Only one room with cache
            cacheRooms.get(0).setHasLaunchCode(true);
        } else if (cacheRooms.size() > 1) {
            // More than one room with cache: Randomize
            Random random = new Random();
            int randomIndex = random.nextInt(cacheRooms.size());
            cacheRooms.get(randomIndex).setHasLaunchCode(true);
        }

    
        return rooms;
    }

    //Starts the game loop
    //Initiates players first choice of room to enter
    public static void startGame() {
        
        System.out.println("ENTER YOUR NAME: ");
        String name = TextIO.getlnString();
        player = new Player(name);

        System.out.println("""
                You go out into the corridor of the ship.
                You must explore the rooms to find the launch code.
                Choose which room to explore first:
                 """);

        int roomCount = 1;
        for (Room room : rooms) {
            System.out.println(roomCount + ". " + room.getName() + room.hasLaunchCode());
            roomCount++;
        }

        System.out.print("Enter Your Choice: ");

        int choice = TextIO.getlnInt();
        System.out.println();
        clearConsole();

        while(choice > rooms.size()) {
            System.out.println("Invalid Selection, please try again.");
            choice = TextIO.getlnInt();

        }
        
        exploreRoom(rooms.get(choice - 1));

    }

    //Gives player options when they enter a room.
    //What happens depends on the random values assinged to instance variables in the Room class
    public static void exploreRoom(Room room) {
        System.out.println(room.getName());
        
        System.out.println("You entered the " + room.getName() + "...");

        //If there's no cache AND no alien in the room
        if (!room.hasCache() && !room.hasAlien()) {
            System.out.println("Hmmm...doesn't seem to be anything of interest in here...");
            System.out.println("ENTER to go back into the corridor:");
            TextIO.getln();
            continueGame();
        } 

        //If there is an Alen player has a choice to fight or run away
        if (room.hasAlien()) {
            Alien alien = new Alien();
            System.out.println("A " + alien.getColor() + " alien with " + alien.getNumArms() + " arms and " + alien.getNumEyes() + " eyes jumps out at you! You must fight it or run away!");
            System.out.println("""
                1. Fight
                2. Run away
            """);
            System.out.println("Make your choice: ");
            int choice = TextIO.getlnInt();
            System.out.println();
            clearConsole();

            while(choice < 1 || choice > 2) {
                System.out.println("Invalid Selection, please try again.");
                choice = TextIO.getlnInt();
            }

            if (choice == 1) {
                fight(player.getHealth(), alien.getHealth());
            }
            else {
                continueGame();
            }
        }

        if (room.hasCache()) {
            System.out.println("You notice a cache in the room, there might be something useful in it!");
            System.out.println("Open the cache?");
            boolean choice = TextIO.getBoolean();

            if (choice) {
                openCache(room);
            }
            else {
                System.out.print("Hmm...there's nothing else in here...");
                continueGame();
            }
        }
        
        //The player might find the launch code to end the game when they open the cache
        //If they don't find the lauch codes then continue
        if (!player.hasLaunchCode())
            continueGame();

    }

    //When the player opens a cache they may find extra health, a weapon to increase their damage, or the launch code to end the game
    public static void openCache(Room room) {
        
        System.out.println("You open the cache...");
        if (room.hasLaunchCode()) {
            player.setHasLaunchCode(true);
            System.out.println("You found the escape pod launch code! Remember it: " + launchCode);
            //They player found the launch codes so can escape the ship and end the game
            System.out.println("""
                Make a choice:
                1. Go straight to the escape pod.
                2. Continue exploring.
            """);
            int choice = TextIO.getlnInt();
            clearConsole();

            while(choice < 1 || choice > 2) {
                System.out.println("Invalid Selection, please try again.");
                choice = TextIO.getlnInt();
            }

            if (choice == 1) {
                escape();
            }
            else {
                rooms.add(new EscapePod());
                continueGame();
            }
            
        }
        else {
            int random = (int)(Math.random() * 2) + 1;
            if (random == 1) {
                System.out.println("You find some food! Health increased by 20!");
                player.setHealth(player.getHealth() + 20);
                System.out.println("There's nothing left to see in here...");
                continueGame();
            }
            else {
                System.out.println("You find a laser pistol! Max damage increased by 20!");
                player.setMaxDamage(player.getMaxDamage() + 20);
                player.setHealth(player.getHealth() + 20);
                System.out.println("There's nothing left to see in here...");
                continueGame();
            }
            
        }
    }

    //This is called when the player has found the launch codes and the game ends
    public static void escape() {

        String[][] keypad = new String[][] {
            {"1","2","3"},
            {"4","5","6"},
            {"7","8","9"},
            {" ", "0", " "}
        };
        System.out.println("You head straight for the escape pod...");
        System.out.println("You strap yourself into the escape pod and see the keypad:");
        for(int i = 0; i < keypad.length; i ++) {
            for (int j = 0; j < keypad[i].length; j++) {
                System.out.print(keypad[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("ENTER THE LAUNCH CODE TO ESCAPE:");
        int input = TextIO.getlnInt();
        if (launchCode == input) {
            System.out.println("CONGRATULATIONS " + player.getName().toUpperCase() + "! YOU HAVE ESCAPED THE SPACESHIP WITH " + player.getHealth() + " HEALTH REMAINING!");
            System.exit(0);
        }
        else {
            System.out.println("Sorry that's not the correct code! Go back to where you found it and check again!");
            continueGame();
        }
    }

    //This is called to allow the player to leave a room and continue the game
    public static void continueGame() 
    {
        
        System.out.println("You go back out into the corridor of the ship.");
        System.out.println("Choose which room to explore next:");

        int roomCount = 1;
        for (Room room : rooms) {
            System.out.println(roomCount + ". " + room.getName());
            roomCount++;
        }

        System.out.print("Enter Your Choice: ");

        int choice = TextIO.getlnInt();
        System.out.println();
        clearConsole();

        while(choice > rooms.size()) {
            System.out.println("Invalid Selection, please try again.");
            choice = TextIO.getlnInt();

        }
        
        if (rooms.get(choice - 1).getName().equals("Escape Pod")){
            escape();
        }
        else {
            exploreRoom(rooms.get(choice - 1));
        }
        
    }

    //This is called when a Player chooses to fight an Alien
    public static boolean fight(int playerHealth, int alienHealth) {
        Random random = new Random();
        boolean playerWins = true;
        
        // Define damage ranges
        //Player has higher damage range to ensure a greater chance of victory.
        int playerMinDamage = 10, playerMaxDamage = player.getMaxDamage();
        int alienMinDamage = 5, alienMaxDamage = 25;
        
        System.out.println("The fight begins!");
        System.out.println("Player Health: " + playerHealth);
        System.out.println("Alien Health: " + alienHealth);

        // Fight loop
        while (playerHealth > 0 && alienHealth > 0) {
            // Player's turn to attack
            int playerDamage = random.nextInt(playerMaxDamage - playerMinDamage + 1) + playerMinDamage;
            alienHealth -= playerDamage;
            System.out.println("Player attacks for " + playerDamage + " damage!");
            if (alienHealth <= 0) {
                System.out.println("The alien is defeated!");
                break;
            }
            System.out.println("Alien's remaining health: " + alienHealth);
            TextIO.getln();

            // Alien's turn to attack
            int alienDamage = random.nextInt(alienMaxDamage - alienMinDamage + 1) + alienMinDamage;
            playerHealth -= alienDamage;
            System.out.println("Creature attacks for " + alienDamage + " damage!");
            if (playerHealth <= 0) {
                System.out.println("The player is defeated!");
                playerWins = false;
                break;
            }
            System.out.println("Player's remaining health: " + playerHealth);
            TextIO.getln();
        }

        System.out.println("The fight is over!");
        if (playerHealth > 0) {
            System.out.println("The player emerges victorious!");
            //set the player's health to what they ended the fight with 
            player.setHealth(playerHealth);
            System.out.println("You have " + player.getHealth() + " health remaining!");
            return playerWins;
        } else {
            System.out.println("The alien wins the battle!");
            return playerWins;
        }
    }


    /* ---------- UTILITY METHODS ---------- */

    //This method is to make sure the console doesn't get cluttered with past instructions/entries.
    public static void clearConsole() {
        // \033[H: Moves the cursor to the top-left corner of the console.
        // \033[2J: Clears the entire screen.
        System.out.print("\033[H\033[2J");
        // Ensures that the escape codes are immediately sent to the console.
        System.out.flush();

    }

}
