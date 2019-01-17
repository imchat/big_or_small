package com.guess.service.impl;

import botapi.IMChatBot;
import botapi.model.RedPackDrawType;
import botapi.model.RedPackSubType;
import botapi.model.RedPackType;
import botapi.resp.ApiResult;
import com.alibaba.fastjson.JSONObject;
import com.guess.dao.GameDao;
import com.guess.model.GameModel;
import com.guess.model.OrderModel;
import com.guess.service.AuthService;
import com.guess.service.GameService;
import com.guess.service.PayService;
import com.guess.util.*;
import java_sdk.IMCConfig;
import java_sdk.WXPay;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("GameService")
public class GameServiceImpl implements GameService {

    @Autowired
    AuthService authService;
    @Autowired
    PayService payService;
    @Autowired
    GameDao gameDao;

    //手续费率
    private static BigDecimal feeRate = BigDecimal.valueOf(0.01);
    //精度位
    private static int  feeBit = 2;
    @Override
    public GameModel updateGameRund() {
        GameModel detail = this.getLastGameDetail();
        return updateGameRund(detail);
    }

    private GameModel updateGameRund(GameModel detail) {
        String round_no = "";

        if (detail == null) {
            //第一轮
            detail = this.getGameNewRund(System.currentTimeMillis());
            round_no = detail.getRound_no();
            gameDao.saveGameList(round_no, detail);
            gameDao.saveGameRundNo(round_no, GuessUtil.GAME_ROUND_TYPE.LastRound);

        } else {
            round_no = detail.getRound_no();
            //活动是否结束
            int status = detail.getStatus();
            if (status == 0) {//状态，0进行中，1已结束，2已公布，3流局
                long endTime = detail.getEndTime();
                if (System.currentTimeMillis() > endTime) {
                    status = GuessUtil.GAME_STATUS.GameOver.getCode();
                    detail.setStatus(status);
                    gameDao.saveGameList(round_no, detail);

                    //检查是否形成流局
                    BigDecimal totalAmount = detail.getTotalAmount();
                    BigDecimal minAmount = detail.getMinAmount();
                    BigDecimal maxAmount = detail.getMaxAmount();
                    //总共手续费, 向上取整
                    BigDecimal totalFee = totalAmount.multiply(feeRate).setScale(0, BigDecimal.ROUND_UP);
                    //没达到竞猜对冲人数形成流局，直接退款给用户
                    if (minAmount.compareTo(BigDecimal.ZERO) == 0 || maxAmount.compareTo(BigDecimal.ZERO) == 0 || totalFee.compareTo(maxAmount) >= 0) {

                        this.refund(round_no);
                        //开始下一轮
                        detail.setStatus(GuessUtil.GAME_STATUS.Flow.getCode());
                        gameDao.saveGameList(round_no, detail);
                        return this.runNextRound(detail, System.currentTimeMillis());

                    }

                }
            }
            //开始检查上链数据
            if (status == 1) {

                long block = detail.getBlock();
                if (block <= 0) {
                    long endTime = detail.getEndTime();
                    JSONObject blockData = this.getLasBlock(endTime);
                    System.out.println("lasBlock:" + JSONObject.toJSONString(blockData));
                    if (blockData != null) {
                        long height = blockData.getLongValue("height");
                        detail.setHeight(height);
                        detail.setBlock(height + Config.getGameBlockSize());//结束块
                        gameDao.saveGameList(round_no, detail);

                    }
                } else {
                    //获取公布块数据
                    JSONObject blockData = this.getPublishedBlock(block);
                    System.out.println("publishedBlock:" + JSONObject.toJSONString(blockData));
                    if (blockData != null) {
                        String hash = blockData.getString("hash");
                        detail.setHash(hash);
                        // new Date(blockData.getLongValue("timeStamp"))
                        long blockTime = DateUtils.addMinutes(new Date(), Config.getRoundIntervalTime()).getTime();
                        detail.setBlockTime(blockTime);

                        status = GuessUtil.GAME_STATUS.Published.getCode();
                        //进行奖池分配
                        int winning = 0;//0小，1为大
                        //中奖数字
                        String winningNum = hash.substring(hash.length() - 9, hash.length() - 8);
                        //数字
                        if (this.isNumeric(winningNum) && Integer.valueOf(winningNum) <= 7) {
                            winning = 1;
                        } else {
                            winning = 2;
                        }
                        detail.setWinning(winning);//1小，2为大
                        detail.setWinningNum(winningNum);//中奖大小
                        detail.setStatus(status);
                        gameDao.saveGameList(round_no, detail);
                        this.allotPool(detail);


                    } else {
                        //更新当前块
                        long height = detail.getHeight();
                        blockData = this.getPublishedBlock(height + 1);
                        if (blockData != null) {
                            detail.setHeight(height + 1);
                            gameDao.saveGameList(round_no, detail);
                        }
                    }

                }

            }
            //是否要进行下一轮
            if (status == 2) {
                //出块时间
                long nextStartTime = detail.getBlockTime();
                System.out.println("nextStartTime:" + nextStartTime);
                //上轮结果区块出块5分钟后进入下一轮
                if (System.currentTimeMillis() > nextStartTime) {
                    System.out.println("runNextRound:" + System.currentTimeMillis());
                    //开始下一轮
                    return this.runNextRound(detail, nextStartTime);
                }
            }

        }

        return detail;
    }

