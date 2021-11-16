package com.ocelot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.model.QualityExpansionActivity;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class SystemHandler {
    /*ForYourInformation
    * System特指教务系统
    * 所有与教务系统有关的API都在这里*/
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    private static int loginFlag = 0;

    /**
     * 学生登陆教务系统
     * @param account 学号
     * @param password 密码
     */
    public static JSONObject studentLogin(String account, String password) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        JSONObject data = new JSONObject();
        //给密码做Base64加密
        String passwordBase64 = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));

        CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();

        //访问一次系统,获取cookie
        HttpPost getCookie = new HttpPost("https://jwxt.gdupt.edu.cn");
        CloseableHttpResponse cookieResponse = httpClient.execute(getCookie);
        if (cookieResponse.getStatusLine().getStatusCode() == 200) {
            HttpPost loginPost = new HttpPost("https://jwxt.gdupt.edu.cn/login!doLogin.action");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("account", account));
            nvps.add(new BasicNameValuePair("pwd", passwordBase64));

            loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            //执行POST

            try (CloseableHttpResponse response = httpClient.execute(loginPost)) {
                //获取响应码
                int responseCode = response.getStatusLine().getStatusCode();
                System.out.println(responseCode);
                //获取响应头的实例
                HttpEntity entity = response.getEntity();
                //将实例转换为字符串
                String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                //将字符串转换为JSON
                JSONObject jsonObject = JSON.parseObject(entityStr);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                if (Objects.equals(status, "y")) {
                    loginFlag = 1;
                    jsonResponse.put("msg","成功登陆");
                    jsonResponse.put("code", true);
                    logger.info("学号: "+account+" 登陆成功");
                    return jsonResponse;
                } else if (Objects.equals(status, "n")) {
                    loginFlag = 0;
                    jsonResponse.put("data", data);
                    jsonResponse.put("code", false);
                    logger.error(jsonResponse.toJSONString());
                    return jsonResponse;
                } else {
                    loginFlag = 0;
                    jsonResponse.put("error","请求登陆失败!");
                    jsonResponse.put("code", false);
                    logger.info("学号: "+account+" 登陆失败");
                    logger.error(jsonResponse.toJSONString());
                    return jsonResponse;
                }
            } finally {
                httpClient.close();
            }
        } else {
            loginFlag = 0;
            jsonResponse.put("error","访问失败,请确认教务系统能否正常访问");
            jsonResponse.put("code: ", false);
            return jsonResponse;
        }
    }

    //从教务系统获取课表
    public static JSONArray takeClassTable(int schoolYear) throws IOException{
        Course course;
        JSONArray classArray = new JSONArray();
        JSONArray returnClassArray = new JSONArray();//返回的数组
        JSONObject statusCode = new JSONObject(); //标识成功/失败

        if (loginFlag == 1) {
            CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();
            HttpGet httpGet = new HttpGet("https://jwxt.gdupt.edu.cn/xsgrkbcx!getKbRq.action?xnxqdm=" + schoolYear);
            CloseableHttpResponse response = httpClient
                    .execute(httpGet);
            //获取响应头的实例
            HttpEntity entity = response.getEntity();
            //将实例转换为字符串
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            //将字符串转换为JSONArray(二维数组)
            JSONArray originArray = JSON.parseArray(entityStr);
            //将JSONArray中课程信息部分提取出来,变为一维数组
            classArray = originArray.getJSONArray(0);
            statusCode.put("code",true);
            returnClassArray.add(0, statusCode);
            try {
                for (int i = 0; i < classArray.size(); i++) {
                    course = JSON.parseObject(classArray.getJSONObject(i).toJSONString(), Course.class);
                    returnClassArray.add(course);
                }
            } finally {
                httpClient.close();
            }
            return returnClassArray;
        } else {
            logger.error("未登录,请先登录");
            statusCode.put("msg","未登录,请先登录");
            statusCode.put("code",false);
            returnClassArray.add(0,statusCode);
            return returnClassArray;
        }
    }

    //从教务系统获得素拓分相关信息
    /*
    * TODO:
    *  测试获取流程
    * */
    public static JSONArray takeQualityExpansionActivities() throws IOException{
        QualityExpansionActivity activity = new QualityExpansionActivity();
        JSONObject statusCode = new JSONObject();//标识成功/失败
        JSONArray returnArray = new JSONArray();//返回的JSON数组
        if(loginFlag == 1){
            CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();
            HttpGet getActivities = new HttpGet("https://jwxt.gdupt.edu.cn/xsktsbxx!getYxktDataList.action?" +
                    "xnxqdm=&page=1&rows=60&sort=cjsj&order=desc");
            CloseableHttpResponse response = httpClient
                    .execute(getActivities);
            //获取响应头的实例
            HttpEntity entity = response.getEntity();
            //将实例转换为字符串
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            //取出返回的JSON对象中装有素拓项目的rows
            JSONArray activitiesArray = JSON.parseObject(entityStr).getJSONArray("rows");
            statusCode.put("code",true);
            returnArray.add(0, statusCode);
            for(int i = 0; i < activitiesArray.size(); i++){
                activity = JSON.parseObject(activitiesArray.getJSONObject(i).toJSONString(),
                        QualityExpansionActivity.class);
                returnArray.add(activity);
            }
        }else{
            logger.error("未登录,请先登录");
            statusCode.put("msg","未登录,请先登录");
            statusCode.put("code",false);
            returnArray.add(0, statusCode);
        }
        return returnArray;
    }
}
