package com.Pojo;


import java.util.ArrayList;
import java.util.List;

import static com.Pojo.CardType.CHANCE;
import static com.Pojo.CardType.COMMUNITY_CHEST;

/**
 * 卡牌管理类 - 管理所有卡牌
 */
public class CardManager {
    private static final String[] Card_Info = {
            //机会卡
            "去图灵院：玩家前进到图灵院，并按照正常流程处理。",
            "前进到最近的公共设施：玩家前进到最近的公共设施（火车站或电力公司），并按规则购买或不买、交过路费。",
            "随机后退：玩家随机后退2-5格，可能会落在不同类型的格子上",
            "进监狱：玩家直接进监狱，需按照监狱规则处理。",
            "自由出狱卡：玩家获得一张“自由出狱卡”，可以在未来的监狱中使用",
            //机会格子里面的道具卡
            "乌龟卡：被指定的玩家接下来3回合骰子固定为1（可以指定自己）",
            "休眠卡：被指定的玩家跳过一回合（可以指定自己）",
            //下面是命运卡
            "支付学费：玩家需要支付 $50 的学费",
            "获得奖金：玩家从银行获得一笔奖金（如 $20、$50 或 $100）",
            "继承遗产：玩家从遗产中获得 $100。",
            "银行错误：玩家因银行的错误获得 $200",
            "慈善捐款：玩家需要向慈善机构捐款 $100",
            //命运格子里面的道具卡
            "换位卡：和被指定的玩家换位",
            "路障卡：选择地图格，设置路障，可以阻挡一次"
    };
    private List<Cards> chanceCards= new ArrayList<>(); // 机会卡牌组
    private List<Cards> communityChestCards= new ArrayList<>(); // 命运卡牌组
    private List<Cards> cards;
    public CardManager() {
    }

    public CardManager(List<Cards> chanceCards, List<Cards> communityChestCards) {
        this.chanceCards = chanceCards;
        this.communityChestCards = communityChestCards;
    }

    /**
     * 难度：  **
     * 负责人：毕哲晖
     * 初始化所有卡牌
     * 那张卡牌对应哪个id，描述是什么，类型是什么，写的时候要记得放注释，方便以后修改
     */
    public void initializeCards() {
        // 创建各种机会卡和命运卡
        chanceCards = new ArrayList<>();
        communityChestCards = new ArrayList<>();
        String s[] = {
                //机会卡
                "去图灵院：玩家前进到图灵院，并按照正常流程处理。",
                "前进到最近的公共设施：玩家前进到最近的公共设施（火车站或电力公司），并按规则购买或不买、交过路费。",
                "随机后退：玩家随机后退2-5格，可能会落在不同类型的格子上",
                "进监狱：玩家直接进监狱，需按照监狱规则处理。",
                "自由出狱卡：玩家获得一张“自由出狱卡”，可以在未来的监狱中使用",
                //机会格子里面的道具卡
                "乌龟卡：被指定的玩家接下来3回合骰子固定为1（可以指定自己）",
                "休眠卡：被指定的玩家跳过一回合（可以指定自己）",
                //下面是命运卡
                "支付学费：玩家需要支付 $50 的学费",
                "获得奖金：玩家从银行获得一笔奖金（如 $20、$50 或 $100）",
                "继承遗产：玩家从遗产中获得 $100。",
                "银行错误：玩家因银行的错误获得 $200",
                "慈善捐款：玩家需要向慈善机构捐款 $100",
                //命运格子里面的道具卡
                "换位卡：和被指定的玩家换位",
                "路障卡：选择地图格，设置路障，可以阻挡一次"};


        for (int i = 0;i<7;i++){
            Cards c = new Cards(i,s[i],CHANCE);
            chanceCards.add(c);
        }
        for(int i = 7;i<14;i++){
            Cards c = new Cards(i,s[i],COMMUNITY_CHEST);
            communityChestCards.add(c);
        }
    }

    /**
     * 难度：**
     * 负责人：毕哲晖
     * 抽取机会卡
     * return 随机机会卡
     */
    public Cards drawChanceCard() {
        Cards c = new Cards();
        int i  = (int)(Math.random()*7);
        c = chanceCards.get(i);
        return c;
    }

    /**
     * 难度：**
     * 负责人：毕哲晖
     * 抽取命运卡
     * return 随机命运卡
     */

    public Cards drawCommunityChestCard() {
        Cards c = new Cards();
        int i  = (int)(Math.random()*7);
        c = communityChestCards.get(i);
        return c;

    }


    /**
     * 获取
<<<<<<< HEAD
<<<<<<< HEAD
=======
     <<<<<<< HEAD
>>>>>>> e49a326f50bd92cfd187ee68b798ab9db981a621
     *
     * @return chanceCards
     */


    public List<Cards> getChanceCards() {
        return chanceCards;
    }

    /**
     * 设置
<<<<<<< HEAD
<<<<<<< HEAD
=======
     <<<<<<< HEAD
>>>>>>> e49a326f50bd92cfd187ee68b798ab9db981a621
     *
     * @param chanceCards
     */


    public void setChanceCards(List<Cards> chanceCards) {

        this.chanceCards = chanceCards;
    }

    /**
     * 获取
<<<<<<< HEAD
<<<<<<< HEAD
=======
     <<<<<<< HEAD
>>>>>>> e49a326f50bd92cfd187ee68b798ab9db981a621
     *
     * @return communityChestCards
     */


    public List<Cards> getCommunityChestCards() {
        return communityChestCards;
    }

    /**
     * 设置
<<<<<<< HEAD
<<<<<<< HEAD
=======
     <<<<<<< HEAD
>>>>>>> e49a326f50bd92cfd187ee68b798ab9db981a621
     *
     * @param communityChestCards
     */


    public void setCommunityChestCards(List<Cards> communityChestCards) {
        this.communityChestCards = communityChestCards;
    }

    public String toString() {
        return "CardManager{chanceCards = " + chanceCards + ", communityChestCards = " + communityChestCards + "}";
    }

    //添加卡牌
    public void addCard(Cards card) {
        if (card.getType() == CHANCE) {
            this.chanceCards.add(card);
        } else if (card.getType() == COMMUNITY_CHEST) {
            this.communityChestCards.add(card);
        }
    }

    //移除卡牌
    public void removeCard(Cards card) {
        if (card.getType() == CHANCE) {
            chanceCards.remove(card);
        } else if (card.getType() == COMMUNITY_CHEST) {
            communityChestCards.remove(card);
        }
    }

    public static String getCardInfoById(int cardId) {
        if (cardId >= 0 && cardId < Card_Info.length) {
            return Card_Info[cardId];
        }
        return "未知卡牌信息";
    }

}
