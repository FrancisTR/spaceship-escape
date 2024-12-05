import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class UnitTest {
    
    Alien alien;

    @Before
    public void setup() throws Exception {
        alien = new Alien();
    }
    
    
    @Test
    public void testAlienHealth(){    
        assertTrue((alien.getHealth() > 0) && (alien.getHealth() <= 100));  
    }

    @Test public void testAlienEyes() {
        assertTrue((alien.getNumEyes() > 0) && (alien.getNumEyes() <= 10));
    }

    @Test
    public void testAlienArms() {
        assertTrue((alien.getNumArms() > 0) && (alien.getNumArms() <= 5));
    }

    @Test
    public void testAlienColor() {
        assertTrue(alien.getColor() == "green" || alien.getColor() == "orange" || alien.getColor() == "blue" || alien.getColor() == "purple" || alien.getColor() == "pink");
    }
}
