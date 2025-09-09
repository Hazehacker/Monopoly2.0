package com.Pojo;

//地块类 - 表示大富翁游戏中的每一个地块/格子


import com.Service.Service;

public class Land {
    /**
     * 地块唯一标识ID
     * 用于区分地图上的不同位置
     */
    private int id;

    /**
     * 地块名称
     * 例如：""、""等
     */
    private String name;

    /**
     * 地块类型
     * 使用LandType枚举表示：
     * START(起点), PROPERTY(地产), STATION(车站)等
     */
    private  LandType type;

    /**
     * 地块价格
     * 仅对可购买的地块(PROPERTY/STATION/UTILITY)有效
     * 单位：游戏货币
     */
    private int price;

    /**
     * 基础租金
     * 当其他玩家停留在此地块时需要支付的费用
     * 实际租金可能根据房屋数量有加成
     */
    private  int rent;

    /**
     * 地块所有者
     * 初始为   null   表示无主状态
     * 当玩家购买后指向对应的Player对象
     */
    private Player owner;



    /**
     * 房屋数量
     * 范围：0-4
     * 0: 无房屋
     * 1-3: 对应数量的房屋
     * 4: 表示已升级为酒店
     * 仅对PROPERTY类型地块有效
     */
    private static int houseCount;



    /**
     * 难度 **
     * 负责人
     * @return
     */
    public  int getActualRent() {
        // 实现租金计算逻辑
        // 例如：基础租金 * (房屋数量 + 1)
        if(type==LandType.PROPERTY){
            if(houseCount<=3) {
                rent = 50 * (houseCount + 1) + 50;
                //房屋小于等于3，返回基础租金 * (房屋数量 + 1) + 50
            }else
                rent = 500;
            // 再次增加房屋数量则升级为酒店，返回固定值500；
        }else if(type==LandType.STATION){
            rent = Service.rollDice() *50;
        }
        return rent;
    }

    /**
     * 负责人
     * 检查地块是否可购买
     * @return 可购买返回true，否则false
     */
    public boolean isPurchasable() {
        return (type == LandType.PROPERTY
                || type == LandType.STATION
                || type == LandType.UTILITY)&&
                owner == null;
    }



    //判断当前地块是否能够建房子
    //可以建设的条件:
    //1. 地块类型为PROPERTY
    //2. 当前地块数量（等级）小于4(houseCount < 4)
    //3. 地块没所有者（owner==null） 或者 地块的所有者是当前玩家
    public boolean canBuildHouse(Player currentPlayer) {
        return type == LandType.PROPERTY
                && houseCount < 4
                && (owner == null || owner == currentPlayer);
    }



    public Land() {
    }

    public Land(int id, String name, LandType type, int price, int rent, Player owner) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.rent = rent;
        this.owner = owner;
        this.houseCount = 0;
    }
    public int  getHouseCount(){
        return this.houseCount;
    }
    public void setHouseCount(int houseCount){
        this.houseCount = houseCount;
    }



    /**
     * 难度 **
     * 负责人
     * 对拥有者进行赋值
     *  参数：玩家对象
     *  功能：根据玩家对象对当前地块的属性进行赋值（所有权）
     *  返回值：void
     * */
    public void Assignment(Player p){
        if(p==null){
            // 如果传入的玩家对象为空，抛出异常
            throw new IllegalArgumentException("玩家对象不能为空");
        }
        this.owner=p;
    }







    //====================================================JavaBean================================================

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
     * @return type
     */
    public LandType getType() {
        return type;
    }

    /**
     * 设置
     * @param type
     */
    public void setType(LandType type) {
        this.type = type;
    }

    /**
     * 获取
     * @return price
     */
    public int getPrice() {
        return price;
    }

    /**
     * 设置
     * @param price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * 获取
     * @return rent
     */
    public int getRent() {
        return rent;
    }

    /**
     * 设置
     * @param rent
     */
    public void setRent(int rent) {
        this.rent = rent;
    }

    /**
     * 获取
     * @return owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * 设置
     * @param owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String toString() {
        return "Land{id = " + id + ", name = " + name + ", type = " + type + ", price = " + price + ", rent = " + rent + ", owner = " + owner + "}";
    }

}


