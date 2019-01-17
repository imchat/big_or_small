package com.guess.model;

import java.math.BigDecimal;

public class OrderModel {
    private String out_trade_no;//订单号
    private String prepay_id;//预付单号
    private int status;//本地订单状态,0等待用户支付，1支付成功，2支付失败
    private int guessType;//猜大小类型：1小，2大
    private String round_no;//轮次号
    private BigDecimal amount;//下单金额
    private String openid;//用户openid
    private int productId;//选中的竞猜
    private String  transaction_id;//订单号
    private BigDecimal reward;//获得的奖励


    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGuessType() {
        return guessType;
    }

    public void setGuessType(int guessType) {
        this.guessType = guessType;
    }

    public String getRound_no() {
        return round_no;
    }

    public void setRound_no(String round_no) {
        this.round_no = round_no;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }



    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public BigDecimal getReward() {
        return reward;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
