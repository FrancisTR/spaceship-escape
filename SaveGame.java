import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveGame{

    public static void save(Player player){
        serializePlayer(player);
        

    }
    public static Player load(){
        return deserializePlayer();
    }

    public static void serializePlayer(Player player) {
        try {
            FileOutputStream fos = new FileOutputStream("player.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(player);
            System.out.println("Player Serialized Successfully!");
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public static Player deserializePlayer() {
        Player player;
        try {
            FileInputStream fis = new FileInputStream("player.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            player = (Player) ois.readObject();
            // System.out.println("Player deserialized Succeccfully!");
            // System.out.println("Player Name: " + player.getName());
            // System.out.println("Player Health: " + player.getHealth());
            // System.out.println("Player has launch code: " + player.getHasLaunchCode());
            // System.out.println("Player Max Damage: " + player.getMaxDamage());
            return player;

        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
            e.printStackTrace();;
        }
                return new Player("");
                
    }
}