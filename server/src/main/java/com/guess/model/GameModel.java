package com.guess.model;

import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;

public class GameModel {
    private String round_no;//轮次
    private long sequence;//每月的轮次
    private String month;//游戏所属月份
    private int status = 0;//0正在进行中，1停止竞猜，2已结束,3流局
    private long height = 0;//当前块的高度
    private long block = 0;//公布中奖块
    private String hash = "";//公布中奖HASH块
    private BigDecimal totalAmount = BigDecimal.ZERO;//总投注额
    private BigDecimal minAmount = BigDecimal.ZERO;//猜小投注额
    private BigDecimal maxAmount = BigDecimal.ZERO;//猜大投注额
    private int winning = 0;//中奖类型：1小中奖，2大中奖
    private String winningNum = "";//中奖字符
    private long userCount = 0;//总投注用户
    private long minUserCount = 0;//猜小投注人数
    private long maxUserCount = 0;//猜大投注人数
    private long startTime = 0;//开始时间
    private long endTime;//结束时间
    private long blockTime=0;//下一次活竞猜时间
    private long currTime=0;//当前时间

    public String getRound_no() {
        return round_no;
    }

    public void setRound_no(String round_no) {
        this.round_no = round_no;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getWinning() {
        return winning;
    }

    public void setWinning(int winning) {
        this.winning = winning;
    }

    public String getWinningNum() {
        return winningNum;
    }

    public void setWinningNum(String winningNum) {
        this.winningNum = winningNum;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getMinUserCount() {
        return minUserCount;
    }

    public void setMinUserCount(long minUserCount) {
        this.minUserCount = minUserCount;
    }

    public long getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(long maxUserCount) {
        this.maxUserCount = maxUserCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(long blockTime) {
        this.blockTime = blockTime;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }
}
