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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*素拓分Controller*/
@RequestMapping("/api")
public class QEActivityController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    QualityExpansionActivitiesService qeaService;
//    获得素拓列表
    @RequestMapping(value = "/qeactivity", method = RequestMethod.POST)
    public JSONObject getQEActivity(String studentId, String studentPassword) throws IOException {
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
            if(studentId.isEmpty()||studentPassword.isEmpty()||studentPassword.isBlank()||studentId.isBlank()){
                returnObject.put("msg","请输入教务系统的账号密码!");
                returnObject.put("code", false);
                return returnObject;
            }else{//执行登陆
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
                        return getQEActivity(studentId, studentPassword);
                    } else {
                        return qeaArrayFromSystem.getJSONObject(0);
                    }
                } else {
                    logger.info("用户 [{}] 未登录",studentId);
                    return loginObject;
                }
            }
        }
    }
//    更新素拓列表
    @RequestMapping(value = "/qeactivity", method = RequestMethod.PUT)
    public JSONObject updateQEActivity(String studentId, String studentPassword) throws IOException{
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化函数返回的JSON对象
        JSONObject returnObject = new JSONObject();
        //定义素拓分列表数组
        JSONArray courseArray;
        //从教务系统获取的素拓分列表
        JSONArray qeaArrayFromSystem;

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
                return qeaService.updateQualityExpansionActivity(qeaArrayFromSystem, studentId);
            } else {
                return qeaArrayFromSystem.getJSONObject(0);
            }
        } else {
            logger.info("用户 [{}] 未登录",studentId);
            return loginObject;
        }
    }
//    删除素拓活动
    @RequestMapping(value = "/classtable", method = RequestMethod.DELETE)
    public JSONObject deleteClassTable(@RequestBody JSONArray studentIdArray){
//        构造一个存放学号的列表
        List<Long> studentIdList = new ArrayList<>();
        for(int i=0; i<studentIdArray.size(); i++){
//            将学号格式化成Long,存入列表
            String tmpStr = studentIdArray.getString(i);
            studentIdList.add(Long.parseLong(tmpStr));
        }
        return qeaService.deleteQualityExpansionActivity(studentIdList);
    }
}
