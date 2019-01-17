package java_sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {

            String appId = "b16487771e8342418f7e0c522df46d56";
            String mchId = "6112c3b2c7a64bd68614879915a88347";
            String key = "d70445a7cabf401482149a37290180cd";
            IMCConfig config = new IMCConfig("open.dev.iweipeng.com",appId, mchId, key);
            WXPay wxpay = new WXPay(config);
            //统一下单
            unifiedOrder(wxpay);
            //关闭订单
            // closeorder(wxpay);
            //退款
            //refund(wxpay);
            //查询订单
            //orderquery(wxpay);
            //查询退款订单
            //refundquery(wxpay);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //统一下单
    private static void unifiedOrder(WXPay wxpay) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "腾讯充值中心-QQ会员充值");
        data.put("out_trade_no", "2016090910595900000017");
        data.put("device_info", "");
        data.put("fee_type", "IMC");
        data.put("total_fee", "1");
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", "http://www.example.com/wxpay/notify");
        data.put("openid", "BB5483F7E69A495B8FA41B1012046E31");
        data.put("trade_type", "JSAPI");  // 此处指定为扫码支付
        data.put("product_id", "13");

        try {
            //Map<String, String> resp =wxpay.processResponseXml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xml><nonce_str>YcaLmpYTywaPtvRbl9lQiGqrw2cAFkVp</nonce_str><appid>imc9adf223edc554f31b6e61e8d2787f3b3</appid><sign>BB7E739005EDD528238752ADD95FA8494C0D41B91DE8690DE29C61EDA894588E</sign><err_code>100067</err_code><err_code_des>此功能暂未开通</err_code_des><mch_id>6112c3b2c7a64bd68614879915a88347</mch_id><return_code>FAIL</return_code><sign_type>HMAC-SHA256</sign_type></xml>");
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭订单
    private static void closeorder(WXPay wxpay) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", "2016090910595900000014");
            Map<String, String> resp = wxpay.closeOrder(data);
            System.out.println(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //退款
    private static void refund(WXPay wxpay) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_refund_no", "1415701182");
            data.put("out_trade_no", "2016090910595900000013");
            data.put("refund_fee", "1");
            data.put("total_fee", "1");
            data.put("transaction_id", "c4ad5ac7c522443cae1c7b418b916b0e");

            Map<String, String> resp = wxpay.refund(data);
            System.out.println(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //查询订单
    private static void orderquery(WXPay wxpay) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", "2016090910595900000013");
            Map<String, String> resp = wxpay.orderQuery(data);
            System.out.println(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //查询退款订单
    private static void refundquery(WXPay wxpay) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", "2016090910595900000013");
            Map<String, String> resp = wxpay.refundQuery(data);
            System.out.println(resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}


