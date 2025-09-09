package com.View;

import com.Pojo.Cards;
import com.Pojo.GameBoard;
import com.Pojo.Land;
import com.Pojo.Player;
import com.Service.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

//用户界面类
public class GUI {

    // private Scanner scanner = new Scanner(System.in);
    JFrame frame = new JFrame("开始界面");//开始界面
    JFrame Frame2 = new JFrame("大富翁游戏");//游戏界面
    private boolean isMusicOn = true;
    private JFrame settingsFrame;
    private JCheckBox musicCheckBox;
    private ArrayList<Land> lands;
    private ArrayList<Player> players;
     int step = 0;//回合数

    public void showWelcomeMessage() {
        /**
         * 显示欢迎信息
         * 难度：****
         * 负责人：董子铭
         * 功能：再开始界面弹一个小窗提示用户表示欢迎：欢迎来到大富翁游戏！
         *      并在开始界面展示相应选项并操作
         */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);

        String[] menuItems = {"开始游戏", "游戏设置", "排行榜", "退出游戏"};
        for (int i = 0; i < menuItems.length; i++) {
            JButton button = new JButton(menuItems[i]);
            button.setBounds(150, 70 + i * 65, 100, 30);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("退出游戏")) {
                        System.exit(0);
                    }else if(e.getActionCommand().equals("开始游戏")){
//                        frame.setVisible(false);
                        frame.dispose();
                        Game game = new Game();
                        game.startGame();
                    }else if(e.getActionCommand().equals("排行榜")){
                        System.out.println("暂无此功能");
                    }else if(e.getActionCommand().equals("游戏设置")){
                        showSettingsFrame();
                    }
                }
            });
            frame.add(button);
        }
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public void showSettingsFrame() {

        settingsFrame = new JFrame("游戏设置");
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭设置窗口时不会关闭主窗口
        settingsFrame.setSize(300, 200);
        settingsFrame.setLocationRelativeTo(null);

        // 创建音乐开关复选框
        musicCheckBox = new JCheckBox("开启音乐", isMusicOn);
        musicCheckBox.setBounds(50, 50, 200, 30);
        musicCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMusicOn = musicCheckBox.isSelected();
                if (isMusicOn) {
                    System.out.println("音乐已开启");
                    //MusicPlayer  musicPlayer = new MusicPlayer();
                    //musicPlayer.playBackgroundMusic(isMusicon);
                } else {
                    System.out.println("音乐已关闭");
                    //MusicPlayer  musicPlayer = new MusicPlayer();
                    //musicPlayer.playBackgroundMusic(isMusicon);(在MusicPlayer中加入局部变量,为假直接退出)
                }
            }
        });

        // 添加复选框到设置窗口
        settingsFrame.getContentPane().setLayout(null);
        settingsFrame.getContentPane().add(musicCheckBox);

        // 添加确认按钮
        JButton confirmButton = new JButton("确认");
        confirmButton.setBounds(100, 120, 100, 30);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsFrame.dispose(); // 关闭设置窗口
            }
        });
        settingsFrame.getContentPane().add(confirmButton);

        settingsFrame.setVisible(true);
    }




    public void showPlayerInfo(Service service) {
        /**
         * 显示当前玩家信息
         * 难度：*****
         * 负责人：刘颖
         * 参数： player 当前玩家
         * 功能：在游戏界面展示给用户看有关的信息
         * 例如：玩家名称、现金、位置、地图等信息
         */
        players = (ArrayList<Player>) service.getPlayers();
        GameBoard gameBoard = service.gameBoard;
        lands = (ArrayList<Land>) gameBoard.getLands();
        Frame2.setBounds(500,250,1000,600);
        Container contentPane = Frame2.getContentPane();
        contentPane.setLayout(new BorderLayout());
        //玩家信息展示区
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new GridLayout(3,1));

        JPanel jPanels = new JPanel(new GridLayout(1,3));
        jPanels.add(new JPanel());
//        JButton action = new JButton("点击摇骰子");
//        action.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int random = service.rollDice();
//                showDiceResult(random);
//                service.handlePlayerAction(players.get(step%4), random);
//                showPlayerInfo(service);
//                Player player = players.get(step%players.size()+1);
//                service.handleLandEvent(player,lands.get(player.getPosition()));
//
//
//                //询问是否购买
//                Player play = players.get(step%players.size()+1);
//                service.handleLandEvent(play,lands.get(play.getPosition()));
//                step++;
//
//            }
//        });
//        jPanels.add(action);
        jPanels.add(new JPanel());
        JPanel xia = new JPanel(new GridLayout(1,6));

        jPanel1.add(jPanels);

        xia.add(new JPanel());
        //玩家一信息面板(中心)
        addPlayer(players.get(0),xia);