    //退款
    private void refund(String round_no) {
        List<OrderModel> list = this.getGameUserList(round_no);
        if (list != null && list.size() > 0) {
            GameModel gameModel = gameDao.getGameDetail(round_no);
            if (gameModel == null) {
                return;
            }
            try {

                //流局消息
                String message = gameModel.getMonth() + "#" + gameModel.getSequence() + "期竞猜结果：流局，投注金额退回到账户中";
                payService.sendMessage(message, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            for (OrderModel item : list) {
                try {
                    payService.refund(round_no, item, gameModel, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }


    //开始下一轮
    private GameModel runNextRound(GameModel gameModel, long nextStartTime) {
        //保存上一轮轮次号
        String round_no = gameModel.getRound_no();
        gameDao.saveGameRundNo(round_no, GuessUtil.GAME_ROUND_TYPE.UpRound);

        //获取新的一轮数据
        gameModel = this.getGameNewRund(nextStartTime);
        round_no = gameModel.getRound_no();
        gameDao.saveGameList(round_no, gameModel);
        gameDao.saveGameRundNo(round_no, GuessUtil.GAME_ROUND_TYPE.LastRound);

        return gameModel;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //分配奖池中的数量
    //用户状态：0未参与，1等待公布中奖，2已中奖未发放，3未中奖,4已退款，5中奖已发放
    private void allotPool(GameModel detail) {
        try {
            String round_no = detail.getRound_no();
            int winning = detail.getWinning();
            BigDecimal totalAmount = detail.getTotalAmount();
            BigDecimal minAmount = detail.getMinAmount();
            BigDecimal maxAmount = detail.getMaxAmount();
            long userCount = detail.getUserCount();
            long minUserCount = detail.getMinUserCount();
            long maxUserCount = detail.getMaxUserCount();
            //投注失败金额
            BigDecimal failAmount = BigDecimal.ZERO;
            long failUser = 0;
            //投注成功金额
            BigDecimal successAmount = BigDecimal.ZERO;
            long successUser = 0;
            if (winning == 1) {
                failAmount = maxAmount;
                successAmount = minAmount;

                failUser = maxUserCount;
                successUser = minUserCount;
            } else {
                successAmount = maxAmount;
                failAmount = minAmount;

                successUser = minUserCount;
                failUser = maxUserCount;
            }
            //获取下单用户列表
            BigDecimal totalFee = BigDecimal.ZERO;
            List<OrderModel> list = this.getGameUserList(round_no);
            if (list != null && list.size() > 0) {

                for (OrderModel item : list) {
                    String out_trade_no = item.getOut_trade_no();
                    int userStatus = item.getStatus();
                    if (userStatus != 1) {
                        continue;
                    }
                    //向上取整收取手续费,保留两位小数
                    BigDecimal fee = item.getAmount().multiply(feeRate).setScale(feeBit, BigDecimal.ROUND_UP);
                    totalFee=totalFee.add(fee);
                }

                //总共手续费
                if (totalFee.compareTo(failAmount) > 0) {
                    this.refund(round_no);
                    detail.setStatus(GuessUtil.GAME_STATUS.Flow.getCode());//流局
                    gameDao.saveGameList(round_no, detail);
                    this.runNextRound(detail, System.currentTimeMillis());
                    return;
                }

                //商户收款
                boolean isReceive = false;
                for (OrderModel item : list) {
                    String out_trade_no = item.getOut_trade_no();
                    int userStatus = item.getStatus();
                    if (userStatus != 1) {
                        continue;
                    }
                    //商户自动收款
                    payService.receiveOrder(out_trade_no);
                    isReceive = true;
                }
                if (isReceive) {
                    //中奖消息
                    String message = detail.getMonth() + "#" + detail.getSequence() + "期竞猜结果：" + (winning == 1 ? "小" : "大") + "，共" + userCount + "人参与，" + successUser +
                            "人中奖瓜分：" + totalAmount.stripTrailingZeros().toPlainString() + " FORCE";
                    payService.sendMessage(message, null);
                }

                //发放奖励
                for (OrderModel item : list) {
                    int guessType = item.getGuessType();
                    String openid = item.getOpenid();
                    BigDecimal amount = item.getAmount();
                    int userStatus = item.getStatus();
                    String out_trade_no = item.getOut_trade_no();
                    System.out.println("distributionPool:" + JSONObject.toJSONString(item));
                    if (userStatus != 1) {
                        continue;
                    }

                    if (guessType == winning) {//中奖用户

                        //分配数量，扣除10%手续费
                        BigDecimal allotAmount = failAmount.subtract(totalFee);
                        System.out.println("allotAmount:" + allotAmount);
                        BigDecimal rewardAmount = amount.divide(successAmount, feeBit, BigDecimal.ROUND_DOWN).multiply(allotAmount);
                        //包括本金一起发放,FORCE 精度0，发放需取整
                        rewardAmount = rewardAmount.add(amount).setScale(feeBit, BigDecimal.ROUND_DOWN);
                        //发送中奖消息
                        this.sendRewardMsg(openid, detail, rewardAmount);
                        //发送红包
                        String rewardName = "猜大小中奖红包";
                        String coinType = "FORCE";
                        int count = 1;//数量
                        float rate = 0;//分享红包，获取奖励比例
                        RedPackType rewardType = RedPackType.Common;
                        RedPackSubType rewardSubType = RedPackSubType.SELF_SHARE;
                        RedPackDrawType drawType = RedPackDrawType.AVG;//分配方式
                        String receiveOpenid = openid;
                        boolean result = payService.sendRedPack(rewardName, coinType, rewardAmount, count, rate, rewardType, rewardSubType, drawType, receiveOpenid);
                        if (result) {
                            //发送成功
                            item.setStatus(5);//状态：0未参与，1等待公布中奖，2已中奖未发送，3未中奖,4已退款,5已中奖已发送
                            item.setReward(rewardAmount);
                            //RocksDBUtil.writeDB(String.format(GameWinningUserListKey, round_no), openid, item);
                            gameDao.saveGamePay(out_trade_no, item);

                        } else {
                            item.setStatus(2);//状态：0未参与，1等待公布中奖，2已中奖未发送，3未中奖,4已退款,5已中奖已发送
                            gameDao.saveGamePay(out_trade_no, item);
                        }
                    } else {
                        item.setStatus(3);//状态：0未参与，1等待公布中奖，2已中奖未发送，3未中奖,4已退款,5已中奖已发送
                        gameDao.saveGamePay(out_trade_no, item);

                    }
                }
            }


        } catch (
                Exception ex) {
            ex.printStackTrace();
        }

    }

    //发送中奖消息
    private void sendRewardMsg(String openid, GameModel gameModel, BigDecimal rewardAmount) {
        try {
            JSONObject user = authService.getUserInfo(openid);
            if (user == null) {
                user = new JSONObject();
            }
            String nickName = user.getString("nickname");
            //中奖消息
            String message = "@" + nickName + " 恭喜您" + gameModel.getMonth() + "#" + gameModel.getSequence() + "期竞猜中奖，获得：" + rewardAmount.stripTrailingZeros().toPlainString() + " FORCE";
            payService.sendMessage(message, openid);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //产生新的一轮活动
    private GameModel getGameNewRund(long startTime) {
        GameModel gameModel = new GameModel();
        gameModel.setStartTime(startTime);
        gameModel.setEndTime(DateUtils.addMinutes(new Date(startTime), Config.getGameIntervalTime()).getTime());//间隔时间
        JSONObject mothSequence = this.getGameMothSequence(startTime);
        long sequence = mothSequence.getLongValue("sequence");
        String month = mothSequence.getString("month");
        String round_no = month + sequence;
        gameModel.setSequence(sequence);
        gameModel.setMonth(month);
        gameModel.setStatus(0);//0正在进行中，1停止竞猜，2已结束
        gameModel.setRound_no(round_no);
        gameModel.setHeight(0);
        gameModel.setBlock(0);
        gameModel.setHash("");
        gameModel.setTotalAmount(BigDecimal.ZERO);
        gameModel.setMinAmount(BigDecimal.ZERO);
        gameModel.setMaxAmount(BigDecimal.ZERO);
        gameModel.setWinning(0);//中奖类型：1小中奖，2大中奖
        gameModel.setWinningNum("");//中奖字符
        gameModel.setUserCount(0);
        gameModel.setMinUserCount(0);
        gameModel.setMaxUserCount(0);
        return gameModel;
    }

    private JSONObject getGameMothSequence(long startTime) {
        String month = this.getDateToString(new Date(startTime), "yyyyMM");
        JSONObject data = gameDao.getGameMonthSequence(month);
        int sequence = 0;
        if (data != null) {
            sequence = data.getIntValue("sequence");
        } else {
            data = new JSONObject();
        }
        data.put("sequence", sequence + 1);
        data.put("month", month);
        gameDao.saveGameMonthSequence(month, data);
        return data;
    }


    @Override
    public GameModel getGameDetail(String round_no) {
        if (StringUtils.isNotEmpty(round_no)) {
            GameModel detail = gameDao.getGameDetail(round_no);
            if (detail == null) {
                return null;
            }
            //活动是否结束
            int status = detail.getStatus();
            if (status == 0) {//状态，0进行中，1已结束，2已公布
                long endTime = detail.getEndTime();
                if (System.currentTimeMillis() > endTime) {
                    return this.updateGameRund(detail);
                }
            }
            return detail;
        }
        return null;

    }

    public GameModel getLastGameDetail() {
        String round_no = this.getLastGameRoundNo();
        System.out.println("round_no:" + round_no);
        return this.getGameDetail(round_no);

    }

    //获取上一轮数据
    public GameModel getUpRoundGameDetail() {
        String round_no = gameDao.getGameRundNo(GuessUtil.GAME_ROUND_TYPE.UpRound);
        return gameDao.getGameDetail(round_no);


    }

    public String getLastGameRoundNo() {
        return gameDao.getGameRundNo(GuessUtil.GAME_ROUND_TYPE.LastRound);

    }


    @Override
    public OrderModel getGamePay(String out_trade_no) {
        if (!StringUtils.isNotEmpty(out_trade_no)) {
            return null;
        }
        return gameDao.getGamePay(out_trade_no);
    }

    @Override
    public long saveGamePay(String out_trade_no, OrderModel detail) {
        int status = detail.getStatus();
        if (status == 1 || status == 6) {
            String openid = detail.getOpenid();
            String round_no = detail.getRound_no();
            BigDecimal amount = detail.getAmount();
            int guessType = detail.getGuessType();
            gameDao.saveGameUserMapping(round_no, openid, out_trade_no);
            if (status == 1) {
                this.saveGameSummary(round_no, guessType, amount);
            }
        }
        return gameDao.saveGamePay(out_trade_no, detail);

    }

    private long saveGameSummary(String round_no, int guessType, BigDecimal amount) {
        GameModel gameDetail = this.getGameDetail(round_no);
        if (gameDetail == null) {
            return -1;
        }
        BigDecimal totalAmount = gameDetail.getTotalAmount();//总数量
        BigDecimal minAmount = gameDetail.getMinAmount();//猜小数量
        BigDecimal maxAmount = gameDetail.getMaxAmount();//猜大数量
        long userCount = gameDetail.getUserCount();
        long minUserCount = gameDetail.getMinUserCount();
        long maxUserCount = gameDetail.getMaxUserCount();

        if (guessType == 1) {
            minAmount = minAmount.add(amount);
            minUserCount += 1;

        } else {
            maxAmount = maxAmount.add(amount);
            maxUserCount += 1;
        }
        userCount += 1;
        totalAmount = totalAmount.add(amount);
        gameDetail.setTotalAmount(totalAmount);//总数量
        gameDetail.setMinAmount(minAmount);//猜小数量
        gameDetail.setMaxAmount(maxAmount);//猜大数量
        gameDetail.setUserCount(userCount);
        gameDetail.setMinUserCount(minUserCount);
        gameDetail.setMaxUserCount(maxUserCount);

        return gameDao.saveGameList(round_no, gameDetail);

    }


    @Override
    public OrderModel getGameUser(String openid, String round_no) {

        String out_trade_no = gameDao.getGameUserMapping(round_no, openid);
        return gameDao.getGamePay(out_trade_no);
    }


    public List<OrderModel> getGameUserList(String round_no) {

        List<String> orderKeys = gameDao.getGameUserMappingList(round_no);
        if (orderKeys != null && orderKeys.size() > 0) {
            List<OrderModel> list = new ArrayList<>();
            for (String out_trade_no : orderKeys) {
                OrderModel order = gameDao.getGamePay(out_trade_no);
                if (order != null) {
                    list.add(order);
                }

            }
            return list;
        }
        return null;
    }


    // 时间格式化
    private static String getDateToString(Date date, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        //取得指定时区的时间：　　　　　　
        TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
        fmt.setTimeZone(zone);
        return fmt.format(date);
    }

    //获取当前时间后的第一个区块
    private JSONObject getLasBlock(long endTime) {
        try {
            List<JSONObject> blocks = new ArrayList<JSONObject>();
            String url = "https://etherscan.io/blocks";
            String resultStr = HttpClientUtils.get(url);
            if (StringUtils.isNotEmpty(resultStr)) {
                Document document = Jsoup.parse(resultStr);
                Element body = document.body();
                Elements list = body.getElementsByTag("tbody").first().children();
                if (list != null && list.size() > 0) {
                    for (Element row : list) {
                        String height = row.child(0).text();
                        String ageStr = row.child(1).getElementsByTag("span").attr("title");
                        Date timeStamp = new Date(ageStr);
                        if (timeStamp.getTime() > endTime) {
                            JSONObject block = new JSONObject();
                            block.put("height", height);
                            block.put("timeStamp", timeStamp.getTime());
                            blocks.add(block);
                        } else {
                            break;
                        }
                    }

                    if (blocks != null && blocks.size() > 0) {
                        return blocks.get(blocks.size() - 1);//取最靠近时间点的这个块
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //获取公布块信息
    private JSONObject getPublishedBlock(long block) {
        try {
            String url = String.format("https://etherscan.io/block/%s", block);
            String resultStr = HttpClientUtils.get(url);
            if (StringUtils.isNotEmpty(resultStr)) {
                Document document = Jsoup.parse(resultStr);
                Element body = document.body();
                Element content = body.getElementById("ContentPlaceHolder1_maintable");
                if (content == null) return null;

                String hash = this.getEtherscanTxValueByName(content, "Hash:");

                String timeStamp = this.getEtherscanTxValueByName(content, "TimeStamp:");
                //提取小括号中的值
                Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
                Matcher matcher = pattern.matcher(timeStamp);
                while (matcher.find()) {
                    timeStamp = matcher.group();
                    break;
                }
                JSONObject data = new JSONObject();
                data.put("hash", hash);
                data.put("timeStamp", new Date(timeStamp).getTime());
                return data;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getEtherscanTxValueByName(Element content, String tagName) {
        try {
            Elements elements = content.getElementsContainingOwnText(tagName);
            if (elements != null) {
                Element txReceiptStatus = elements.first().nextElementSibling();
                if (txReceiptStatus != null) {
                    return txReceiptStatus.text();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }


}
