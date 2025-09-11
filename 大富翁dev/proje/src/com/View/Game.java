package com.View;
/*
 * 在JavaFx下绘制地图
 *
 * */
import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.Service;
import javafx.application.Platform;

import java.util.List;

public class Game {
    private Service service;
    private GUI ui;
    private int currentPlayerIndex;

    public Game() {
        this.service = new Service();
        this.ui = new GUI(this); // 传入Game实例供界面回调
        this.currentPlayerIndex = 0;
        service.init();
    }


    /**
     * 开始游戏主循环
     */
    public void startGame() {



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
                Platform.runLater(() -> ui.showPlayerInfo(service));

                int steps = Service.rollDice();

                // 显示骰子结果（UI线程）
                Platform.runLater(() -> ui.showDiceResult(steps, currentPlayerIndex));

                // 处理玩家行动
                service.handlePlayerAction(currentPlayer, steps, service);
                Land currentLand = service.gameBoard.getLandById(currentPlayer.getPosition());
                if (currentLand != null) {
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

                // 可选：添加小延迟，避免循环过快导致UI卡顿
                try {
                    Thread.sleep(500); // 500ms延迟，可根据需要调整
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // 启动游戏逻辑线程
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