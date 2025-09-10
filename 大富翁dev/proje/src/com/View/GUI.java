package com.View;
/*
 * 从swing转到了JavaFx
 * 接下来在JavaFx下重新绘制地图
 *
 * */
import com.Pojo.Cards;
import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.Service;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {
    private Stage primaryStage;
    private Stage gameStage;
    private Game game;
    private boolean isMusicOn = true;
    private ArrayList<Land> lands;
    private ArrayList<Player> players;

    public GUI(Game game) {
        this.game = game;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcomeMessage();
    }

    /**
     * 显示欢迎界面
     */
    public void showWelcomeMessage() {
        primaryStage.setTitle("开始界面");
        primaryStage.setWidth(400);
        primaryStage.setHeight(400);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        String[] menuItems = {"开始游戏", "游戏设置", "排行榜", "退出游戏"};
        for (String item : menuItems) {
            Button button = new Button(item);
            button.setPrefSize(100, 30);
            button.setOnAction(e -> handleWelcomeAction(item));
            root.getChildren().add(button);
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
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
     * 显示游戏主界面
     */
    public void showGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("大富翁游戏");
        gameStage.setWidth(1000);
        gameStage.setHeight(600);
        gameStage.setOnCloseRequest(e -> System.exit(0));

        // 初始化空布局，等待数据加载后更新
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        gameStage.setScene(scene);
        gameStage.show();
    }

    /**
     * 显示玩家信息
     */
    public void showPlayerInfo(Service service) {
        if (gameStage == null) return;

        players = (ArrayList<Player>) service.getPlayers();
        GameBoard gameBoard = service.gameBoard;
        lands = (ArrayList<Land>) gameBoard.getLands();

        BorderPane root = (BorderPane) gameStage.getScene().getRoot();

        // 玩家信息展示区
        VBox centerPane = new VBox(10);
        centerPane.setPadding(new Insets(10));

        // 顶部空面板（原按钮区域）
        HBox topCenter = new HBox();
        topCenter.setPrefHeight(50);

        // 玩家信息面板
        HBox playerBox = new HBox(20);
        playerBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            playerBox.getChildren().add(createPlayerPanel(players.get(i)));
        }

        centerPane.getChildren().addAll(topCenter, playerBox);
        root.setCenter(centerPane);

        // 北部地块
        HBox northLands = new HBox(5);
        northLands.setPadding(new Insets(5));
        for (int i = 1; i <= 5; i++) {
            northLands.getChildren().add(createLandPanel(lands.get(i)));
        }
        root.setTop(northLands);

        // 西部地块
        VBox westLands = new VBox(5);
        westLands.setPadding(new Insets(5));
        westLands.getChildren().addAll(
                createLandPanel(lands.get(14)),
                createLandPanel(lands.get(13))
        );
        root.setLeft(westLands);

        // 东部地块
        VBox eastLands = new VBox(5);
        eastLands.setPadding(new Insets(5));
        eastLands.getChildren().addAll(
                createLandPanel(lands.get(6)),
                createLandPanel(lands.get(7))
        );
        root.setRight(eastLands);

        // 南部地块
        HBox southLands = new HBox(5);
        southLands.setPadding(new Insets(5));
        for (int i = 12; i >= 8; i--) {
            southLands.getChildren().add(createLandPanel(lands.get(i)));
        }
        root.setBottom(southLands);
    }

    /**
     * 创建玩家信息面板
     */
    private VBox createPlayerPanel(Player player) {
        VBox panel = new VBox(5);
        panel.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT
        )));
        panel.setPadding(new Insets(5));
        panel.setPrefWidth(150);

        panel.getChildren().addAll(
                new Label("ID: " + player.getId()),
                new Label("名称: " + player.getName()),
                new Label("现金: " + player.getMoney()),
                new Label("位置: " + lands.get(player.getPosition()).getName())
        );
        return panel;
    }

    /**
     * 创建地块信息面板
     */
    private VBox createLandPanel(Land land) {
        VBox panel = new VBox(3);
        panel.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT
        )));
        panel.setPadding(new Insets(3));
        panel.setPrefWidth(120);

        Label nameLabel = new Label("名称: " + land.getName());
        nameLabel.setFont(new Font(10));
        Label typeLabel = new Label("类型: " + land.getType().name());
        typeLabel.setFont(new Font(10));
        Label priceLabel = new Label("价格: " + land.getPrice());
        priceLabel.setFont(new Font(10));
        Label rentLabel = new Label("租金: " + land.getRent());
        rentLabel.setFont(new Font(10));

        panel.getChildren().addAll(nameLabel, typeLabel, priceLabel, rentLabel);
        return panel;
    }

    /**
     * 显示骰子结果
     */
    public void showDiceResult(int steps, int currentPlayerIndex) {
        int playerNum = currentPlayerIndex % 4 + 1;
        showAlert("骰子结果", "玩家" + playerNum + "摇到点数: " + steps);
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
                new Game(); // 重新开始游戏
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
}