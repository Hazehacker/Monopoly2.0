package com.Pojo;


import java.util.List;


/**
 * 卡牌类 - 表示机会卡或命运卡
 */
public class Cards {
    private int id; // 卡牌ID
    private String description; // 卡牌描述
    private CardType type; // 卡牌类型


    public Cards() {
    }

    public Cards(int id, String description, CardType type) {
        this.id = id;
        this.description = description;
        this.type = type;
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
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取
     * @return type
     */
    public CardType getType() {
        return type;
    }

    /**
     * 设置
     * @param type
     */
    public void setType(CardType type) {
        this.type = type;
    }

    public String toString() {
        return "Cards{id = " + id + ", description = " + description + ", type = " + type + "}";
    }
}






