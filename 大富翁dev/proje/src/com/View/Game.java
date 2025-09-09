package com.View;

import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private Service service;
    private GUI ui;
    private int currentPlayerIndex;

    public Game() {
        this.service = new Service();
        this.ui = new GUI();
        this.currentPlayerIndex = 0;
        service.init();
    }

// 开始游戏

    /**
     * 难度：****
     * 负责人：雷钧辉
     * 功能：开始游戏
     * 1. 显示欢迎信息
     * 2. 循环执行以下操作，直到游戏结束
     * 2.1 获取当前玩家
     * 2.2 显示当前玩家信息
     * 2.3 摇筛子
     * 2.4 处理玩家行动
     * 2.5 判断游戏是否结束
     * 2.6 如果游戏结束，显示游戏结束信息，退出循环
     * 2.7 切换到下一个玩家
     */


    public void startGame() {
        //获得地图
        GameBoard gameBoard = new GameBoard();
        List<Land> lands = gameBoard.getLands();
        while(true){
            List<Player> players = service.getPlayers();
            Player currentPlayer = players.get(currentPlayerIndex);
            ui.showPlayerInfo(service);
            int steps = Service.rollDice();
            ui.showDiceResult(steps,currentPlayerIndex);
            service.handlePlayerAction(currentPlayer, steps,service);
            Land currentLand = service.gameBoard.getLandById(currentPlayer.getPosition());
            if (currentLand != null) {
                service.handleLandEvent(currentPlayer, currentLand);
            }

            if (service.isGameOver()) {
                Player winner = service.findWinner();
                ui.showGameOver(winner);
                ui.showPlayerInfo(service);
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
}