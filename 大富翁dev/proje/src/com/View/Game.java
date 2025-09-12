package com.View;
/*
 * 在JavaFx下绘制地图
 *
 * */
import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.InputCallback;
import com.Service.Service;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Game {
    private Service service;
    private GUI ui;
    private int currentPlayerIndex;

    public Game(InputCallback callback, GUI ui) { // 把外部 GUI 也传进来
        this.service = new Service(callback);
        this.ui = ui;
        this.currentPlayerIndex = 0;
        service.init();
    }

    /**
     * 负责人：李亦航
     * 功能：
     * 1，实现骰子的窗口可视化展示
     * 2，添加等待玩家投骰子功能
     * */

    /**
     * 开始游戏主循环
     */
    public void startGame() {

        service.init();

        //创建GameBoard实例
        GameBoard gameBoard = new GameBoard();
        List<Land> lands = gameBoard.getLands();

        // 启动JavaFX界面（UI线程操作）
        Platform.runLater(() -> ui.showGameStage());

        // 将游戏主循环放入新线程，避免阻塞UI线程
        new Thread(() -> {
            while (true) {
                List<Player> players = service.getPlayers();
                Player currentPlayer = players.get(currentPlayerIndex);

                // 显示玩家信息（UI操作必须在UI线程）
                Platform.runLater(() -> {
                    ui.showPlayerInfo(service);
                    ui.setDiceButtonEnabled(true);
                });


                try {
                    // 等待玩家点击骰子按钮
                    CompletableFuture<Integer> diceFuture = ui.waitForDiceRoll();
                    int steps = diceFuture.get(); // 这会阻塞直到玩家点击骰子按钮

                    // 处理玩家行动
                    service.handlePlayerAction(currentPlayer, steps, service);
                    Land currentLand = service.gameBoard.getLandById(currentPlayer.getPosition());

                    if (currentLand != null && currentPlayer != null) {
                        service.handleLandEvent(currentPlayer, currentLand);
                    }

                    // 判断游戏是否结束
                    if (service.isGameOver()) {
                        Player winner = service.findWinner();
                        Platform.runLater(() -> {
                            ui.showGameOver(winner);
                            ui.showPlayerInfo(service);
                        });
                        break;
                    }


                    // 切换玩家
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //为GUI提供当前玩家id检索
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    // 提供获取服务的方法供界面使用
    public Service getService() {
        return service;
    }

}