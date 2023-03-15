package com.example.localdelivery.modelClass;

public class Users {
    public String getVehicleNum() {
        return vehicleNum;
    }

    public void setVehicleNum(String vehicleNum) {
        this.vehicleNum = vehicleNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    private String username;
    private String password;
    private String phno;
    private String fullname;
    private String address;
    private String approval;



    public Users(String vehicleNum, String vehicleType) {
        this.vehicleNum = vehicleNum;
        this.vehicleType = vehicleType;
    }

    private String liBack;
    private String liFront;
    private String icFront;
    private String icBack;
    private String selfie;
    private String vehicleNum;
    private String vehicleType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getLiBack() {
        return liBack;
    }

    public void setLiBack(String liBack) {
        this.liBack = liBack;
    }

    public String getLiFront() {
        return liFront;
    }

    public void setLiFront(String liFront) {
        this.liFront = liFront;
    }

    public String getIcFront() {
        return icFront;
    }

    public void setIcFront(String icFront) {
        this.icFront = icFront;
    }

    public String getIcBack() {
        return icBack;
    }

    public void setIcBack(String icBack) {
        this.icBack = icBack;
    }

    public String getSelfie() {
        return selfie;
    }

    public void setSelfie(String selfie) {
        this.selfie = selfie;
    }

    public Users(String username, String password, String phno, String fullname, String address,
                 String approval, String liBack, String liFront, String icFront, String icBack, String selfie) {
        this.username = username;
        this.password = password;
        this.phno = phno;
        this.fullname = fullname;
        this.address = address;
        this.approval = approval;
        this.liBack = liBack;
        this.liFront = liFront;
        this.icFront = icFront;
        this.icBack = icBack;
        this.selfie = selfie;
    }

    public Users(){

    }
}
