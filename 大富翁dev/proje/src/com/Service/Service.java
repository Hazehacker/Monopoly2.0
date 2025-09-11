package com.Service;

import com.Pojo.*;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Service {

    // 玩家列表
    private List<Player> players;
    // 地块列表
    private List<Land> lands;
    // 卡牌列表
    public CardManager cards;

    public GameBoard gameBoard;
    private int boardsize=20;//格子数目
    private void useCard(int choice,List<Cards> playerCards,Player player){
        if (choice > 0 && choice <= playerCards.size()) {
            Cards selectedCard = playerCards.get(choice - 1);
            // 调用 choiceCard 方法处理卡牌
            choiceCard(player, selectedCard);
            // 使用后移除卡牌
            player.getCards().removeCard(selectedCard);
        } else {
            System.out.println(player.getName() + " 编号使用错误，重新输入: ");

        }
    }


    public Service() {
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

        players.add(new Player(1, "玩家1", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager()));
        players.add(new Player(2, "玩家2", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager()));
        players.add(new Player(3, "玩家3", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager()));
        players.add(new Player(4, "玩家4", 0, rollDice()*400, false, false, false,0,0,0,0,false,0,0,0,false,new CardManager()));

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

    public void handlePlayerAction1(Player player, int steps) {
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
                player.setInJail(true);//()
//                ()
                return ;
//                ()
            }
        }
        if(player.getTurtleNum()>0){
            System.out.println("该玩家还处在乌龟状态中，只能移动一格");
            player.setPosition(player.getPosition()+1);
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
        handlePlayerAction1(player,steps);
        //        【】【】

//        player.move(steps,boardsize);//移动玩家到新位置【】【】
        Land templand=service.gameBoard.getLandById(player.getPosition());
        //这个地方根据地块类型的不同调用不同方法,如下示例
        if (templand!=null) {//【】【】
            switch (templand.getType()) {
    //            【】【】居然漏掉了使用命运卡的逻辑
                case CHANCE -> handleCardEvent(player,service.cards.drawChanceCard());
                //【】【】
                case PROPERTY -> service.handleLandEvent(player, templand);
                case STATION -> service.handleStation(player);
                case JAIL -> {
                    System.out.println("你到达了监狱,暂停一回合");
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
                        player.setInJail(true);
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
        if (templand.getId()==3) {
            while (true) {
                System.out.print("请输入你想前进多少步:");
                Scanner sc=new Scanner(System.in);
                Integer foot = (Integer)(sc.nextInt());
                if(foot>0){
                    player.move(foot,boardsize);
                }else{
                    System.out.println("真男人从不回头，请重新输入步数");
                }
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
        List<Cards> playerCards = player.getCards().getChanceCards();
        playerCards.addAll(player.getCards().getCommunityChestCards());

        if (playerCards.isEmpty()) {
            System.out.println(player.getName() + " 没有道具卡可以使用。");
            return;
        }

        System.out.println(player.getName() + " 拥有的道具卡如下：");
        for (int i = 0; i < playerCards.size(); i++) {
            Cards card = playerCards.get(i);
            int cardId = card.getId();
            // 假设 CardManager 中有一个方法可以根据 cardId 获取卡牌信息
            String cardInfo = CardManager.getCardInfoById(cardId);
            System.out.println((i + 1) + ". " + cardInfo);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("是否使用道具卡？请输入Yes或No.");
        String get = sc.nextLine();

        if (get=="Yes") {
            System.out.println("请输入要使用的道具卡编号（1 - " + playerCards.size() + "），输入 0 取消使用：");
            int choice = scanner.nextInt();

            useCard( choice,playerCards,player);
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
        if (land.isPurchasable()&&owner == null&&(player.getMoney() >= land.getPrice())) {
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
            //地块主人是自己，判断是否可以建房子
            if(land.canBuildHouse(player)){
                //可以建房子
                int houseCost = land.getPrice();
                if(player.getMoney() >= houseCost){
//                    （）
                    if(land.getHouseCount()==4){
                        System.out.println("该土地已经不能再建房子了");
                        return;
                    }
//                    （）
                    usePrint(player,land,houseCost);
                    houseCost = land.getPrice();

                }

                else
                    System.out.println("钱不够你建房子了");
            }else if(land.isPurchasable()&&owner==player){//【】【】漏掉一个判断逻辑，导致会输出“玩家3拥有此地块：监狱”之类的结果
                System.out.println(player.getName()+"拥有此地块："+land.getName() );
            }

        }
    }

    //()()
    private void usePrint(Player player,Land land,int houseCost) {
        System.out.println("地块 " + land.getName() + " 是你的，是否要建房子？");
        System.out.println("请输入 Y 表示建房子，N 表示不建房子：");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if ("Y".equalsIgnoreCase(input)) {
            // 假设建房子有一个固定的花费，这里简单设为 100

            if (player.getMoney() >= houseCost) {
                //扣钱
                player.setMoney(player.getMoney() - houseCost);
                //加房子
                land.setHouseCount(land.getHouseCount() + 1);
                land.getActualRent();
                System.out.println("你成功在地块 " + land.getName() + " 建造了房子，当前房子数量为 " + land.getHouseCount());
            } else {
                System.out.println(player.getName()+"金钱不足，无法建造房子。");
            }


        }else{
            System.out.println(player.getName() + "选择不建房子。");
        }
    }
    //()()

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
//            case 13:
//                method14(player);
//                break;

            default:
                System.out.println("未知卡牌ID");
        }
    }
    Scanner sc = new Scanner(System.in);

    private void method1(Player player) {
//        【】【】
        player.setPosition(1);//设图灵院pos为5
        System.out.println(player.getName()+"已前往图灵院");
    }
    //前进到最近的公共设施
    private void method2(Player player) {
//        【】【】
        int closestStationId = 3; // 假设火车站pos为5
        player.setPosition(closestStationId);
        GameBoard gameBoard = new GameBoard();
        Land land = gameBoard.getLandById(closestStationId);
        handleLandEvent(player, land);
        System.out.println(player.getName()+"已前往最近的公共设施：" + land.getName());
    }
    //随机后退：
    private void method3(Player player) {
        int steps = (int)(Math.random() * 4 + 2);
        player.move(-steps, boardsize);
        System.out.println(player.getName()+"后退了"+steps+"格");
    }
    //进监狱：
    private void method4(Player player) {
        System.out.println(player.getName()+"进入监狱");
        player.setInJail(true);
//        【】【】
        player.setPosition(6);
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
        System.out.println("请输入要被施加乌龟卡玩家的ID：");
        int targetId = sc.nextInt();
        Player target = players.stream().filter(p -> p.getId() == targetId).findFirst().orElse(null);
        if (target != null) {
            target.setIsTurtle(true);
//            【】【】
            target.setTurtleCount(target.getTurtleCount()+3);
        }
    }
    //休眠卡
    private void method7(Player player) {
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
    //支付学费
    private void method8(Player player) {
        System.out.println(player.getName() + "支付$50的学费");
        //更新玩家的金钱
        player.setMoney(player.getMoney() - 50);
        System.out.println(player.getName() + "剩余金钱:"+player.getMoney());
    }
    //获得奖金
    private void method9(Player player) {
        int[] arr = {20,50,100};
        Random r = new Random();
        int index = r.nextInt(arr.length);
        int bonus = arr[index];
        System.out.println(player.getName()+"从银行获得了$"+bonus+"的奖金");
        player.setMoney(player.getMoney()+bonus);
        System.out.println(player.getName()+"剩余金钱:"+player.getMoney());
    }
    //继承遗产
    private void method10(Player player) {
        System.out.println(player.getName()+"从遗产中获得$1θθ");
        player.setMoney(player.getMoney()+100);
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
        System.out.println(player.getName() + "为慈善机构捐款$100");
        player.setMoney(player.getMoney() - 100);
        System.out.println(player.getName() + "剩余金钱:"+player.getMoney());
    }
    //换位卡
    private void method13(Player player) {
        System.out.println("你可以与一位玩家交换位置，请输入他的ID：");
        int targetId = sc.nextInt();
        Player target = players.stream().filter(p -> p.getId() == targetId).findFirst().orElse(null);
        if (target != null) {
            int temp = player.getPosition();
            player.setPosition(target.getPosition());
            target.setPosition(temp);
        }
        System.out.println(player.getName() + "成功和" + target.getName() + "交换位置");
    }
    //路障卡
//    private void method14(Player player) {

//    }





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
        int max=player.getMoney();
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getMoney()>=max){
                player=players.get(i);
            }
        }
        return player;
    }

    /**
     * 获取玩家列表
     * 难度：**
     * 负责人：
     * */
    public List<Player> getPlayers() {
        return players;
    }
}



