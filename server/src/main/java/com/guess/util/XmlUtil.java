package com.guess.util;


import com.alibaba.fastjson.JSONObject;
import java_sdk.WXPayConstants;
import java_sdk.WXPayUtil;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class XmlUtil {

    public static JSONObject readerXmlToMap(HttpServletRequest request) {
        try {
            JSONObject map = new JSONObject();
            SAXReader reader = new SAXReader();
            InputStream in = request.getInputStream();
            org.dom4j.Document document = reader.read(in);
            Element root = document.getRootElement();
            List<Element> list = root.elements();
            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
            in.close();
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static JSONObject xmlToMap(String strXML) {
        try {
            JSONObject data = new JSONObject();
            DocumentBuilder documentBuilder = newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }

            stream.close();
            return data;
        } catch (Exception ex) {
            System.out.println(String.format("Invalid XML, can not convert to map. Error message: %s. XML content: %s", ex.getMessage(), strXML));
        }
        return null;

    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);

        return documentBuilderFactory.newDocumentBuilder();
    }

    public static Document newDocument() throws ParserConfigurationException {
        return newDocumentBuilder().newDocument();
    }


    public static String resultXml(JSONObject detail, Map<String, String> data, String appSecret) {
        try {
            String appid = detail.getString("appid");
            String mchid = detail.getString("mch_id");
            String signTypeInData = detail.getString(WXPayConstants.FIELD_SIGN_TYPE);
            return resultXml(appid, appSecret, mchid, signTypeInData, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String resultXml(String appid, String appSecret, String mchid, String signTypeInData, Map<String, String> reqData) {
        try {


            WXPayConstants.SignType signType;
            if (signTypeInData == null) {
                signType = WXPayConstants.SignType.MD5;
            } else {
                signTypeInData = signTypeInData.trim();
                if (signTypeInData.length() == 0) {
                    signType = WXPayConstants.SignType.MD5;
                } else if (WXPayConstants.MD5.equals(signTypeInData)) {
                    signType = WXPayConstants.SignType.MD5;
                } else if (WXPayConstants.HMACSHA256.equals(signTypeInData)) {
                    signType = WXPayConstants.SignType.HMACSHA256;
                } else {
                    System.out.println(String.format("Unsupported sign_type: %s", signTypeInData));
                    return "";
                }
            }

            reqData.put("appid", appid);
            reqData.put("mch_id", mchid);
            reqData.put("nonce_str", WXPayUtil.generateNonceStr());
            reqData.put("sign_type", signType.name());
            reqData.put("sign", WXPayUtil.generateSignature(reqData, appSecret, signType));
            System.out.println("reqData:" + JSONObject.toJSONString(reqData));
            return WXPayUtil.mapToXml(reqData);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