//       玩家二信息面板（中心）
        addPlayer(players.get(1),xia);
//       玩家三信息面板（中心）
        addPlayer(players.get(2),xia);
//       玩家四信息面板（中心）
        addPlayer(players.get(3),xia);
        xia.add(new JPanel());
        jPanel1.add(xia);
        jPanel1.add(new Label());


        contentPane.add(BorderLayout.CENTER,jPanel1);
//        上部分
        JPanel jPanel2 = new JPanel(new GridLayout(1,5));
        addLand(lands.get(1),jPanel2);
        addLand(lands.get(2),jPanel2);
        addLand(lands.get(3),jPanel2);
        addLand(lands.get(4),jPanel2);
        addLand(lands.get(5),jPanel2);

        contentPane.add(BorderLayout.NORTH,jPanel2);

//        左部分

//        左部分
        JPanel jPanel3 = new JPanel();
        GridLayout gridLayout1 = new GridLayout(2, 1);
        gridLayout1.setVgap(10);
        jPanel3.setLayout(gridLayout1);
        addLand(lands.get(14),jPanel3);
        addLand(lands.get(13),jPanel3);

        contentPane.add(BorderLayout.WEST,jPanel3);
//      右部分
        JPanel jPanel4 = new JPanel(new GridLayout(2,1));
        addLand(lands.get(6),jPanel4);
        addLand(lands.get(7),jPanel4);

        contentPane.add(BorderLayout.EAST,jPanel4);
//       下部分
        JPanel jPanel5 = new JPanel(new GridLayout(1,5));
        addLand(lands.get(12),jPanel5);
        addLand(lands.get(11),jPanel5);
        addLand(lands.get(10),jPanel5);
        addLand(lands.get(9),jPanel5);
        addLand(lands.get(8),jPanel5);
        contentPane.add(BorderLayout.SOUTH,jPanel5);
        Frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Frame2.setVisible(true);
//        上部分
        JPanel jPanel22 = new JPanel(new GridLayout(1,5));
        addLand(lands.get(1),jPanel22);
        addLand(lands.get(2),jPanel22);
        addLand(lands.get(3),jPanel22);
        addLand(lands.get(4),jPanel22);
        addLand(lands.get(5),jPanel22);

        contentPane.add(BorderLayout.NORTH,jPanel22);

//        左部分
        JPanel jPanel33 = new JPanel();
        GridLayout gridLayout11 = new GridLayout(2, 1);
        gridLayout1.setVgap(10);
        jPanel33.setLayout(gridLayout11);
        addLand(lands.get(6),jPanel33);
        addLand(lands.get(7),jPanel33);


        contentPane.add(BorderLayout.WEST,jPanel3);
//      右部分
        JPanel jPanel44 = new JPanel(new GridLayout(2,1));
        addLand(lands.get(8),jPanel44);
        addLand(lands.get(9),jPanel44);

        contentPane.add(BorderLayout.EAST,jPanel4);
