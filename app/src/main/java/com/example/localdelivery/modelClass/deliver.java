package com.example.localdelivery.modelClass;

public class deliver {
    private String userId,shopId,orderId,oTime,oDistance,oAddress,oType;

    public String getUserId() {
        return userId;
    }

    public String getoType() {
        return oType;
    }

    public void setoType(String oType) {
        this.oType = oType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopId() {
        return shopId;
    }

    public deliver(String oType) {
        this.oType = oType;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getoTime() {
        return oTime;
    }

    public void setoTime(String oTime) {
        this.oTime = oTime;
    }

    public String getoDistance() {
        return oDistance;
    }

    public void setoDistance(String oDistance) {
        this.oDistance = oDistance;
    }

    public String getoAddress() {
        return oAddress;
    }

    public void setoAddress(String oAddress) {
        this.oAddress = oAddress;
    }

    public deliver(String userId, String shopId, String orderId, String oTime, String oDistance, String oAddress) {
        this.userId = userId;
        this.shopId = shopId;
        this.orderId = orderId;
        this.oTime = oTime;
        this.oDistance = oDistance;
        this.oAddress = oAddress;
    }

    public deliver(){

    }
}
