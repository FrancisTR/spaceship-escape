import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.net.URI;
import java.awt.Desktop;
import java.io.*;

import textio.*;

/* This program runs a single-player text-based adventure game called "Spaceship Escape". */

/*
    Main requirement: Create a text-based, role-playing game. 

    1. A configuration file for saving interesting aspects of the game, such as player names and high scores.  Even some player character customization.   

    2. A Save and restore feature.  You should be able to save your game, exit, and return to exactly where you left off.  

    5. Optional. Expand requirement #2 with autosave so a player never has to hit save.
 */


public class Main {

    public static void main(String[] args) throws Exception {
        introduceScenario(); // Introduction
        startGame(); // Game Start
    }

    // Global variables to hold the available rooms and the current Player.
    public static ArrayList<Room> rooms;
    public static Player player;
    public static int launchCode = (int) (Math.random() * 9000) + 1000; // set the launchcode to a random 4 digit number
    public static boolean easyMode = false;
    /* ---------- GAME METHODS --------- */

    // The introduction to the game
    // Explains the starting situation and objective.
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

    // Creates the list of available rooms.
    public static ArrayList<Room> createRooms() {
        ArrayList<Room> rooms = new ArrayList<>();

        // Add 5 rooms to the list
        for (int i = 0; i < 5; i++) {
            rooms.add(new Room());
        }

        // Make sure that we don't get rooms with duplicate names, if a duplicate is
        // found, update it.
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                if (rooms.get(i).getName().equals(rooms.get(j).getName())) {
                    // Update the duplicate to a new Room
                    Room updatedRoom = new Room();
                    rooms.set(j, updatedRoom);
                }
            }
        }

        // Checks to make sure that at least one of the rooms has a cache where the
        // launch codes can be found so the game can end.
        // Checks to make sure at least one room contains an alien the player must fight
        // to continue.
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
            int random1 = (int) (Math.random() * rooms.size() -1) + 1;
            if (!rooms.get(random1).getName().equals("Escape Pod"))
            {
                rooms.get(random1).setHasCache(true);
            }
            

        }

        if (noRoomHasAlien) {
            int random2 = (int) (Math.random() * rooms.size() -1) + 1;
            if (!rooms.get(random2).getName().equals("Escape Pod"))
            {
                rooms.get(random2).setHasAlien(true);
            }
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

    // Starts the game loop
    // Initiates players first choice of room to enter
    public static void startGame() throws Exception {
        String name;
        System.out.println("Would you like to load a previous player? y/n");
        boolean input = TextIO.getBoolean();
        if (input) {
            String filePath = "player.ser";
            Path path = Paths.get(filePath);
            boolean playerExists = Files.exists(path);
            if (playerExists) {
                player = SaveGame.load();
                rooms = player.getRooms();
                System.out.println("Player Loaded!");
            } else {
                System.out.println("Sorry no player exists to load!");
                System.out.println("ENTER YOUR NAME: ");
                do {
                    name = TextIO.getlnString();
                } while(name.equals(""));
                
                player = new Player(name);
                player.setRooms(createRooms());
                SaveGame.save(player);
            }
        } else {
            System.out.println("ENTER YOUR NAME: ");
            do {
                name = TextIO.getlnString();
            } while(name.equals(""));
            player = new Player(name);
            player.setRooms(createRooms());
            SaveGame.save(player);
        }
        
        rooms = player.getRooms();
        
        System.out.print("Do you want to enable easy mode? (y/n): ");
        easyMode = TextIO.getBoolean();
        clearConsole();

        player.getPlayerStats();
        System.out.println("""
                You go out into the corridor of the ship.
                You must explore the rooms to find the launch code.
                Choose which room to explore first:
                 """);

        int roomCount = 1;
        for (Room room : rooms) {
            System.out.println(roomCount + ". " + room.getName());
            roomCount++;
        }
        System.out.println("0. EXIT GAME");

        System.out.print("Enter Your Choice: ");

        int choice = TextIO.getlnInt();
        System.out.println();

        if (choice == 0) {
            SaveGame.save(player);
            System.out.println("Game Exited!");
            System.exit(0);
        }

        while (choice > rooms.size()) {
            System.out.println("Invalid Selection, please try again.");
            System.out.print("Enter Your Choice: ");
            choice = TextIO.getlnInt();
        }
        clearConsole();
        exploreRoom(rooms.get(choice - 1));

    }

    // Gives player options when they enter a room.
    // What happens depends on the random values assinged to instance variables in
    // the Room class
    public static void exploreRoom(Room room) throws Exception {
        System.out.println(room.getName());

        System.out.println("You entered the " + room.getName() + "...");

        // If there's no cache AND no alien in the room
        if (!room.hasCache() && !room.hasAlien()) {
            if (room.getName().equals("Escape Pod")) {
                escape();
            }
            else {
                System.out.println("Hmmm...doesn't seem to be anything of interest in here...");
                System.out.println("ENTER to go back into the corridor:");
                TextIO.getln();
                continueGame();
            }
            
        }

        // If there is an Alen player has a choice to fight or run away
        if (room.hasAlien()) {
            Alien alien = new Alien();
            System.out.println("A " + alien.getColor() + " alien with " + alien.getNumArms() + " arms and "
                    + alien.getNumEyes() + " eyes jumps out at you! You must fight it or run away!");
            System.out.println("""
                        1. Fight
                        2. Run away
                    """);
            System.out.print("Make your choice: ");
            int choice = TextIO.getlnInt();

            while (choice < 1 || choice > 2) {
                System.out.println("Invalid Selection, please try again.");
                System.out.print("Make your choice: ");
                choice = TextIO.getlnInt();
            }
            clearConsole();

            if (choice == 1) {
                // if the player wins, remove the alien from the room so they don't have to
                // fight it again if they re-enter the room later
                if (fight(player.getHealth(), alien.getHealth())) {
                    room.setHasAlien(false);
                } else {
                    // player has lost the fight
                    room.setHasAlien(true);
                    // add GAME OVER method or reset player health to 50 or player lives (3)
                }
            } else {
                continueGame();
            }
        }

        if (room.hasCache()) {
            System.out.println("You notice a cache in the room, there might be something useful in it!");
            System.out.print("Open the cache? (y/n): ");
            boolean choice = TextIO.getBoolean();

            if (choice) {
                openCache(room);
            } else {
                System.out.println("Hmm...there's nothing else in here...");
                continueGame();
            }
        }

        continueGame();

    }

    // When the player opens a cache they may find extra health, a weapon to
    // increase their damage, or the launch code to end the game
    public static void openCache(Room room) throws Exception {

        System.out.println("You open the cache...");
        if (room.hasLaunchCode()) {
            player.setHasLaunchCode(true);
            System.out.println("You found the escape pod launch code! Remember it: " + launchCode);
            SaveGame.save(player);
            // They player found the launch codes so can escape the ship and end the game
            // System.out.println("""
            // Make a choice:
            // 1. Go straight to the escape pod.
            // 2. Continue exploring.
            // """);
            // int choice = TextIO.getlnInt();
            // clearConsole();

            // while(choice < 1 || choice > 2) {
            // System.out.println("Invalid Selection, please try again.");
            // choice = TextIO.getlnInt();
            // }

            // if (choice == 1) {
            // escape();
            // }
            // else {
            // rooms.add(new EscapePod());
            // continueGame();
            // }
            System.out.println("""
                        Make a choice:
                        1. Go straight to the escape pod.
                        2. Continue exploring.
                    """);
            do {
                int choice = TextIO.getlnInt();
                switch (choice) {
                    case 1 -> { // Go to escape pod
                        clearConsole();
                        escape();
                    }
                    case 2 -> { // Continue searching in different rooms
                        clearConsole();
                        if (!rooms.get(rooms.size() -1).getName().equals("Escape Pod")) 
                        {
                            rooms.add(new EscapePod());
                        }
                        continueGame();
                    }
                    default -> {
                        System.out.println("Invalid Selection, please try again.");
                        System.out.println("""
                        Make a choice:
                        1. Go straight to the escape pod.
                        2. Continue exploring.
                    """);
                    }
                }
            } while (true);
        } else {
            int random = (int) (Math.random() * 2) + 1;
            System.out.println("--------------------------------------ITEM--------------------------------------");
            if (random == 1) {
                System.out.println("You find some food! Health increased by 20!");
                player.setHealth(player.getHealth() + 20);
                System.out.println("There's nothing left to see in here...");
                room.setHasCache(false); // remove the cash from the room so the player can't get it again if they
                // re-enter the room
            } else {
                System.out.println("You find a laser pistol! Max damage increased by 20!");
                player.setMaxDamage(player.getMaxDamage() + 20);
                player.setHealth(player.getHealth() + 20);
                System.out.println("There's nothing left to see in here...");
                room.setHasCache(false); // remove the cash from the room so the player can't get it again if they
                // re-enter the room
            }
            System.out.println("--------------------------------------------------------------------------------");
            continueGame();
        }
    }

    // This is called when the player has found the launch codes and the game ends
    public static void escape() throws Exception {
        int attempts = 3;

        String[][] keypad = new String[][] {
                { "1", "2", "3" },
                { "4", "5", "6" },
                { "7", "8", "9" },
                { " ", "0", " " }
        };
        System.out.println("You head straight for the escape pod...");
        System.out.println("You strap yourself into the escape pod and see the keypad:");
        for (int i = 0; i < keypad.length; i++) {
            for (int j = 0; j < keypad[i].length; j++) {
                System.out.print(keypad[i][j] + " ");
            }
            System.out.println();
        }

        // Player has 3 tries
        do {
            System.out.print("ENTER THE LAUNCH CODE TO ESCAPE. YOU HAVE " + attempts + " ATTEMPTS: ");
            int input = TextIO.getlnInt();

            if (launchCode == input) {
                System.out.println("CONGRATULATIONS " + player.getName().toUpperCase()
                        + "! YOU HAVE ESCAPED THE SPACESHIP WITH " + player.getHealth() + " HEALTH REMAINING!");
                System.exit(0);
            } else {
                attempts -= 1;
                // continueGame();
            }
        } while (attempts > 0);
        rickRoll();
    }

    // This is called to allow the player to leave a room and continue the game
    public static void continueGame() throws Exception {
        SaveGame.save(player);
        System.out.println("You go back out into the corridor of the ship.");
        System.out.println("Choose which room to explore next:");
        player.getPlayerStats();

        int roomCount = 1;
        for (Room room : rooms) {
            System.out.println(roomCount + ". " + room.getName());
            roomCount++;
        }
        System.out.println("0. EXIT GAME");

        System.out.print("Enter Your Choice: ");
        int choice = TextIO.getlnInt();
        System.out.println();

        if (choice == 0) {
            SaveGame.save(player);
            System.out.println("Game Exited!");
            System.exit(0);
        }

        while (choice > rooms.size()) {
            System.out.println("Invalid Selection, please try again.");
            System.out.print("Enter Your Choice: ");
            choice = TextIO.getlnInt();
        }

        clearConsole();
        if (rooms.get(choice - 1).getName().equals("Escape Pod")) {
            escape();
        } else {
            exploreRoom(rooms.get(choice - 1));
        }

    }

    // This is called when a Player chooses to fight an Alien
    public static boolean fight(int playerHealth, int alienHealth) {
        Random random = new Random();
        boolean playerWins = true;

        // Define damage ranges
        // Player has higher damage range to ensure a greater chance of victory.
        int playerMinDamage = 10, playerMaxDamage = player.getMaxDamage();
        int alienMinDamage = 5, alienMaxDamage = 25;

        System.out.println("-----------------------FIGHT BEGIN-----------------------");
        System.out.println("Player Health: " + playerHealth);
        System.out.println("Alien Health: " + alienHealth);
        System.out.println("---------------------------------------------------------");

        do {
            boolean rewind = false;
            // Player's turn to attack
            int playerDamage = random.nextInt(playerMaxDamage - playerMinDamage + 1) + playerMinDamage;
            alienHealth -= playerDamage;
            System.out.println("Player attacks for " + playerDamage + " damage!");
            if (alienHealth <= 0) {
                System.out.println("---------------VICTORY---------------");
                System.out.println("The alien is defeated!");
                break;
            }
            System.out.println("-------------------------------------");
            System.out.println("Alien's remaining health: " + alienHealth);
            System.out.println("-------------------------------------");
            
            TextIO.getln();

            // Alien's turn to attack
            int alienDamage = random.nextInt(alienMaxDamage - alienMinDamage + 1) + alienMinDamage;
            playerHealth -= alienDamage;
            System.out.println("Creature attacks for " + alienDamage + " damage!");
            if (playerHealth <= 0) {
                System.out.println("---------------DEFEAT---------------");
                System.out.println("The player is defeated!");
                System.out.println("------------------------------------");

                if (easyMode){
                    playerWins = false;
                    break;
                }else{
                    System.out.println("GAME OVER!");
                    System.exit(0);
                }
            }
            System.out.println("-------------------------------------");
            System.out.println("Player's remaining health: " + playerHealth);
            System.out.println("-------------------------------------");

            //Reverse time?
            if (easyMode){
                System.out.println("---------------------");
                System.out.print("Reverse Time? (y/n): ");
                rewind = TextIO.getlnBoolean();
                if (rewind){ //back to the original state
                    System.out.println("REVERSING TIME...");
                    alienHealth += playerDamage;
                    playerHealth += alienDamage;
                }
            }else{
                TextIO.getln();
            }
        } while (playerHealth > 0 && alienHealth > 0);

        System.out.println("The fight is over!");
        if (playerHealth > 0) {
            System.out.println("The player emerges victorious!");
            // set the player's health to what they ended the fight with
            player.setHealth(playerHealth);
            System.out.println("You have " + player.getHealth() + " health remaining!");
            System.out.println("-------------------------------------");
        } else {
            System.out.println("The alien wins the battle!");
        }
        System.out.println("Press ENTER to Continue: ");
        TextIO.getln();
        return playerWins;
    }

    /* ---------- UTILITY METHODS ---------- */

    // This method is to make sure the console doesn't get cluttered with past
    // instructions/entries.
    public static void clearConsole() {
        // \033[H: Moves the cursor to the top-left corner of the console.
        // \033[2J: Clears the entire screen.
        System.out.print("\033[H\033[2J");
        // Ensures that the escape codes are immediately sent to the console.
        System.out.flush();

    }

    // A joke method to surprise the player!
    public static void rickRoll() throws Exception {
        Desktop desk = Desktop.getDesktop();

        // now we enter our URL that we want to open in our
        // default browser
        System.out.println("You have failed to enter the escape pod! Game Over!");
        System.out.println("Never gonna give you up though! So next time, make sure you'll never let us down!");
        desk.browse(new URI("https://www.youtube.com/watch?v=oHg5SJYRHA0"));
        System.exit(0);
    }

}
