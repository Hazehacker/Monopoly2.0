package com.Pojo;

//玩家数据设计


import java.util.List;
import java.util.Objects;

public class Player {
    private int id;  //玩家id
    private String name; //玩家名字
    private int position; // 当前在地图上的位置
    private int money; //金钱
    private boolean bankruptcy; //判断这个玩家是否已经破产了
    private List<Land> landOfPlayer; // 拥有的地产
    private CardManager cards;//拥有的卡牌
    private boolean inJail; //判断是否处于在监狱的标志
    private boolean isTurtle; //判断是否中了乌龟卡


    private int TurtleCount; //乌龟卡的残余次数
    private int TurtleNum; //乌龟卡的数量
    public int outJailNum; //出监狱卡的数量
    private int SleepNum; //睡眠卡的数量
    private boolean isSleep; //是否处于睡眠状态
    private int SleepCount; //睡眠卡的剩余回合数
    private int ExchangeNum; //交换卡的数量
    private int BlockNum; //路障卡的数量
    private boolean isBlock; //是否处于路障状态



    public Player() {
    }

    public Player(int id, String name, int position, int money, boolean bankruptcy, boolean inJail, boolean isTurtle, int TurtleCount, int TurtleNum, int outJailNum, int SleepNum, boolean isSleep, int SleepCount, int ExchangeNum, int BlockNum, boolean isBlock,CardManager cards) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.money = money;
        this.bankruptcy = bankruptcy;
        this.inJail = inJail;
        this.isTurtle = isTurtle;
        this.TurtleCount = TurtleCount;
        this.TurtleNum = TurtleNum;
        this.outJailNum = outJailNum;
        this.SleepNum = SleepNum;
        this.isSleep = isSleep;
        this.SleepCount = SleepCount;
        this.ExchangeNum = ExchangeNum;
        this.BlockNum = BlockNum;
        this.isBlock = isBlock;
        this.cards = cards;
    }


    /*
     * 难度：**
     * 负责人：
     * 功能：移动玩家
     * 参数：
     * 1. steps：移动步数
     * 2. boardSize：地图大小（格子数目）
     *
     * */
    public void move(int steps, int boardSize) {
        // 更新玩家位置，处理绕回起点（起点positon为0）

        // 睡眠状态判断
        if (isSleep) {
            if (SleepCount > 0) {
                SleepCount--;
                System.out.println(name + " 睡眠中，剩余睡眠回合：" + SleepCount);
                return;
            } else {
                isSleep = false;
            }
        }

        // 阻塞状态判断
        if (isBlock) {
            isBlock = false;
            System.out.println(name + " 被阻塞，跳过本回合！");
            return;
        }

        // 乌龟卡限制骰子点数为1
        if (isTurtle) {
            if (TurtleCount > 0) {
                TurtleCount--;
                steps = 1; // 固定为1
                System.out.println(name + " 中了乌龟卡，本回合骰子固定为1，剩余乌龟回合：" + TurtleCount);
            } else {
                isTurtle = false;
            }
        }

        // 正常移动
        int oldPos = position;
        position = (position + steps) % boardSize;
        if(position < 0){
            position += boardSize;
        }

        if (oldPos + steps >= boardSize) {
            money += 200;
            System.out.println(name + " 经过起点，获得200金币！");
        }

        System.out.println(name + " 移动了 " + steps + " 步，当前位置：" + position);
    }



    /*
     * 难度： **
     * 负责人：
     * 功能：是否成功购买地块
     * 参数：
     * 参数一： land 要购买的地块
     * 参数二： 购买是否成功  true->购买成功 false->购买失败
     * 返回值：购买是否成功  true->购买成功 false->购买失败
     *
     * */
    public boolean buyLand(Land land) {
        // 检查金钱是否足够
        // 更新玩家金钱
        // 将地块添加到properties
        //返回购买结果


        if (!land.isPurchasable()) {
            System.out.println(name + " 尝试购买 " + land.getName() + "，但该地块不可购买！");
            return false;
        }

        // 2. 判断地块是否已有主人
        if (land.getOwner() != null) {
            System.out.println(name + " 尝试购买 " + land.getName() + "，但已经被 "
                    + land.getOwner().getName() + " 拥有！");
            return false;
        }

        // 3. 判断金钱是否足够
        int price = land.getPrice();
        if (money >= price) {
            //扣除金钱，改变地块所有者
            money -= price;
            land.setOwner(this);
            System.out.println(name + " 成功购买地块：" + land.getName() + "，花费："
                    + price + "，剩余金币：" + money);
            return true;
        } else {
            System.out.println(name + " 金币不足，无法购买地块：" + land.getName() +
                    "，所需：" + price + "，现有：" + money);
            return false;

        }
    }



    /*
    难度： *
    负责人：
    判断玩家是否破产
        参数：玩家对象
        功能：判断玩家的资金是否小于等于0
        返回值：true->破产了
                false->没有
     */
    public boolean breakMoney() {
        if (getMoney() <= 0) {
            return true;
        } else {
            return false;
        }
    }



    /*
        难度：**
        负责人：
        功能：玩家使用卡片(机会卡或者命运卡)
        参数：id  -> 对应卡牌的id
        功能：调用choiceCard方法，传入id
        */
    public void UseCard(int id) {
        System.out.println(name + " 正在使用卡牌 ID：" + id);
        choiceCard(id);
    }


    //难度： ***
    //负责人：
    //功能：
    //根据id查找对应的方法并且实现
    //根据id的不同，用选择语句执行不同的卡对应的方法
    private void choiceCard(int id){
        switch (id){

            //以此类推....
        }
    }

    private void method1() {
    }



    public void addLandOfPlayer(Land land) {
        this.landOfPlayer.add(land);
    }

    //=============================================JavaBean=======================================================


    public List<Land> getLandOfPlayer() {
        return landOfPlayer;
    }

    public void setLandOfPlayer(List<Land> landOfPlayer) {
        this.landOfPlayer = landOfPlayer;
    }

    /**
     * 获取
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return position
     */
    public int getPosition() {
        return position;
    }

    /**
     * 设置
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 获取
     * @return money
     */
    public int getMoney() {
        return money;
    }

    /**
     * 设置
     * @param money
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * 获取
     * @return bankruptcy
     */
    public boolean isBankruptcy() {
        return bankruptcy;
    }

    /**
     * 设置
     * @param bankruptcy
     */
    public void setBankruptcy(boolean bankruptcy) {
        this.bankruptcy = bankruptcy;
    }

    /**
     * 获取
     * @return inJail
     */
    public boolean isInJail() {
        return this.inJail;
    }

    /**
     * 设置
     * @param inJail
     */
    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

    /**
     * 获取
     * @return isTurtle
     */
    public boolean isIsTurtle() {
        return isTurtle;
    }

    /**
     * 设置
     * @param isTurtle
     */
    public void setIsTurtle(boolean isTurtle) {
        this.isTurtle = isTurtle;
    }





    /**
     * 获取
     * @return TurtleCount
     */
    public int getTurtleCount() {
        return TurtleCount;
    }

    /**
     * 设置
     * @param TurtleCount
     */
    public void setTurtleCount(int TurtleCount) {
        this.TurtleCount = TurtleCount;
    }

    /**
     * 获取
     * @return TurtleNum
     */
    public int getTurtleNum() {
        return TurtleNum;
    }

    /**
     * 设置
     * @param TurtleNum
     */
    public void setTurtleNum(int TurtleNum) {
        this.TurtleNum = TurtleNum;
    }

    /**
     * 获取
     * @return outJailNum
     */
    public int getOutJailNum() {
        return outJailNum;
    }

    /**
     * 设置
     * @param outJailNum
     */
    public void setOutJailNum(int outJailNum) {
        this.outJailNum = outJailNum;
    }

    /**
     * 获取
     * @return SleepNum
     */
    public int getSleepNum() {
        return SleepNum;
    }

    /**
     * 设置
     * @param SleepNum
     */
    public void setSleepNum(int SleepNum) {
        this.SleepNum = SleepNum;
    }

    /**
     * 获取
     * @return isSleep
     */
    public boolean isIsSleep() {
        return isSleep;
    }

    /**
     * 设置
     * @param isSleep
     */
    public void setIsSleep(boolean isSleep) {
        this.isSleep = isSleep;
    }

    /**
     * 获取
     * @return SleepCount
     */
    public int getSleepCount() {
        return SleepCount;
    }

    /**
     * 设置
     * @param SleepCount
     */
    public void setSleepCount(int SleepCount) {
        this.SleepCount = SleepCount;
    }

    /**
     * 获取
     * @return ExchangeNum
     */
    public int getExchangeNum() {
        return ExchangeNum;
    }

    /**
     * 设置
     * @param ExchangeNum
     */
    public void setExchangeNum(int ExchangeNum) {
        this.ExchangeNum = ExchangeNum;
    }

    /**
     * 获取
     * @return BlockNum
     */
    public int getBlockNum() {
        return BlockNum;
    }

    /**
     * 设置
     * @param BlockNum
     */
    public void setBlockNum(int BlockNum) {
        this.BlockNum = BlockNum;
    }

    /**
     * 获取
     * @return isBlock
     */
    public boolean isIsBlock() {
        return isBlock;
    }

    /**
     * 设置
     * @param isBlock
     */
    public void setIsBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }

    public String toString() {
        return "Player{id = " + id + ", name = " + name + ", position = " + position + ", money = " + money + ", bankruptcy = " + bankruptcy + ", inJail = " + inJail + ", isTurtle = " + isTurtle + ", TurtleCount = " + TurtleCount + ", TurtleNum = " + TurtleNum + ", outJailNum = " + outJailNum + ", SleepNum = " + SleepNum + ", isSleep = " + isSleep + ", SleepCount = " + SleepCount + ", ExchangeNum = " + ExchangeNum + ", BlockNum = " + BlockNum + ", isBlock = " + isBlock + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id && position == player.position && money == player.money && bankruptcy == player.bankruptcy && inJail == player.inJail && isTurtle == player.isTurtle && TurtleCount == player.TurtleCount && TurtleNum == player.TurtleNum && outJailNum == player.outJailNum && SleepNum == player.SleepNum && isSleep == player.isSleep && SleepCount == player.SleepCount && ExchangeNum == player.ExchangeNum && BlockNum == player.BlockNum && isBlock == player.isBlock && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, position, money, bankruptcy, inJail, isTurtle, TurtleCount, TurtleNum, outJailNum, SleepNum, isSleep, SleepCount, ExchangeNum, BlockNum, isBlock);
    }

    /**
     * 获取
     * @return cards
     */
    public CardManager getCards() {
        return cards;
    }

    /**
     * 设置
     * @param card
     */
    public void setCards(Cards card) {
        this.cards.addCard(card);
        System.out.println(this.name + "得到了卡牌"+card.getId()+",卡牌作用如下"+card.getDescription());
    }
}
