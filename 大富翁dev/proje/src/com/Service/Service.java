package com.Service;

import com.Pojo.*;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Service {

    // 在GUI和Service间传输入数据的接口实例//新增
    private final InputCallback in;

    // 玩家列表
    private List<Player> players;
    // 地块列表
    private List<Land> lands;
    // 卡牌列表
    public CardManager cards;

    public GameBoard gameBoard;
    private int boardsize=20;//格子数目


    //【这个方法暂时不需要】
//    private void useCard(int choice,List<Cards> playerCards,Player player){
//
//        if (choice > 0 && choice <= playerCards.size()) {
//            Cards selectedCard = playerCards.get(choice - 1);
//            // 调用 choiceCard 方法使用所选择的卡牌对应的方法
//            choiceCard(player, selectedCard);
//            // 使用后移除卡牌
//            player.getCards().removeCard(selectedCard);
//
//        } else {
////            System.out.println(player.getName() + " 编号使用错误，重新输入: ");
//            System.out.println("这句话应该不会被打印出来，哈哈");
//
//        }
//
//    }


    public Service(InputCallback inputCallback) {
        //----新增
        this.in = inputCallback;
        System.out.println("Service 收到的 callback = " + inputCallback.getClass());
        System.out.println("Service 收到回调实例 = " + System.identityHashCode(inputCallback));
        //---
        this.players = new ArrayList<>();
        this.lands = new ArrayList<>();
        this.gameBoard = new GameBoard();
    }

    /**
     难度： ***
     负责人：毕哲晖
     功能：初始化游戏数据
     **/
    public void init() {

        // 初始化玩家

        players.add(new Player(1, "玩家1", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager(),1));
        players.add(new Player(2, "玩家2", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager(),2));
        players.add(new Player(3, "玩家3", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager(),3));
        players.add(new Player(4, "玩家4", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager(),4));

        // 初始化地块
        //()()
        this.gameBoard.initializeLands();
        lands = this.gameBoard.getLands();
        // 初始化卡牌
        //.
        this.cards=new CardManager();
        cards.initializeCards();
        //.
    }


    /** 摇骰子
     难度：*
     负责人：
     功能：返回一个1~6随机整数
     **/
    public static int rollDice() {
        try {
            // 让线程暂停500毫秒
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // 如果线程被中断，打印堆栈跟踪
            e.printStackTrace();
        }
        int i = (int)(Math.random()*6);
        return i + 1;
    }


    /** 补充函数
     难度： ***
     负责人：
     功能：处理玩家行动，被handlePlayerAction调用
     1. 移动玩家到新位置
     2. 处理地块事件  handleLandEvent
     3. 处理卡牌事件 handleCardEvent
     **/

    public void handlePlayerMove(Player player, int steps) {
//        【】【】写路障卡的逻辑
        //【】【】处理休眠、破产等
        if (player.isInJail()) {

            if (player.outJailNum > 0) {

//                【】【】

                System.out.println("是否使用出狱卡，输入Yes或No");
                String s = sc.nextLine();
                if(s=="Yes"){
                    player.outJailNum--;
                    player.setInJail(false);
                    System.out.println(player.getName() + "成功出狱！");

                }
            } else {
                System.out.println(player.getName() + " 被关在监狱中，无法移动！");
                player.setInJail(false);//()
//                ()
                return ;
//                ()
            }
        }
        if(player.getTurtleCount()>0){
            System.out.println("该玩家还处在乌龟状态中，只能移动一格");
            player.setPosition(player.getPosition()+1);
            player.setTurtleCount(player.getTurtleCount()-1);
            return ;
        }
        if(player.isBankruptcy()){
            System.out.println("该玩家已破产");
            return ;
        }
        if(player.isIsSleep()){
            System.out.println("该玩家还在休眠，不能移动");
            player.setIsSleep(true);
            return ;
        }

//我正在尝试一下使用pullrequest进行分支合并，看下流程和效果
        player.setPosition((player.getPosition()+steps)%lands.size());//移动玩家到新位置
    }



    /**
     处理
     难度：**
     负责人：
     **/

    public void handlePlayerAction(Player player, int steps,Service service) {
//        【】【】
        if(player.breakMoney()){
            System.out.println("该玩家已破产");
            return ;
        }

        useItemCard(player);//道具卡逻辑
        if(player.isIsSleep()){
            System.out.println("该玩家还在休眠，不能移动");
            return ;
        }

        // 【】【】处理移动玩家位置的逻辑
        handlePlayerMove(player,steps);
        //        【】【】

//        player.move(steps,boardsize);//移动玩家到新位置【】【】
        Land templand=service.gameBoard.getLandById(player.getPosition());
        //这个地方根据地块类型的不同调用不同方法,如下示例
        if (templand!=null) {//【】【】
            switch (templand.getType()) {
                //            【】【】
                case CHANCE -> handleCardEvent(player,service.cards.drawChanceCard());
                //【】【】
                case PROPERTY -> service.handleLandEvent(player, templand);
                case STATION -> service.handleStation(player);
                case JAIL -> {
                    System.out.println("你到达了监狱,下一回合将被关在监狱。");
                    if (player.outJailNum > 0) {

                        //                【】【】
                        System.out.println("是否使用出狱卡？输入Yes或No");
                        String s = sc.nextLine();
                        if(s=="Yes"){
                            player.outJailNum--;
                            player.setInJail(false);
                            System.out.println(player.getName() + "成功出狱！");
                        }
                    }else{
                        player.setInJail(false);
                    }

                }
                case  FREE_PARKING -> {
                    System.out.println("你到达了停车场,交款20");
                    player.setMoney(player.getMoney()-20);
                }
                case COMMUNITY_CHEST ->handleCardEvent(player,service.cards.drawCommunityChestCard());

                case UTILITY -> service.handleLandEvent(player, templand);
            }
        }

    }

    /**
     处理地块事件
     难度：**
     负责人：
     * 功能：处理车站事件
     *
     * */
    private void handleStation(Player player) {
        Land templand=gameBoard.getLandById(player.getPosition());
        System.out.println("你到达了火车站,");

        while (true) {
            System.out.print("请输入你想前进多少步:");
            Scanner sc=new Scanner(System.in);
            Integer foot = (Integer)(sc.nextInt());
            if(foot>0&&foot<=19){
                player.move(foot,boardsize);
                break;
            }else if(foot>19){
                System.out.println("你输入的步数超过19，请重新输入步数");
            }
            else{
                System.out.println("真男人从不回头，请重新输入步数");
            }
        }


    }

    /**
     * 功能：玩家使用道具卡
     * 难度：***
     * 负责人：新增
     * 步骤：
     * 1. 展示玩家拥有的卡及其具体信息
     * 2. 让玩家选择要使用的卡
     * 3. 调用 choiceCard 方法处理相应的卡牌
     */
    public void useItemCard(Player player) {


        //把机会卡和命运卡合并，存放到一个列表中

        //-------------------
//        List<Cards> playerCards = player.getCards().getChanceCards();
//        playerCards.addAll(player.getCards().getCommunityChestCards());
//
        if (player.getTurtleNum() == 0 && player.getExchangeNum() == 0 && player.getBlockNum() == 0 && player.getSleepNum() == 0) {
            System.out.println(player.getName() + " 没有道具卡可以使用。");
            return;
        }
        //---------------------

        System.out.println(player.getName() + " 拥有的道具卡如下：");
//        for (int i = 0; i < playerCards.size(); i++) {
//            Cards card = playerCards.get(i);
//            int cardId = card.getId();
//            // CardManager 中一个方法可以根据 cardId 获取卡牌信息
//            String cardInfo = CardManager.getCardInfoById(cardId);
//            System.out.println((i + 1) + ". " + cardInfo);
            List<Integer> cardIds = new ArrayList<>();//用于记录玩家当前所有道具卡的id
            int cnt =0;
            if(player.getTurtleNum()>0){
                cnt++;System.out.println((cnt)+". 乌龟卡：剩余"+player.getTurtleNum()+"张");
                cardIds.add(5);
            }
            if(player.getSleepNum()>0){
                cnt++;System.out.println((cnt)+". 休眠卡：剩余"+player.getSleepNum()+"张");
                cardIds.add(6);
            }
            if(player.getOutJailNum()>0){cnt++;System.out.println((cnt)+". 出狱卡：剩余"+player.getOutJailNum()+"张");
                cardIds.add(4);
            }
            if(player.getExchangeNum()>0){cnt++;System.out.println((cnt)+". 换位卡：剩余"+player.getExchangeNum()+"张");
                cardIds.add(12);
            }
            if(player.getBlockNum()>0){cnt++;System.out.println((cnt)+". 路障卡：剩余"+player.getBlockNum()+"张");
                cardIds.add(13);
            }
//        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("是否使用道具卡？请输入Yes或No：");
        String get = sc.nextLine();

        if ("Yes".equals(get)) {
            System.out.println("请输入要使用的道具卡编号（1 - " + cnt + "），输入 0 取消使用：");
            int choice;
            while (true) {
                choice = scanner.nextInt();
                if (choice > 0 && choice <=cnt) {
                    int cardId = cardIds.get(choice - 1);
                    useToolCard(player, cardId);
                    break;
                } else {
                    System.out.println(player.getName() + " 编号使用错误，重新输入: ");

                }
            }
            //玩家使用手头已有的道具卡

        }


    }


    /**
     处理地块购买或过路费
     难度：**
     负责人：
     >>>>>>> e49a326f50bd92cfd187ee68b798ab9db981a621
     * 功能：处理地块事件
     * 先判断这个地块的拥有者是谁，null就是没有人，不是null就是别的玩家
     * 1. 地块未被购买，询问玩家是否购买
     * 2. 地块已被其他玩家购买，支付租金
     *
     * */

    public void handleLandEvent(Player player, Land land) {
        Player owner = land.getOwner();
        if (land.isPurchasable()&&owner == null) {
            // 地块未被购买，询问玩家是否购买
            // 这里简单假设询问玩家的逻辑用一个方法 askPlayerToBuy 表示
            Scanner scanner = new Scanner(System.in);
            System.out.println("地块 " + land.getName() + " 未被购买，价格为 " + land.getPrice() + "，你是否要购买？");
            System.out.println("请输入 Y 表示购买，N 表示不购买：");
            String input = scanner.nextLine();
            if ("Y".equalsIgnoreCase(input)) {
                // 玩家决定购买
                if (player.getMoney() >= land.getPrice()) {
                    player.setMoney(player.getMoney() - land.getPrice());
                    land.setOwner(player);
                    player.addLandOfPlayer(land);


                    //【连携逻辑】如果玩家已经买过这个系列的，则这个系列其他玩家路过时的过路费增加
                    //拉曼却领 系列连携
                    if(land.getId() == 1){
                        Land anotherLand = gameBoard.getLandById(11);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }
                    if(land.getId() == 11){
                        Land anotherLand = gameBoard.getLandById(1);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }



                    //Cafe Stella 系列连携
                    if(land.getId() == 4){
                        Land anotherLand = gameBoard.getLandById(14);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }
                    if(land.getId() == 14){
                        Land anotherLand = gameBoard.getLandById(4);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }

                    //中南大学 系列连携
                    if(land.getId() == 2){
                        Land anotherLand = gameBoard.getLandById(12);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }
                    if(land.getId() == 12){
                        Land anotherLand = gameBoard.getLandById(2);
                        if(player.getLandOfPlayer().contains(anotherLand)){
                            land.setRent(land.getRent()+50);//[过路费加倍]
                        }
                    }



                    //巨人 系列连携（3）
                    // 巨人 系列连携（3个地皮，id分别为5/7/9）
                    if (land.getId() == 5) {
                        Land anotherLand1 = gameBoard.getLandById(7);
                        Land anotherLand2 = gameBoard.getLandById(9);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    } else if (land.getId() == 7) {
                        Land anotherLand1 = gameBoard.getLandById(5);
                        Land anotherLand2 = gameBoard.getLandById(9);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    } else if (land.getId() == 9) {
                        Land anotherLand1 = gameBoard.getLandById(5);
                        Land anotherLand2 = gameBoard.getLandById(7);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    }





                    //下北泽 系列连携

                    if (land.getId() == 15) {
                        Land anotherLand1 = gameBoard.getLandById(17);
                        Land anotherLand2 = gameBoard.getLandById(19);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    } else if (land.getId() == 17) {
                        Land anotherLand1 = gameBoard.getLandById(15);
                        Land anotherLand2 = gameBoard.getLandById(19);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    } else if (land.getId() == 19) {
                        Land anotherLand1 = gameBoard.getLandById(15);
                        Land anotherLand2 = gameBoard.getLandById(17);
                        if (player.getLandOfPlayer().contains(anotherLand1) && player.getLandOfPlayer().contains(anotherLand2)) {
                            land.setRent(land.getRent() + 50); // [过路费加倍]
                        }
                    }





                    System.out.println(player.getName()+"成功购买了地块 " + land.getName());
                    System.out.println(player.getName()+"剩余金钱：" + player.getMoney());

                    //调用建房子函数
                    int houseCost = land.getPrice();
                    buildHouse(player,land,houseCost);

                } else {
                    System.out.println(player.getName()+"金钱不足，无法购买。");
                }
            }
        } else if (owner != player&&owner!=null) {//【】【】
            // 地块已被其他玩家购买，支付租金
            System.out.println("地块 " + land.getName() + " 已被 " + owner.getName() + " 购买，需要支付租金 " + land.getRent() );
            int rent = land.getRent();
            if (player.getMoney() >= rent) {
                player.setMoney(player.getMoney() - rent);
                owner.setMoney(owner.getMoney() + rent);
                System.out.println(player.getName()+"成功支付租金 " + rent + " 给 " + owner.getName());
                System.out.println(player.getName()+"剩余金钱：" + player.getMoney());
            } else {
                // 玩家金钱不足，可能需要额外处理，如破产等
                System.out.println(player.getName()+"金钱不足，无法支付租金。");
            }
        }else{
            //地块主人是自己
            // 判断是否可以建房子
            if(land.canBuildHouse(player)){
                //可以建房子
                int houseCost = land.getPrice();
                buildHouse(player,land,houseCost);

            }else if(land.isPurchasable()&&owner==player){//【】【】漏掉一个判断逻辑，导致会输出“玩家3拥有此地块：监狱”之类的结果
                System.out.println(player.getName()+"拥有此地块，且"+land.getName()+"建满了房子，无法继续建房" );
            }

        }
    }
    /*
     * 处理建房
     * 询问是否建房
     * 判断钱是否足够建房
     *
     */

    private void buildHouse(Player player,Land land,int houseCost) {
        System.out.println("你已购买了 "+land.getName() + " 是否要建房子？");
        System.out.println("请输入 Y 表示建房子，N 表示不建房子：");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        while (input.equals("Y")) {
            if(player.getMoney() >= houseCost){
    //                    （）
                if(land.getHouseCount()==4){
                    System.out.println("该土地已经不能再建房子了");
                    return;
                }
    //                    （）

                //-----------

                if ("Y".equalsIgnoreCase(input)) {
                    // 假设建房子有一个固定的花费，这里简单设为 100


                    //扣钱
                    player.setMoney(player.getMoney() - houseCost);
                    //加房子
                    land.setHouseCount(land.getHouseCount() + 1);
                    land.getActualRent();
                    System.out.println("你成功在地块 " + land.getName() + " 建造了房子，当前房子数量为 " + land.getHouseCount());


                    System.out.print("是否继续建房？Y 表示建房子，N 表示不建房子：");
                    input = sc.nextLine();
                }




            } else {
                System.out.println("钱不够你建房子了");
                break;
            }

        }
    }


    /**
     难度：**
     负责人：
     功能：处理卡牌事件
     参数一： player 玩家
     参数二： card 卡牌
     根据卡牌的id找到对应的功能
     调用函数choiceCard

     **/
    private void handleCardEvent(Player player, Cards card) {
        choiceCard(player, card);
    }

    /*
     * 使用道具卡牌
     * 参数一： player 玩家
     * 参数二： 卡牌id 玩家
     * 用卡牌id对应道具卡牌，调用相应的道具卡牌的方法
     */
    private  void useToolCard(Player player, int cardId){
        if(cardId==5){
            turtleCardMethod(player);
        }else if(cardId==6){
            sleepCardMethod(player);
        }else if(cardId==4){
            System.out.println("你现在不在监狱，无法使用出狱卡");
        }else if(cardId==12){
            exchangeCardMethod(player);
        }else if(cardId==13){
            blockCardMethod(player);
        }

    }


    /**
     选择卡牌
     难度：***
     负责人：
     功能：选择卡牌
     根据卡牌的id找到对应的功能
     例如id为1的卡牌调用method1
     例如id为2的卡牌调用method2
     .......以此类推
     * */
    private void choiceCard(Player player, Cards card) {
        int id = card.getId();

        switch (id) {
            case 0:
                method1(player);
                break;
            case 1:
                method2(player);
                break;
            case 2:
                method3(player);
                break;
            case 3:
                method4(player);
                break;
            case 4:
                method5(player);
                break;
            case 5:
                method6(player);
                break;
            case 6:
                method7(player);
                break;
            case 7:
                method8(player);
                break;
            case 8:
                method9(player);
                break;
            case 9:
                method10(player);
                break;
            case 10:
                method11(player);
                break;
            case 11:
                method12(player);
                break;
            case 12:
                method13(player);
                break;
            case 13:
                method14(player);
                break;

            default:
                System.out.println("未知卡牌ID");
        }
    }
    Scanner sc = new Scanner(System.in);

    private void method1(Player player) {
//        【】【】
        player.setPosition(6);//
        System.out.println(player.getName()+"已前往火车站");
        handleStation(player);
    }

    //前进到最近的公共设施
    private void method2(Player player) {
//        【】【】
        int currentIndex = player.getPosition();
        int closestStationId1 = 6;
        int closestStationId2 = 16;
        GameBoard gameBoard = new GameBoard();

        Land land = null;
        if(0<=currentIndex&&currentIndex<=5 || 17<=currentIndex&&currentIndex<=19){
            player.setPosition(closestStationId1);
            land = gameBoard.getLandById(closestStationId1);
            handleStation(player);
        }else if(7<=currentIndex&&currentIndex<=15){
            player.setPosition(closestStationId2);
            land = gameBoard.getLandById(closestStationId2);
            System.out.println("你到达了停车场,交款20");
            player.setMoney(player.getMoney()-20);
        }else{
            System.out.println("您已经在公共设施，本次使用无效");

            return ;
        }




        handleLandEvent(player, land);
        System.out.println(player.getName()+"已前往最近的公共设施：" + land.getName());
    }
    //随机后退：
    private void method3(Player player) {
        int steps = (int)(Math.random() * 4 + 2);
        player.move(-steps, boardsize);
        System.out.println(player.getName()+"后退了"+steps+"格");
        Land templand=this.gameBoard.getLandById(player.getPosition());
        //这个地方根据地块类型的不同调用不同方法,如下示例
        if (templand!=null) {//【】【】
            switch (templand.getType()) {
                //            【】【】
                case CHANCE -> handleCardEvent(player,this.cards.drawChanceCard());
                //【】【】
                case PROPERTY -> this.handleLandEvent(player, templand);
                case STATION -> this.handleStation(player);
                case JAIL -> {
                    System.out.println("你到达了监狱,暂停一回合");
                    if (player.outJailNum > 0) {

                        //                【】【】
                        System.out.println("是否使用出狱卡？输入Yes或No");
                        String s = sc.nextLine();
                        if(s=="Yes"){
                            player.outJailNum--;
                            player.setInJail(true);
                            System.out.println(player.getName() + "成功出狱！");
                        }
                    }else{
                        player.setInJail(true);
                    }

                }
                case  FREE_PARKING -> {
                    System.out.println("你到达了停车场,交款20");
                    player.setMoney(player.getMoney()-20);
                }
                case COMMUNITY_CHEST ->handleCardEvent(player,this.cards.drawCommunityChestCard());

                case UTILITY -> this.handleLandEvent(player, templand);
            }
        }

    }
    //进监狱：
    private void method4(Player player) {
        System.out.println(player.getName()+"已被送入了监狱");
        player.setInJail(true);
//        【】【】
        player.setPosition(10);
        System.out.println("是否使用出狱卡？输入Yes或No");
        String s = sc.nextLine();
        if(s=="Yes"){
            player.outJailNum--;
            player.setInJail(false);
            System.out.println(player.getName() + "成功出狱！");
        }
    }
    //自由出狱卡
    private void method5(Player player) {

//        System.out.println(player.getName()+"使用自由出狱卡，直接出狱");
//        player.setInJail(false);
        player.outJailNum++;
        //()()
    }
    //乌龟卡
    private void method6(Player player) {
        player.setTurtleNum(player.getTurtleNum()+1);
        System.out.println(player.getName()+"因为被一只乌龟爱上，获得一张乌龟卡");
        System.out.println("是否使用乌龟卡");
        String choice = sc.nextLine();
        if(choice=="yes"){
            turtleCardMethod(player);
        }

    }

    private void turtleCardMethod(Player player) {
        System.out.println("请输入要被施加乌龟卡玩家的ID：");
        int targetId = sc.nextInt();

        //避免出现玩家不存在的情况
        while (true) {


            if(0<targetId && targetId<=4){
                int finalTargetId = targetId;
                Player target = players.stream().filter(p -> p.getId() == finalTargetId).findFirst().orElse(null);
                if (target != null) {
                    target.setIsTurtle(true);

                    target.setTurtleCount(target.getTurtleCount()+3);
                }
                player.setTurtleNum(player.getTurtleNum()-1);
                break;
            }else{
                System.out.println("玩家不存在，请重新输入");
                System.out.print("请重新输入要被施加乌龟卡的玩家ID：");
            }
            targetId = sc.nextInt();
        }
    }

    //休眠卡
    private void method7(Player player) {
        player.setTurtleNum(player.getSleepNum()+1);
        System.out.println(player.getName()+"因为昨天睡太久错过早八，获得一张休眠卡");
        System.out.println("是否使用休眠卡");
        String choice = sc.nextLine();
        if(choice=="yes"){
            sleepCardMethod( player);
        }

    }
    private void sleepCardMethod(Player player) {

        while (true) {
            System.out.println("请输入要被施加休眠卡玩家的ID：");
            int targetId = sc.nextInt();
            Player target = players.stream().filter(p -> p.getId() == targetId).findFirst().orElse(null);
            if (target != null) {
                target.setIsSleep(true);
                target.setSleepCount(target.getSleepCount() + 1);
                break;
            }else{
                System.out.println("玩家不存在，请重新输入");

            }
        }
    }



    //命中注定的诈骗//卡牌中的效果卡
    private void method8(Player player) {
        System.out.println(player.getName() + "被诈骗走了15%的财产");
        //更新玩家的金钱
        player.setMoney(player.getMoney()*0.85);
        System.out.println(player.getName() + "剩余金钱:"+player.getMoney());
    }
    //获得奖金//卡牌中的效果卡
    private void method9(Player player) {
        int[] arr = {200,300,320};
        Random r = new Random();
        int index = r.nextInt(arr.length);
        int bonus = arr[index];
        System.out.println(player.getName()+"因为用左脚踏入异世界，获得了$"+bonus+"的奖金");
        player.setMoney(player.getMoney()+bonus);
        System.out.println(player.getName()+"剩余金钱:"+player.getMoney());
    }

    //继承遗产//卡牌中的效果卡
    private void method10(Player player) {
        System.out.println(player.getName()+"在异世界认的爸爸去世，从遗产中获得$25θ");
        player.setMoney(player.getMoney()+250);
        System.out.println(player.getName()+"剩余金钱:"+player.getMoney());
    }

    //银行错误
    private void method11(Player player) {
        System.out.println(player.getName()+"因ATM机错误获得了$200");
        player.setMoney(player.getMoney()+200);
        System.out.println(player.getName()+"剩余金钱:"+player.getMoney());
    }
    //慈善捐款
    private void method12(Player player) {
        System.out.println(player.getName() + "被慈善机构的妹妹勾引，为慈善机构捐款$100");
        player.setMoney(player.getMoney() - 100);
        System.out.println(player.getName() + "剩余金钱:"+player.getMoney());
    }
    private void method13(Player player){
        player.setExchangeNum(player.getExchangeNum()+1);
        System.out.println(player.getName()+"昨晚元歌玩太多了，获得一张换位卡");
        System.out.println("是否使用交换卡");
        String choice = sc.nextLine();
        if(choice=="yes"){
            exchangeCardMethod( player);
        }

    }


    //换位卡
    private void exchangeCardMethod(Player player) {
        System.out.println("你可以与一位玩家交换位置，请输入他的ID：");
        int targetId;
        int temp = player.getPosition();
        while (true) {
            targetId = sc.nextInt();

            if(0<targetId&&targetId<=4){
                break;
            }else if (targetId == temp){
                System.out.println("不能与自己交换位置，请重新输入");
            }else{
                System.out.println("玩家不存在，请重新输入");
            }
        }
        int finalTargetId = targetId;
        Player target = players.stream().filter(p -> p.getId() == finalTargetId).findFirst().orElse(null);
        if (target != null) {

            player.setPosition(target.getPosition());
            target.setPosition(temp);
        }
        System.out.println(player.getName() + "成功和" + target.getName() + "交换位置");
    }

    private void method14(Player player) {
        player.setExchangeNum(player.getBlockNum()+1);
        System.out.println(player.getName()+"今天被香蕉皮绊倒了100次、决定报复社会，获得一张路障卡");
        System.out.print("是否使用路障卡？请输入Yes或No：");
        String choice = sc.nextLine();
        if(choice=="yes"){
            blockCardMethod( player);
        }
    }

    //路障卡【下一个版本编写其逻辑】
    private void blockCardMethod(Player player) {

    }




    /**难度：**
     负责人：
     功能：检查游戏是否结束
     * 1. 所有玩家金钱不足，游戏结束
     * 2. 游戏结束，展示游戏结束信息
     * 3. 结束游戏
     * */



    public boolean isGameOver() {
        int allBankruptCount = 0;
        Player winner = null;
        for (Player player : players) {
            if(player.breakMoney()){
                allBankruptCount++;
                winner = player;
            }
        }

        if(allBankruptCount == players.size()-1){
            System.out.println("游戏结束，" + winner.getName() + "获胜！   其他人已经破产了");
            System.out.println(winner.getName()+"剩余金钱：" + winner.getMoney() + "你才是真正的大富翁！");

            return true;
        } else{
            System.out.println("游戏未结束，进行下一回合");
            System.out.println("=======================");
            return false;
        }

    }


    /**难度： **
     负责人：
     功能：查找获胜者
     1. 找出拥有最多金钱的玩家
     2. 展示游戏结束信息
     3. 结束游戏
     * */
    public Player findWinner() {
        Player player=players.get(0);
        double max=player.getMoney();
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getMoney()>=max){
                player=players.get(i);
            }
        }
        return player;
    }

    /**
     * getter方法（获取玩家列表）
     * 难度：**
     * 负责人：
     * */
    public List<Player> getPlayers() {
        return players;
    }
}