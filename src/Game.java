import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Game {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Game Java");
        config.setWindowedMode(1000, 500);
        config.useVsync(true);
        new Lwjgl3Application(new GameScreen(), config); // Certifique-se de que GameScreen estende ApplicationAdapter
    }
}
