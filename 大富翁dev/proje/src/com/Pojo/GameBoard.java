package com.Pojo;

import java.util.ArrayList;
import java.util.List;


//游戏棋盘类对象
//用来初始化棋盘、获取地块id
public class GameBoard {
    private static List<Land> lands=new ArrayList<>(); // 所有地块的集合
    private int size; // 地图大小（地块总数）

    /**
     * 难度：
     * 负责人：
     * 传参初始化棋盘
     * 参数： size 地图大小
     * 功能： 通过调用initializeLands初始化棋盘
     */
    public GameBoard(int size) {
        this.size = size;
        initializeLands();
    }

    public GameBoard() {
    }

    public GameBoard(List<Land> lands, int size) {
        this.lands = lands;
        this.size = size;
        initializeLands();
    }

    /**
     * 难度：**
     * 负责人：
     * 初始化所有地块
     */
    public List<Land> initializeLands() {
        // 创建各种类型的地块并添加到lands集合中
        // 例如：起点、地产、车站、机会卡、命运卡等
        // 基础地块
        this.lands.add(new  Land(0, "起点", LandType.START, 0, 0, null));
        this.lands.add(new  Land(1, "拉·曼却领", LandType.PROPERTY, 275,55 , null));
        this.lands.add(new  Land(2, "中南大学", LandType.UTILITY, 0, 30, null));
        this.lands.add(new  Land(3, "命运卡", LandType.COMMUNITY_CHEST, 0, 0, null));
        this.lands.add(new  Land(4, "Cafe Stella", LandType.PROPERTY, 300, 0, null));
        this.lands.add(new  Land(5, "玛利亚之墙", LandType.UTILITY, 150, 30, null));
        this.lands.add(new  Land(6, "火车站", LandType.STATION, 0, 0, null));
        this.lands.add(new  Land(7, "罗塞之墙", LandType.PROPERTY, 300, 40, null));
        this.lands.add(new  Land(8, "机会卡", LandType.CHANCE, 0, 0, null));
        this.lands.add(new  Land(9, "希娜之墙", LandType.PROPERTY, 300, 45, null));
        this.lands.add(new  Land(10, "监狱", LandType.JAIL, 0, 200, null));
        this.lands.add(new  Land(11, "鸿园", LandType.PROPERTY, 300, 0, null));
        this.lands.add(new  Land(12, "湖南大学", LandType.UTILITY, 0, 0, null));
        this.lands.add(new  Land(13, "机会卡", LandType.CHANCE, 300, 45, null));
        this.lands.add(new  Land(14, "穗织", LandType.PROPERTY, 300, 50, null));
        this.lands.add(new  Land(15, "下北泽", LandType.PROPERTY, 300, 50, null));
        this.lands.add(new  Land(16, "停车场", LandType.FREE_PARKING, 0, 50, null));
        this.lands.add(new  Land(17, "飞鸟山公园", LandType.PROPERTY, 250, 50, null));
        this.lands.add(new  Land(18, "命运卡", LandType.COMMUNITY_CHEST, 250, 50, null));
        this.lands.add(new  Land(19, "吉野家", LandType.PROPERTY, 250, 50, null));


        this.size  = lands.size();  // 同步更新地图尺寸
        return lands;
    }


    /**
     * 难度：**
     * 负责人：
     * 根据ID获取地块
     * 参数： id 地块ID
     * 返回值： 对应的地块对象，找不到返回null
     */
    public Land getLandById(int id) {
        for (Land land : lands) {
            if (land.getId()  == id) {
                return land;
            }
        }
        return null; // 未找到时返回null
    }


    /**
     * 难度：**
     * 负责人：
     * 获取当前地块的下一个地块
     * 参数： currentLand 当前地块
     * 返回值： 下一个地块对象
     */
    public Land getNextLand(Land currentLand) {
        int nextId = currentLand.getId()  + 1;
        if (nextId >= size) {
            nextId = 0; // 环形地图处理
        }
        return getLandById(nextId);
    }


    //=============================JavaBean=============================================




    /**
     * 获取地图大小
     * @return 地图大小
     */
    public int getSize() {
        return size;
    }


    /**
     * 获取
     * @return lands
     */
    public List<Land> getLands() {
        return lands;
    }

    /**
     * 设置
     * @param lands
     */
    public void setLands(List<Land> lands) {
        this.lands = lands;
    }

    /**
     * 设置
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return "GameBoard{lands = " + lands + ", size = " + size + "}";
    }
}