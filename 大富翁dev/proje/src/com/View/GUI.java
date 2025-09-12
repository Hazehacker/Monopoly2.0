package com.View;
/*
 *在JavaFx下绘制地图
 *
 * */
import com.Pojo.Cards;
import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.InputCallback;
import com.Service.Service;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
//视频相关
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class GUI extends Application implements InputCallback {
    private Stage primaryStage;
    private Stage gameStage;
    //视频播放页面
    private Stage splashStage;
    private Game game;
    private boolean isMusicOn = true;
    private ArrayList<Land> lands;
    private ArrayList<Player> players;

    //存储地块面板与玩家头像的映射关系
    private Map<Integer, Pane> landPanels = new HashMap<>();
    // 存储带边框的头像容器
    private Map<Integer, Pane> playerAvatars = new HashMap<>();


    // 骰子按钮和结果标签
    private Button rollDiceButton;
    private Label diceResultLabel;
    private CompletableFuture<Integer> diceRollFuture;

    /**
     * 设计程序图标（注释内用于调试代码）
     */
    public void loadAndSetIcon(Stage stage) {
        String iconPath = "pic/pic-icon.png";
        try {
            if (getClass().getResource(iconPath) == null) {
                //System.err.println("图标资源不存在: " + iconPath);
                //System.err.println("请确认文件路径是否正确");
                return;
            }
            try (InputStream iconStream = getClass().getResourceAsStream(iconPath)) {
                Image icon = new Image(iconStream);
                if (icon.isError()) {
                    //System.err.println("图标文件损坏或格式不支持");
                    return;
                }
                stage.getIcons().add(icon);
                //System.out.println("图标加载成功: " + iconPath);
                //System.out.println("图标尺寸: " + icon.getWidth() + "x" + icon.getHeight());
            }
        } catch (Exception e) {
            //System.err.println("加载图标时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 负责人：郑俊晖
     * 功能：控制台操作等效转移至窗口
     * */
    /* ========== 控制台相关 ========== */
    private Button consoleButton;
    private VBox consolePanel;
    private TextArea consoleOutputArea;
    private TextField consoleInputField;
    private Button submitInputButton;

    /* 用于重定向 System.in/out */
    private PipedInputStream pipedIn;
    private PipedOutputStream pipedOutForIn;   // 写入 pipedIn
    private PrintStream pipedPrintStream;      // 重定向 System.out
    private PrintStream originalOut;

    private final BlockingQueue<String> inputQueue = new ArrayBlockingQueue<>(1);

    public GUI(Game game) {

        this.game = game;

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // 先显示视频，视频播放完后自动显示欢迎界面
        showVideoSplash();
    }

    /**
     * 定义"玩家ID -> 边框颜色"的映射关系
     *
     */
    private final Map<Integer, Color> playerColors = new HashMap<>() {{
        put(1, Color.RED);
        put(2, Color.rgb(55,148,110));
        put(3, Color.rgb(91,110,225));
        put(4, Color.rgb(102,57,49));
    }};



    /**
     * 难度 **
     * 负责人：郑俊晖
     **功能：
     * 1，开始菜单制作
     * 2，开始菜单背景音效制作
     * 3，将图像在开始界面上展示
     * */

    /**
     * 显示视频 splash 页面
     */
    public void showVideoSplash() {
        splashStage = new Stage();
        splashStage.setTitle("游戏启动中");



        loadAndSetIcon(splashStage);

        // 视频文件路径
        String videoPath = getClass().getResource("video/OP.mp4").toExternalForm();
        System.out.println("视频路径: " + videoPath); // 若为 null，说明路径错误
        Media media = new Media(videoPath);
        System.out.println("媒体初始化状态: " + (media.getError() == null ? "正常" : media.getError().getMessage()));
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        //添加视频播放错误监听
        mediaPlayer.setOnError(() -> {
            Platform.runLater(() -> {
                System.err.println("视频播放错误: " + mediaPlayer.getError().getMessage());
                // 错误发生时，直接关闭视频窗口并显示欢迎界面
                splashStage.close();
                showWelcomeMessage();
            });
        });

        MediaView mediaView = new MediaView(mediaPlayer);




        // 设置视频适应窗口
        mediaView.fitWidthProperty().bind(splashStage.widthProperty());
        mediaView.fitHeightProperty().bind(splashStage.heightProperty());
        mediaView.setPreserveRatio(true);

        StackPane root = new StackPane();
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root, 800, 600);

        splashStage.setScene(scene);
        splashStage.setResizable(false);
        splashStage.centerOnScreen();


        // 视频播放完成后显示欢迎界面
        mediaPlayer.setOnEndOfMedia(() -> {
            splashStage.close();
            showWelcomeMessage();
        });

        // 播放视频
        mediaPlayer.play();
        splashStage.show();
    }

    /**
     * 显示欢迎界面
     */
    public void showWelcomeMessage() {
        Pane root = new Pane();

        // 加载音效资源（使用Java Sound API）
        Clip hoverClip = loadSound("/com/View/ui-music/hover.wav");
        Clip clickClip = loadSound("/com/View/ui-music/click.wav");

        // 设置音量（0.0到1.0）
        setClipVolume(hoverClip, 1f);
        setClipVolume(clickClip, 1f);

        Image backgroundImage = new Image(getClass().getResourceAsStream("pic/others/bg.png"));
        ImageView imageView = new ImageView(backgroundImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1100);
        imageView.setFitHeight(835);

        root.getChildren().add(imageView);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Monopoly");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setWidth(1100);
        primaryStage.setHeight(835);

        root.setPadding(new Insets(20));

        Image start = new Image(getClass().getResourceAsStream("/com/View/pic/others/start.png"));
        Image settings = new Image(getClass().getResourceAsStream("/com/View/pic/others/settings.png"));
        Image ranking = new Image(getClass().getResourceAsStream("/com/View/pic/others/ranking.png"));
        Image quit = new Image(getClass().getResourceAsStream("/com/View/pic/others/quit.png"));
        Image[] imgItems = {start, settings, ranking, quit};

        int X = 30;
        int Y = 250;
        int i = 0;
        String[] menuItems = {"开始游戏", "游戏设置", "排行榜", "退出游戏"};
        for (String item : menuItems) {
            ImageView imgview = new ImageView(imgItems[i]);
            i++;
            imgview.setFitWidth(175);
            imgview.setFitHeight(50);
            imgview.setPreserveRatio(true);

            Button button = new Button("", imgview);
            button.setStyle("-fx-background-color: transparent; " +
                    "-fx-border-width: 0; " +
                    "-fx-focus-traversable: false;");

            button.setLayoutX(X);
            button.setLayoutY(Y);
            Y += 125;
            button.setPrefSize(155, 50);

            // 悬停效果：图片提亮 + 播放悬停音效
            button.setOnMouseEntered(e -> {
                ColorAdjust brightEffect = new ColorAdjust();
                brightEffect.setBrightness(0.3);
                imgview.setEffect(brightEffect);

                playClip(hoverClip);
            });

            // 鼠标离开时恢复
            button.setOnMouseExited(e -> {
                imgview.setEffect(null);
            });

            // 点击事件：播放点击音效
            button.setOnAction(e -> {
                // 先停止并重置点击音效
                if (clickClip != null) {
                    clickClip.stop();
                    clickClip.setFramePosition(0);
                }
                // 播放点击音效
                playClip(clickClip);

                // 延迟处理动作
                new Thread(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(() -> handleWelcomeAction(item));
                }).start();
            });
            root.getChildren().add(button);
        }
        primaryStage.show();
    }

    // 加载音效文件
    private Clip loadSound(String resourcePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(resourcePath)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            System.err.println("无法加载音效: " + resourcePath + "，错误: " + e.getMessage());
            return null;
        }
    }

    // 修改playClip方法，确保每次播放都从头开始
    private void playClip(Clip clip) {
        if (clip == null) return;
        synchronized(clip) {
            // 停止当前播放
            clip.stop();
            // 确保重置到起始位置
            clip.setFramePosition(0);
            // 开始播放
            clip.start();
        }
    }

    // 设置音量
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // 将0.0-1.0的音量转换为dB
            float dB = (float) (Math.log(volume) / Math.log(10) * 20);
            gainControl.setValue(dB);
        } catch (Exception e) {
            System.err.println("无法设置音量: " + e.getMessage());
        }
    }

    /**
     * 处理欢迎界面按钮事件
     */
    private void handleWelcomeAction(String action) {
        switch (action) {
            case "退出游戏":
                System.exit(0);
                break;
            case "开始游戏":
                primaryStage.close();
                game.startGame();
                break;
            case "排行榜":
                showAlert("提示", "暂无此功能");
                break;
            case "游戏设置":
                showSettingsStage();
                break;
        }
    }

    /**
     * 显示设置界面
     */
    public void showSettingsStage() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("游戏设置");
        settingsStage.setWidth(300);
        settingsStage.setHeight(200);
        settingsStage.initOwner(primaryStage);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        CheckBox musicCheckBox = new CheckBox("开启音乐");
        musicCheckBox.setSelected(isMusicOn);
        musicCheckBox.setOnAction(e -> {
            isMusicOn = musicCheckBox.isSelected();
            showAlert("提示", isMusicOn ? "音乐已开启" : "音乐已关闭");
        });

        Button confirmButton = new Button("确认");
        confirmButton.setOnAction(e -> settingsStage.close());

        root.getChildren().addAll(musicCheckBox, confirmButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        settingsStage.setScene(scene);
        settingsStage.centerOnScreen();
        settingsStage.show();
    }
    /**
     * 难度 **
     * 负责人：龚浩天
     **功能：
     * 1，显示游戏主界面
     * 2，绘制ui
     * 3，将图像在界面上展示*/

    public void showGameStage() {
        gameStage = new Stage();
        loadAndSetIcon(gameStage);
        gameStage.setTitle("千富万翁");
        //目前游戏窗口大小固定，为了防止布局被伸缩破坏
        gameStage.setWidth(1100);
        gameStage.setHeight(835);
        gameStage.setResizable(false); // 禁止调整窗口大小
        gameStage.setOnCloseRequest(e -> {
            // 退出时恢复原始控制台流
            if (originalOut != null) System.setOut(originalOut);
            if (System.in != null) System.setIn(System.in);
            System.exit(0);
        });

        // 初始化空布局，等待数据加载后更新
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        gameStage.setScene(scene);
        gameStage.show();
    }
    /**
     * 初始化控制台组件（新增）
     */
    private void initConsoleComponents() {
        consoleOutputArea = new TextArea();
        consoleOutputArea.setEditable(false);
        consoleOutputArea.setPrefHeight(200);
        consoleOutputArea.setPrefWidth(400);
        consoleOutputArea.setWrapText(true);
        consoleOutputArea.setFont(new Font("Monospace", 12));

        consoleInputField = new TextField();
        consoleInputField.setPromptText("输入指令（Y/N 等）后按 Enter 或点击提交");
        consoleInputField.setPrefWidth(320);
        consoleInputField.setOnAction(e -> submitConsoleInput());

        submitInputButton = new Button("提交");
        submitInputButton.setPrefWidth(70);
        submitInputButton.setOnAction(e -> submitConsoleInput());

        HBox inputLine = new HBox(10, consoleInputField, submitInputButton);
        inputLine.setAlignment(Pos.CENTER);

        consolePanel = new VBox(10, consoleOutputArea, inputLine);
        consolePanel.setPadding(new Insets(10));
        consolePanel.setBorder(new Border(new BorderStroke(
                Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        consolePanel.setVisible(false);

        consoleButton = new Button("打开控制台");
        consoleButton.setPrefSize(120, 40);
        consoleButton.setOnAction(e -> {
            boolean now = !consolePanel.isVisible();
            consolePanel.setVisible(now);
            consoleButton.setText(now ? "关闭控制台" : "打开控制台");
        });

        redirectConsoleStreams();   // 关键：重定向
    }

    /**
     * 提交控制台输入
     */
    private void submitConsoleInput() {
        String txt = consoleInputField.getText().trim();
        if (txt.isEmpty()) return;
        consoleOutputArea.appendText("> " + txt + "\n");
        consoleInputField.clear();
        boolean ok = inputQueue.offer(txt);   // 先记录返回值
        System.out.println("【GUI】offer >" + txt + "< 结果=" + ok);                // 放入即唤醒
    }

    /**
     * 重定向系统输入输出
     */
    private void redirectConsoleStreams() {
        try {
            /* 1. 重定向 System.in */
            pipedIn = new PipedInputStream();
            pipedOutForIn = new PipedOutputStream(pipedIn);
            System.setIn(pipedIn);

            /* 2. 重定向 System.out */
            originalOut = System.out;
            pipedPrintStream = new PrintStream(new ByteArrayOutputStream() {
                @Override
                public void flush() throws IOException {
                    super.flush();
                    String line = toString();
                    if (!line.isEmpty()) {
                        Platform.runLater(() -> {
                            consoleOutputArea.appendText(line);
                            consoleOutputArea.setScrollTop(Double.MAX_VALUE);
                        });
                        originalOut.print(line);
                        reset();
                    }
                }
            }, true);
            System.setOut(pipedPrintStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示玩家以及地图信息
     */
    public void showPlayerInfo(Service service) {
        if (gameStage == null) return;

        players = (ArrayList<Player>) service.getPlayers();
        GameBoard gameBoard = service.gameBoard;
        lands = (ArrayList<Land>) gameBoard.getLands();

        //初始化玩家头像和控制台组件（仅初始化一次）
        initPlayerAvatars(players);
        if (consoleButton == null) {
            initConsoleComponents();
        }


        BorderPane root = (BorderPane) gameStage.getScene().getRoot();

        // 玩家信息展示区
        VBox centerPane = new VBox(10);
        centerPane.setPadding(new Insets(10));

        // 底部面板（骰子按钮和结果显示区域）
        HBox diceControlPanel = new HBox(10);
        diceControlPanel.setAlignment(Pos.CENTER);
        diceControlPanel.setPadding(new Insets(5));


        //控制台展示
        centerPane.setAlignment(Pos.TOP_CENTER);

        // 顶部空面板（原按钮区域）
        HBox topCenter = new HBox();
        topCenter.setPrefHeight(50);

        // 玩家信息面板
        HBox playerBox = new HBox(20);
        playerBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            playerBox.getChildren().add(createPlayerPanel(players.get(i)));
        }


        // 控制台容器（居中）
        VBox consoleContainer = new VBox(10, consoleButton, consolePanel);
        consoleContainer.setAlignment(Pos.CENTER);

        // 创建骰子按钮
        rollDiceButton = new Button("投骰子");
        rollDiceButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 10px;");
        rollDiceButton.setOnAction(e -> {
            int steps = Service.rollDice();
            showDiceResult(steps, game.getCurrentPlayerIndex());

            if (diceRollFuture != null) {
                diceRollFuture.complete(steps);
                diceRollFuture = null;
            }
        });

        // 创建结果显示标签
        diceResultLabel = new Label("骰子结果: ");
        diceResultLabel.setFont(Font.font(14));

        diceControlPanel.getChildren().addAll(rollDiceButton, diceResultLabel);

        // 组装中间区域
        centerPane.getChildren().addAll(topCenter, playerBox, consoleContainer,diceControlPanel);
        root.setCenter(centerPane);




        root.setCenter(centerPane);

        // 北部地块
        HBox northLands = new HBox(5);
        northLands.setPadding(new Insets(5));
        for (int i = 0; i <=6; i++) {
            Pane landPane = createLandPanel(lands.get(i));
            landPanels.put(lands.get(i).getId(), landPane);
            northLands.getChildren().add(landPane);
        }
        root.setTop(northLands);

        // 西部地块
        VBox westLands = new VBox(5);
        westLands.setPadding(new Insets(5));
        List<Integer> westLandIds = List.of(19, 18, 17);
        for (int id : westLandIds) {
            Pane landPane = createLandPanel(lands.get(id));
            landPanels.put(id, landPane);
            westLands.getChildren().add(landPane);
        }
        root.setLeft(westLands);

        // 东部地块
        VBox eastLands = new VBox(5);
        eastLands.setPadding(new Insets(5));
        List<Integer> eastLandIds = List.of(7, 8, 9);
        for (int id : eastLandIds) {
            Pane landPane = createLandPanel(lands.get(id));
            landPanels.put(id, landPane);
            eastLands.getChildren().add(landPane);
        }
        root.setRight(eastLands);

        HBox southLands = new HBox(5);
        southLands.setPadding(new Insets(5));
        for (int i = 16; i >= 10; i--) {
            Pane landPane = createLandPanel(lands.get(i));
            landPanels.put(lands.get(i).getId(), landPane);
            southLands.getChildren().add(landPane);
        }
        root.setBottom(southLands);

        //更新玩家位置
        updatePlayerPositions();
    }


    /**
     * 初始化玩家头像
     */
    //边框实际效果不太明显，尽力了orz
    private void initPlayerAvatars(List<Player> players) {
        for (Player player : players) {
            if (playerAvatars.containsKey(player.getId())) {
                continue;
            }

            //创建头像图片
            ImageView avatarImg = new ImageView(
                    new Image(getClass().getResourceAsStream("pic/playerPic/p" + player.getPicId() + "/" + player.getPicId() + ".jpg"))
            );
            avatarImg.setFitWidth(50);
            avatarImg.setFitHeight(50);
            avatarImg.setPreserveRatio(true);
            avatarImg.setSmooth(true);

            // 为破产玩家添加灰度效果
            if (player.getMoney()<0) {
                ColorAdjust grayEffect = new ColorAdjust();
                grayEffect.setSaturation(-1.0); // 完全去色
                avatarImg.setEffect(grayEffect);
            }

            //创建边框容器（Pane），包裹头像
            Pane avatarContainer = new Pane(avatarImg); // 将头像作为子节点
            avatarContainer.setPrefSize(37.5, 37.5);    // 容器大小与头像一致

            // 设置边框
            Color color = playerColors.getOrDefault(player.getId(), Color.GRAY);
            // 破产玩家使用灰色虚线边框
            if (player.getMoney()<0) {
                avatarContainer.setBorder(new Border(new BorderStroke(
                        Color.GRAY,
                        BorderStrokeStyle.DASHED,
                        CornerRadii.EMPTY,
                        new BorderWidths(4)
                )));
            } else {
                avatarContainer.setBorder(new Border(new BorderStroke(
                        color,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(4)
                )));
            }

            // 添加阴影效果
            avatarContainer.setEffect(new DropShadow(
                    2, 0, 0, Color.rgb(0, 0, 0, 0.3)
            ));

            // 存储边框头像
            playerAvatars.put(player.getId(), avatarContainer);
        }
    }


    /**
     * 更新所有玩家的位置显示
     */
    public void updatePlayerPositions() {
        // 清除所有地块上的玩家头像容器（现在是Pane类型）
        for (Pane landPane : landPanels.values()) {
            landPane.getChildren().removeIf(node ->
                    node instanceof Pane && playerAvatars.containsValue(node)
            );
        }

        // 按地块分组玩家（逻辑不变）
        Map<Integer, List<Player>> playersOnLand = new HashMap<>();
        for (Player player : players) {
            int landId = lands.get(player.getPosition()).getId();
            playersOnLand.computeIfAbsent(landId, k -> new ArrayList<>()).add(player);
        }

// 为每个地块上的玩家设置位置
        for (Map.Entry<Integer, List<Player>> entry : playersOnLand.entrySet()) {
            int landId = entry.getKey();
            List<Player> playersHere = entry.getValue();
            Pane landPane = landPanels.get(landId);

            if (landPane == null) continue;

            int playerCount = playersHere.size();
            for (int i = 0; i < playerCount; i++) {
                Player player = playersHere.get(i);
                // 获取带边框的头像容器（Pane类型）
                Pane avatarContainer = playerAvatars.get(player.getId());
                double[] position = calculateAvatarPosition(i, playerCount);
                // 添加到地块（调用下面修改的addPlayerToLand方法）
                addPlayerToLand(landPane, avatarContainer, position[0], position[1]);
            }
        }
    }

    /**
     * 计算头像位置
     */
    private double[] calculateAvatarPosition(int index, int totalPlayers) {
        double x = 0;
        // 距离地皮底部10px的位置（地皮高度150 - 头像高度50 - 间距10 = 90）
        double y = 150 - 50 - 10;  // 最终y坐标固定为90

        // 头像尺寸50×50，地皮宽度150，基于这些参数计算x坐标
        switch (totalPlayers) {
            case 1:
                // 单个玩家居中：(地皮宽度 - 头像宽度) / 2
                x = (150 - 50) / 2;  // 50
                break;
            case 2:
                // 两个玩家分左右，各留10px边距
                if (index == 0) {
                    x = 10;  // 左侧边距10px
                } else {
                    x = 150 - 50 - 10;  // 右侧边距10px（90）
                }
                break;
            default:
                // 三个及以上：半重叠堆叠（偏移量为头像宽度的一半25px）
                // 从左侧10px开始，每个后续头像右移25px
                x = 10 + (index * 25);

                // 限制最大偏移，避免超出地皮范围（最多右移至头像右侧不超过地皮右边界）
                if (x > 150 - 50) {
                    x = 150 - 50;  // 最大x坐标为100
                }
                break;
        }

        return new double[]{x, y};
    }

    /**
     * 添加玩家头像到地块，使用layoutX和layoutY设置位置
     */
    private void addPlayerToLand(Pane landPanel, Pane avatarContainer, double x, double y) {
        avatarContainer.setLayoutX(x);  // 容器的X坐标
        avatarContainer.setLayoutY(y);  // 容器的Y坐标

        if (!landPanel.getChildren().contains(avatarContainer)) {
            landPanel.getChildren().add(avatarContainer);
        }
    }


    /**
     * 创建玩家信息面板（包括立绘，id，资金，行动状态等）
     */
    private Pane createPlayerPanel(Player player) {
        VBox panel = new VBox(10);
        panel.setPrefWidth(100);
        panel.setAlignment(Pos.CENTER);

        // 根据玩家ID获取颜色
        javafx.scene.paint.Color color = playerColors.getOrDefault(player.getId(), javafx.scene.paint.Color.GRAY);

        // 角色立绘
        ImageView characterImage = new ImageView(
                new Image(getClass().getResourceAsStream("pic/playerPic/p" + player.getPicId() +"/"+player.getPicId()+ ".png"))
        );
        characterImage.setFitWidth(75); // 设置宽度为75px
        characterImage.setPreserveRatio(true); // 保持宽高比
        characterImage.setSmooth(true); // 平滑缩放(提升显示效果)
        // 为破产玩家添加灰度效果
        if (player.getMoney()<0) {
            ColorAdjust grayEffect = new ColorAdjust();
            grayEffect.setSaturation(-1.0); // 饱和度设为-1.0，完全去色（灰度）
            grayEffect.setBrightness(0.1); // 稍微提亮一点，避免过于灰暗
            characterImage.setEffect(grayEffect);
        }



        // 角色信息
        VBox infoPanel = new VBox(5);
        infoPanel.setPrefWidth(75);
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setBorder(new Border(new BorderStroke(
                color,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3), // 圆角半径
                new BorderWidths(3) // 边框宽度
        )));


        infoPanel.getChildren().addAll(
                new Label("ID: " + player.getId()),
                new Label("名称: " + player.getName()),
                new Label("现金: " + player.getMoney()),
                new Label("位置: " + lands.get(player.getPosition()).getName())
        );
        // 状态图标容器
        HBox statusIconContainer = new HBox();
        statusIconContainer.setPrefHeight(20);
        statusIconContainer.setAlignment(Pos.CENTER);

        // 根据玩家状态添加对应图标
        ImageView statusIcon = new ImageView();
        statusIcon.setFitWidth(20);
        statusIcon.setFitHeight(20);

        if (player.getMoney()<0) {
            // 玩家已淘汰，显示叉号图标
            statusIcon.setImage(new Image(getClass().getResourceAsStream("pic/others/out.png")));
        } else if (isCurrentPlayer(player)) {
            // 当前行动玩家，显示箭头图标
            statusIcon.setImage(new Image(getClass().getResourceAsStream("pic/others/arrow.png")));
        }

        // 检查是否在监狱
        if (player.isInJail()) {
            ImageView jailIcon = new ImageView();
            jailIcon.setFitWidth(20);
            jailIcon.setFitHeight(20);
            jailIcon.setImage(new Image(getClass().getResourceAsStream("pic/others/zuolao.png")));
            statusIconContainer.getChildren().add(jailIcon);
        }
        // 检查是否中了乌龟卡
        if (player.isIsTurtle()) {
            ImageView turtleIcon = new ImageView();
            turtleIcon.setFitWidth(20);
            turtleIcon.setFitHeight(20);
            turtleIcon.setImage(new Image(getClass().getResourceAsStream("pic/others/wugui.png")));
            statusIconContainer.getChildren().add(turtleIcon);
        }

        statusIconContainer.getChildren().add(statusIcon);

        panel.getChildren().addAll(characterImage, infoPanel,statusIconContainer);
        return panel;
    }

    /**
     * 判断是否为当前行动玩家
     */
    private boolean isCurrentPlayer(Player player) {
        // 需要Game类提供当前玩家索引的访问方法
        if (game != null && game.getCurrentPlayerIndex() >= 0 &&
                game.getService() != null && game.getService().getPlayers() != null) {
            List<Player> players = game.getService().getPlayers();
            if (game.getCurrentPlayerIndex() < players.size()) {
                return players.get(game.getCurrentPlayerIndex()).getId() == player.getId();
            }
        }
        return false;
    }


    /**
     * 创建地块信息面板
     */
    private Pane createLandPanel(Land land) {
        Pane panel = new Pane(); // 用 Pane 替代 VBox，支持自由定位
        panel.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT
        )));
        panel.setPrefSize(150, 150); // 固定地块大小（与头像尺寸适配）

        // 加载地块图片（保持不变）
        String imagePath = "pic/map/" + land.getId() + ".png";
        Image landImage;
        try {
            landImage = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.out.println("资源加载失败: " + imagePath);
            landImage = new Image(getClass().getResourceAsStream("pic/map/default.png"));
        }
        ImageView imageView = new ImageView(landImage);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setOnMouseClicked(event -> showLandInfoDialog(land));

        panel.getChildren().add(imageView);
        // 初始化房子容器（用于动态更新房子显示）
        HBox houseContainer = new HBox(5); //水平布局房子，间距5px
        houseContainer.setId("houseContainer_" + land.getId()); //唯一标识
        houseContainer.setLayoutY(40); //距离顶端40px处
        houseContainer.setAlignment(Pos.CENTER); //水平居中
        houseContainer.setPrefWidth(150); //与地块同宽
        panel.getChildren().add(houseContainer);

        // 初始加载房子
        updateHousesOnLand(land, panel);

        return panel;
    }
    /**
     * 更新地块上的房子显示
     */
    public void updateHousesOnLand(Land land, Pane landPanel) {
        // 找到房子容器
        HBox houseContainer = (HBox) landPanel.lookup("#houseContainer_" + land.getId());
        if (houseContainer == null) return;

        // 清除现有房子
        houseContainer.getChildren().clear();

        // 没有拥有者或等级为0时不显示房子
        if (land.getOwner()==null || land.getHouseCount() == 0) {
            return;
        }

        // 房子图片尺寸（地块的1/4：150/4=37.5）
        double houseSize = 37.5;
        int ownerId = land.getOwner().getId();

        try {
            if (land.getHouseCount() == 4) {
                // 4级显示大房子
                Image bigHouse = new Image(getClass().getResourceAsStream(
                        "pic/others/p" + ownerId + "bighouse.png"
                ));
                ImageView bigHouseView = new ImageView(bigHouse);
                bigHouseView.setFitWidth(houseSize * 2); // 大房子宽度是普通的2倍
                bigHouseView.setFitHeight(houseSize);
                bigHouseView.setPreserveRatio(true);
                houseContainer.getChildren().add(bigHouseView);
            } else {
                // 1-3级显示对应数量的小房子
                for (int i = 0; i < land.getHouseCount(); i++) {
                    Image smallHouse = new Image(getClass().getResourceAsStream(
                            "pic/others/p" + ownerId + "house.png"
                    ));
                    ImageView houseView = new ImageView(smallHouse);
                    houseView.setFitWidth(houseSize);
                    houseView.setFitHeight(houseSize);
                    houseView.setPreserveRatio(true);
                    houseContainer.getChildren().add(houseView);
                }
            }
        } catch (Exception e) {
            System.out.println("房子图片加载失败: " + e.getMessage());
        }

    }

    /**
     * 当地块等级或拥有者变化时，调用此方法更新所有地块的房子显示
     */
    public void refreshAllHouseDisplays() {
        for (Land land : lands) {
            Pane landPanel = landPanels.get(land.getId());
            if (landPanel != null) {
                updateHousesOnLand(land, landPanel);
            }
        }
    }

    /**
     * 难度 **
     * 负责人：托尼
     **功能：
     * 更加丰富展现地块信息弹窗*/
    private void showLandInfoDialog(Land land) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("地块信息");
        alert.setHeaderText(null);
        alert.setContentText(
                "名称: " + land.getName() + "\n" +
                        "类型: " + land.getType().name() + "\n" +
                        "价格: " + land.getPrice() + "\n" +
                        "租金: " + land.getRent()
        );
        alert.showAndWait();
    }

    /**
     * 显示骰子结果
     */
    public void showDiceResult(int steps, int currentPlayerIndex) {
        int playerNum = currentPlayerIndex % 4 + 1;
        if (diceResultLabel != null) {
            diceResultLabel.setText("骰子结果: 玩家" + playerNum + "摇到点数: " + steps);
        }
    }

    /**
     * 等待玩家投骰子
     */
    public CompletableFuture<Integer> waitForDiceRoll() {
        diceRollFuture = new CompletableFuture<>();
        return diceRollFuture;
    }

    /**
     * 设置骰子按钮状态
     */
    public void setDiceButtonEnabled(boolean enabled) {
        rollDiceButton.setDisable(!enabled);
    }


    /**
     * 询问是否购买地块
     */
    public boolean askToBuyLand(Land land) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("购地契约");
        alert.setHeaderText(null);
        alert.setContentText(
                "名称: " + land.getName() + "\n" +
                        "价格: " + land.getPrice() + "\n" +
                        "是否购买？"
        );

        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    /**
     * 显示卡牌信息
     */
    public void showCardInfo(Cards card) {
        showAlert("卡牌信息",
                "卡牌ID: " + card.getId() + "\n" +
                        "描述: " + card.getDescription() + "\n" +
                        "类型: " + card.getType());
    }

    /**
     * 显示游戏结束信息
     */
    public void showGameOver(Player winner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("游戏结束");
        alert.setHeaderText(null);
        alert.setContentText("获胜者是: " + winner.getName() + "\n是否重新开始游戏？");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                gameStage.close();
                new Game(this,this); // 重新开始游戏
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * 通用提示框
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(gameStage != null ? gameStage : primaryStage);
        alert.showAndWait();
    }

    // 程序入口
    public static void main(String[] args) {
        launch(args);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public String readLine() {
        System.out.println("【Service】准备 take...");
        try {
            String s = inputQueue.take();
            System.out.println("【Service】take 到 >" + s + "<");
            return s;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }
}