//       下部分
        JPanel jPanel55 = new JPanel(new GridLayout(1,5));
        addLand(lands.get(10),jPanel55);
        addLand(lands.get(11),jPanel55);
        addLand(lands.get(12),jPanel55);
        addLand(lands.get(13),jPanel55);
        addLand(lands.get(14),jPanel55);
        contentPane.add(BorderLayout.SOUTH,jPanel55);









        Frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Frame2.setVisible(true);

    }
    public  void addLand(Land land,JPanel jPanel){
        JPanel new_jPanel = new JPanel(new GridLayout(6,1));
        JLabel jLabel1 = new JLabel("名称："+land.getName());
        JLabel jLabel2 = new JLabel("类型："+land.getType().name());
//        JLabel jLabel3 = new JLabel("所有者："+land.getOwner().getName());

        JLabel jLabel4 = new JLabel("价格"+String.valueOf(land.getPrice()));
        JLabel jLabel5 = new JLabel("租金："+land.getRent());
        JLabel jLabel6 = new JLabel("租金："+land.getRent());
        JLabel jLabel7 = new JLabel("租金："+land.getRent());
        new_jPanel.add(jLabel1);
        new_jPanel.add(jLabel2);
//        new_jPanel.add(jLabel3);
        new_jPanel.add(jLabel4);
        new_jPanel.add(jLabel6);
        new_jPanel.add(jLabel7);
        jPanel.add(new_jPanel);
    }


    public void addPlayer(Player player,JPanel jPanel){
        JPanel play_jPanel4 = new JPanel();
        GridLayout gridLayout = new GridLayout(4, 1);
        gridLayout.setVgap(10);
        play_jPanel4.setLayout(gridLayout);
        JLabel jLabeld1 = new JLabel("id："+String.valueOf(player.getId()));
        JLabel jLabeld2 = new JLabel("名称："+player.getName());
        JLabel jLabeld3 = new JLabel("现金："+player.getMoney());
        JLabel jLabeld4 = new JLabel("位置："+lands.get(player.getPosition()).getName());
        play_jPanel4.add(jLabeld1);//玩家Id
        play_jPanel4.add(jLabeld2);//玩家名字
        play_jPanel4.add(jLabeld3);//玩家现金
        play_jPanel4.add(jLabeld4);//玩家位置
        jPanel.add(play_jPanel4);
    }



    public void showDiceResult(int steps, int currentPlayerIndex) {
        /**
         * 难度：****
         * 负责人：董子铭
         * 参数：抛出筛子 steps 骰子点数
         * 功能：在游戏界面弹一个小窗显示骰子结果，展示给用户看
         *
         */

        int player = currentPlayerIndex%4+1;
        JOptionPane.showMessageDialog(Frame2, "玩家"+player+"摇到点数: " + steps, "骰子结果", JOptionPane.INFORMATION_MESSAGE);

    }


    public void showLandInfo(Land land) {
        /**
         * 显示地块信息
         * 难度：*****
         * 负责人：刘颖
         * 参数： land 当前地块
         * 功能：显示给用户看有关的信息
         * 例如：抵达的地块名称、类型、所有者、价格、租金等
         */

    }


    public boolean askToBuyLand(Land land) {
        /**
         * 询问是否购买地块
         * 难度：*****
         * 负责人：刘颖
         * 参数一： land 地块信息
         * 参数二： true表示购买，false表示不购买
         * 功能：游戏界面展示地块名称、价格等信息，询问用户是否购买
         * 例如：是否购买地产：名称、价格、所有者等信息
         * 返回值： true表示购买，false表示不购买
         */
        JDialog jDialog = new JDialog();
        jDialog.setTitle("购地契约");
        jDialog.setBounds(430,430,180,150);
        jDialog.setResizable(false);
        jDialog.setLayout(new GridLayout(4,1));
        JLabel TjLabel1 = new JLabel("名称：" + land.getName());
        JLabel TjLabel2 = new JLabel("价格：" + land.getRent());
        jDialog.add(TjLabel1);
        jDialog.add(TjLabel2);
        JLabel jLabel1 = new JLabel("确认购买？");
        final int[] flag = {0};
        jDialog.add(jLabel1);

        JLabel jLabel2 = new JLabel();
        jLabel2.setLayout(new GridLayout(1,2));
        JButton jButton1 = new JButton("购买");
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("该买成功！");
                jDialog.setVisible(false);
                flag[0] = 1;
            }
        });
        JButton jButton2 = new JButton("再想想");
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("取消购买！");
                jDialog.setVisible(false);
            }
        });
        jLabel2.add(jButton1);
        jLabel2.add(jButton2);
        jDialog.add(jLabel2);
        jDialog.setVisible(true);
        if(flag[0] == 1) return true;
        return false;
    }


    public void showCardInfo(Cards card) {
        /**
         * 显示卡牌信息
         *难度：*****
         * 负责人：董子铭
         * 参数 card 抽取的卡牌
         * 功能：游戏界面展示给用户看有关的信息
         * 例如：抽到的卡牌名称、描述、类型等信息
         */
        JOptionPane.showMessageDialog(Frame2,
                "卡牌ID: " + card.getId() + " " +
                        "描述: " + card.getDescription() + " " +
                        "类型: " + card.getType(), "卡牌信息", JOptionPane.INFORMATION_MESSAGE);
    }


    public void showGameOver(Player winner) {
        /**
         * 难度：***
         * 负责人：董子铭
         * 参数： winner 获胜玩家
         * 功能：在游戏界面弹小窗显示游戏结束信息，玩家读取信息后隐藏游戏界面，显示开始界面
         */
        int option = JOptionPane.showConfirmDialog(Frame2,
                "游戏结束！获胜者是: " + winner.getName() + " 是否重新开始游戏？",
                "游戏结束", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            showWelcomeMessage();
            Frame2.setVisible(false);




        } else {
            System.exit(0); // 退出程序
        }
    }

    public JFrame getFrame2() {
        return Frame2;
    }

    public void setFrame2(JFrame frame2) {
        Frame2 = frame2;
    }
}
