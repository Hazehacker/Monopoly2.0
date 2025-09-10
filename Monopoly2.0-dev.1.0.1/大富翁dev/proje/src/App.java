import com.Pojo.MusicPlayer;
import com.View.Game;
import com.View.GUI;
import javafx.application.Application;
import javafx.stage.Stage;
/*
 * 从swing转到了JavaFx
 * 接下来在JavaFx下重新绘制地图
 *
 * */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 启动音乐线程
        new Thread(() -> {
            MusicPlayer.MusicServer musicServer = new MusicPlayer.MusicServer();
            musicServer.playing(musicServer);
        }).start();

        // 初始化游戏和界面
        Game game = new Game();
        GUI gui = new GUI(game);
        // 将JavaFX的Stage传递给GUI（需要在GUI中添加对应的setter方法）
        gui.setPrimaryStage(primaryStage);
        gui.showWelcomeMessage();
        //设置程序图标
        gui.loadAndSetIcon(primaryStage);


    }

    public static void main(String[] args) {
        // 启动JavaFX应用
        launch(args);
    }
}

