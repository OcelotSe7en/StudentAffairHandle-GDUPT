package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.QualityExpansionActivity;
import com.ocelot.service.QualityExpansionActivitiesService;
import com.ocelot.util.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*素拓分Controller*/
@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class QEActivityController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    QualityExpansionActivitiesService qeaService;

    //    获得素拓列表
    @RequestMapping(value = "/qeactivity", method = RequestMethod.POST)
    public JSONObject getQEActivity(String studentId, String studentPassword) throws IOException {
        JSONObject loginObject;//从studentLogin返回的JSON对象
        JSONObject responseObject = new JSONObject();//初始化函数返回的JSON对象
        JSONArray courseArray;//定义素拓分列表数组
        JSONArray qeaArrayFromSystem;//从教务系统获取的素拓分列表
        //仅作测试用途
        logger.debug("传入的用户名为: [{}], 密码为: [{}]", studentId, studentPassword);

        if (studentId != null && !studentId.isEmpty() && !studentId.isBlank()) {
            List<QualityExpansionActivity> qeaList = qeaService.selectQualityExpansionActivity(studentId);//从数据库/Redis获取的素拓分列表
            /*判断有无素拓分列表,有则带状态码返回素拓分列表,无则进入系统获取*/
            if (!qeaList.isEmpty()) {//判空
                String courseStr = JSON.toJSONString(qeaList);
                courseArray = JSONArray.parseArray(courseStr);
                responseObject.put("data", courseArray);
                responseObject.put("code", 200);
                return responseObject;
            } else {
                if (studentPassword != null && !studentPassword.isEmpty() && !studentPassword.isBlank()) {
                    loginObject = SystemHandler.studentLogin(studentId, studentPassword);
                    //判断登陆状态
                    if (loginObject.get("code").equals(200)) {
                        qeaArrayFromSystem = SystemHandler.takeQualityExpansionActivities();
                        //状态码永远在数组第0位
                        String statusCode = qeaArrayFromSystem.getJSONObject(0).get("code").toString();
                        //判断素拓分列表获取状态
                        if (statusCode.equals("200")) {
                            qeaArrayFromSystem.remove(0);//判断为200后,将数组首位的状态码删除
                            qeaService.addQualityExpansionActivity(qeaArrayFromSystem, studentId);
                            return getQEActivity(studentId, studentPassword);
                        } else {
                            return qeaArrayFromSystem.getJSONObject(0);
                        }
                    } else {
                        logger.info("用户 [{}] 未登录", studentId);
                        return loginObject;
                    }
                } else {
                    responseObject.put("msg", "请输入教务系统的账号密码!");
                    responseObject.put("code", 403);
                    return responseObject;
                }
            }
        } else {
            responseObject.put("msg", "请输入学号!");
            responseObject.put("code", 403);
            return responseObject;
        }
    }

    //    更新素拓列表
    @RequestMapping(value = "/qeactivity", method = RequestMethod.PUT)
    public JSONObject updateQEActivity(String studentId, String studentPassword) throws IOException {
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化函数返回的JSON对象
        JSONObject responseObject = new JSONObject();
        //从教务系统获取的素拓分列表
        JSONArray qeaArrayFromSystem;

        if (studentId != null && studentPassword != null && !studentId.isEmpty() && !studentId.isBlank() && !studentPassword.isEmpty() && !studentPassword.isBlank()) {
            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(200)) {
                qeaArrayFromSystem = SystemHandler.takeQualityExpansionActivities();
                //状态码永远在数组第0位
                String statusCode = qeaArrayFromSystem.getJSONObject(0).get("code").toString();
                //判断素拓分列表获取状态
                if (statusCode.equals("200")) {
                    qeaArrayFromSystem.remove(0);//判断为200后,将数组首位的状态码删除
                    responseObject = qeaService.updateQualityExpansionActivity(qeaArrayFromSystem, studentId);
                } else {
                    responseObject = qeaArrayFromSystem.getJSONObject(0);
                }
            } else {
                logger.info("用户 [{}] 未登录", studentId);
                return loginObject;
            }
        } else {
            responseObject.put("msg", "请输入教务系统的账号密码!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }

    //    删除素拓活动
    @RequestMapping(value = "/qeactivity", method = RequestMethod.DELETE)
    public JSONObject deleteClassTable(@RequestBody JSONArray studentIdArray) {
        JSONObject responseObject = new JSONObject();
        if (!studentIdArray.isEmpty()) {
            //        构造一个存放学号的列表
            List<Long> studentIdList = new ArrayList<>();
            for (int i = 0; i < studentIdArray.size(); i++) {
//            将学号格式化成Long,存入列表
                String tmpStr = studentIdArray.getString(i);
                studentIdList.add(Long.parseLong(tmpStr));
            }
            responseObject = qeaService.deleteQualityExpansionActivity(studentIdList);
        } else {
            responseObject.put("msg", "请至少输入一个学号!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }
}
