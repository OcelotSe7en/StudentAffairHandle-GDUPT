package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.model.QualityExpansionActivity;
import com.ocelot.service.CourseService;
import com.ocelot.service.QualityExpansionActivitiesService;
import com.ocelot.util.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/*素拓分Controller*/
public class QEActivityController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    QualityExpansionActivitiesService qeaService;
    
    @RequestMapping(value = "/qeactivity", method = RequestMethod.POST)
    public JSONObject getClassTables(String studentId, String schoolYear, String studentPassword) throws IOException {
        JSONObject loginObject;//从studentLogin返回的JSON对象
        JSONObject returnObject = new JSONObject();//初始化函数返回的JSON对象
        JSONArray courseArray;//定义素拓分列表数组
        JSONArray qeaArrayFromSystem;//从教务系统获取的素拓分列表

        List<QualityExpansionActivity> qeaList = qeaService.selectQualityExpansionActivity(studentId);//从数据库/Redis获取的素拓分列表
        /*判断有无素拓分列表,有则带状态码返回素拓分列表,无则进入系统获取*/
        if (!qeaList.isEmpty()) {//判空
            String courseStr = JSON.toJSONString(qeaList);
            courseArray = JSONArray.parseArray(courseStr);
            returnObject.put("data", courseArray);
            returnObject.put("code", true);
            return returnObject;
        } else {
            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(true)) {
                qeaArrayFromSystem = SystemHandler.takeQualityExpansionActivities();
                //状态码永远在数组第0位
                String statusCode = qeaArrayFromSystem.getJSONObject(0).get("code").toString();
                //判断素拓分列表获取状态
                if (statusCode.equals("true")) {
                    qeaArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                    qeaService.addQualityExpansionActivity(qeaArrayFromSystem, studentId);
                } else {
                    return qeaArrayFromSystem.getJSONObject(0);
                }
                return getClassTables(studentId, schoolYear, studentPassword);
            } else {
                logger.info("用户 [{}] 未登录",studentId);
                return loginObject;
            }
        }
    }
}
