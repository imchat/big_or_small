package com.guess.util;

public class GuessUtil {
    //用户状态：0未参与，1等待公布中奖，2已中奖未发放，3未中奖,4已退款,5已中奖已发送
    public enum USER_STATUS {
        NoPAY(0, "未支付,未参与"),
        PAY(1, "已支付,等待公布中奖"),
        NotSendWinning(2, "已中奖未发放"),
        NotWinning(3, "未中奖"),
        Refund(4, "已退款"),
        SendWinning(5, "已中奖已发送"),
        NoConfirm(6, "已支付,未确认");

        USER_STATUS(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

    }

    //游戏状态，0进行中，1已结束，2已公布，3流局
    public enum GAME_STATUS {
        Ongoing(0, "进行中"),
        GameOver(1, "已结束"),
        Published(2, "已公布"),
        Flow(3, "流局");

        GAME_STATUS(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

    }

    //游戏最新一轮类型，0进行中，1已结束，2已公布，3流局
    public enum GAME_ROUND_TYPE {
        LastRound("当前轮次"),
        UpRound("上一轮");

        GAME_ROUND_TYPE(String desc) {
            this.desc = desc;
        }

        private String desc;

        public String getDesc() {
            return desc;
        }

    }
}
