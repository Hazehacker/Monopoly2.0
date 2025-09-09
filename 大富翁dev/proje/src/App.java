import com.Pojo.MusicPlayer;
import com.View.Game;
import com.View.GUI;
public class App {
    public static void main(String[] args) {
        new Thread(()->{
            MusicPlayer.MusicServer musicServer = new MusicPlayer.MusicServer();
            musicServer.playing(musicServer);
        }).start();
        GUI gui = new GUI();
        gui.showWelcomeMessage();

    }
}

