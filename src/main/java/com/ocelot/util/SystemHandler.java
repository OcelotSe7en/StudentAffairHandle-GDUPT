package com.ocelot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.model.Examination;
import com.ocelot.model.QualityExpansionActivity;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
     *
     * @param account  学号
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
                CookieStore cookieStore = HttpPoolUtil.getCookieStore();
                logger.info(cookieStore.getCookies().toString());
                if (Objects.equals(status, "y")) {
                    loginFlag = 1;
                    jsonResponse.put("msg", "成功登陆");
                    jsonResponse.put("code", 200);
                    logger.info("学号: [{}] 登陆成功", account);
                    return jsonResponse;
                } else if (Objects.equals(status, "n")) {
                    loginFlag = 0;
                    jsonResponse.put("msg", msg);
                    jsonResponse.put("code", 403);
                    logger.error(jsonResponse.toJSONString());
                    logger.error(msg);
//                    清空cookie,避免连接超时
                    cookieStore.clear();
                    return jsonResponse;
                } else {
                    loginFlag = 0;
                    jsonResponse.put("error", "请求登陆失败!请检查日志!");
                    jsonResponse.put("code", 500);
                    logger.info("学号: [{}] 登陆失败", account);
                    logger.error(jsonResponse.toJSONString());
                    return jsonResponse;
                }
            } finally {
                httpClient.close();
            }
        } else {
            loginFlag = 0;
            jsonResponse.put("error", "访问失败,请确认教务系统能否正常访问");
            jsonResponse.put("code: ", 500);
            return jsonResponse;
        }
    }

    //从教务系统获取课表
    public static JSONArray takeClassTable(int schoolYear) throws IOException {
        Course course;
        JSONArray classArray = new JSONArray();
        JSONArray returnClassArray = new JSONArray();//返回的数组
        JSONObject statusCode = new JSONObject(); //标识成功/失败
        CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();

        try {
            if (loginFlag == 1) {
                HttpGet httpGet = new HttpGet("https://jwxt.gdupt.edu.cn/xsgrkbcx!getKbRq.action?xnxqdm=" + schoolYear);
                CloseableHttpResponse response = httpClient
                        .execute(httpGet);
                //获取响应头的实例
                HttpEntity entity = response.getEntity();
                //将实例转换为字符串
                String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                if(entityStr != null && !entityStr.isBlank() && !entityStr.isEmpty()){
                    //将字符串转换为JSONArray(二维数组)
                    JSONArray originArray = JSON.parseArray(entityStr);
                    //将JSONArray中课程信息部分提取出来,变为一维数组
                    classArray = originArray.getJSONArray(0);
                    statusCode.put("code", 200);
                    returnClassArray.add(0, statusCode);

                    for (int i = 0; i < classArray.size(); i++) {
                        course = JSON.parseObject(classArray.getJSONObject(i).toJSONString(), Course.class);
                        returnClassArray.add(course);
                    }
                }else{
                    logger.error("从教务系统获取课表失败! 原因: 参数错误或用户在该学期无课表(如: 18年入学的同学查询17年的课表)");
                    statusCode.put("msg", "参数错误或用户在该学期无课表");
                    statusCode.put("code", 403);
                    returnClassArray.add(0, statusCode);
                }
            } else {
                logger.error("从教务系统获取课表失败! 原因: 未登录,请先登录");
                statusCode.put("msg", "未登录,请先登录");
                statusCode.put("code", 403);
                returnClassArray.add(0, statusCode);
            }
            return returnClassArray;
        } finally {
            httpClient.close();
        }
    }

    //从教务系统获得素拓分相关信息
    public static JSONArray takeQualityExpansionActivities() throws IOException {
        QualityExpansionActivity activity = new QualityExpansionActivity();
        JSONObject statusCode = new JSONObject();//标识成功/失败
        JSONArray returnArray = new JSONArray();//返回的JSON数组
        if (loginFlag == 1) {
            CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();
            HttpGet getActivities = new HttpGet("https://jwxt.gdupt.edu.cn/xsktsbxx!getYxktDataList.action?" +
                    "xnxqdm=&page=1&rows=60&sort=cjsj&order=desc");
            CloseableHttpResponse response = httpClient
                    .execute(getActivities);
            //获取响应头的实例
            HttpEntity entity = response.getEntity();

            //将实例转换为字符串
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if(entityStr != null && !entityStr.isBlank() && !entityStr.isEmpty()){
                //取出返回的JSON对象中装有素拓项目的rows
                JSONArray activitiesArray = JSON.parseObject(entityStr).getJSONArray("rows");
                statusCode.put("code", 200);
                returnArray.add(0, statusCode);
                for (int i = 0; i < activitiesArray.size(); i++) {
                    activity = JSON.parseObject(activitiesArray.getJSONObject(i).toJSONString(),
                            QualityExpansionActivity.class);
                    returnArray.add(activity);
                }
            }else{
                logger.error("从教务系统获取课表失败! 原因: 参数错误");
                statusCode.put("msg", "参数错误");
                statusCode.put("code", 403);
                returnArray.add(0, statusCode);
            }

        } else {
            logger.error("从教务系统获取素拓分失败! 原因: 未登录,请先登录");
            statusCode.put("msg", "未登录,请先登录");
            statusCode.put("code", 403);
            returnArray.add(0, statusCode);
        }
        return returnArray;
    }

    //从教务系统获得成绩信息
    public static JSONArray takeExamination() throws IOException {
        Examination examination;
        JSONObject statusCode = new JSONObject();//标识成功/失败
        JSONArray returnArray = new JSONArray();//返回的JSON数组

        if (loginFlag == 1) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            //下列两行表示: 请求一页显示500行数据(不这么写无法一次获得所有数据)
            nvps.add(new BasicNameValuePair("page", "1"));
            nvps.add(new BasicNameValuePair("rows", "500"));
            CloseableHttpClient httpClient = HttpPoolUtil.getHttpClient();
            HttpPost postExamination = new HttpPost("https://jwxt.gdupt.edu.cn/xskccjxx!getDataList.action");
            postExamination.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            CloseableHttpResponse response = httpClient
                    .execute(postExamination);
            //获取响应头的实例
            HttpEntity entity = response.getEntity();
            //将实例转换为字符串
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if(entityStr != null && !entityStr.isBlank() && !entityStr.isEmpty()){
                //取出返回的JSON对象中装有成绩信息的rows
                JSONArray examinationArray = JSON.parseObject(entityStr).getJSONArray("rows");
                statusCode.put("code", 200);
                returnArray.add(statusCode);
                for (int i = 0; i < examinationArray.size(); i++) {
                    examination = JSON.parseObject(examinationArray.getJSONObject(i).toJSONString(),
                            Examination.class);
                    returnArray.add(examination);
                }
            }else{
                logger.error("从教务系统获取课表失败! 原因: 参数错误");
                statusCode.put("msg", "参数错误");
                statusCode.put("code", 403);
                returnArray.add(0, statusCode);
            }

        } else {
            logger.error("从教务系统获取成绩失败! 原因: 未登录,请先登录");
            statusCode.put("msg", "未登录,请先登录");
            statusCode.put("code", 403);
            returnArray.add(0, statusCode);
        }
        return returnArray;
    }
}
