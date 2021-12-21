package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Examination;
import com.ocelot.service.ExaminationService;
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

/*成绩Controller*/
@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class ExaminationController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    ExaminationService examinationService;

    //    查询成绩
    @RequestMapping(value = "/examination", method = RequestMethod.POST)
    public JSONObject getExamination(String studentId, String studentPassword, String schoolYear) throws IOException {
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化函数返回的JSON对象
        JSONObject responseObject = new JSONObject();
        //定义课表数组
        JSONArray examinationArray;
        //从教务系统获取的课表
        JSONArray examinationArrayFromSystem;

        //从数据库/Redis获取课表
        List<Examination> examinationList = examinationService.selectExamination(studentId, schoolYear);

        /*判断有无课表,有则带状态码返回课表,无则进入系统获取*/
        if (!examinationList.isEmpty()) {//判空
            String courseStr = JSON.toJSONString(examinationList);
            examinationArray = JSONArray.parseArray(courseStr);
            responseObject.put("data", examinationArray);
            responseObject.put("code", true);
            return responseObject;
        } else {
            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(true)) {
                examinationArrayFromSystem = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
                //状态码永远在数组第0位
                String statusCode = examinationArrayFromSystem.getJSONObject(0).get("code").toString();
                //判断课表获取状态
                if (statusCode.equals("true")) {
                    examinationArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                    examinationService.addExamination(examinationArrayFromSystem, studentId);
                    return getExamination(studentId, schoolYear, studentPassword);
                } else {
                    return examinationArrayFromSystem.getJSONObject(0);
                }
            } else {
                logger.info("用户 [{}] 未登录", studentId);
                return loginObject;
            }
        }
    }

    //    更新课表
    @RequestMapping(value = "/examination", method = RequestMethod.PUT)
    public JSONObject updateExamination(String studentId, String studentPassword, String schoolYear) throws IOException{
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化函数返回的JSON对象
        JSONObject responseObject = new JSONObject();
        //定义课表数组
        JSONArray examinationArray;
        //从教务系统获取的课表
        JSONArray examinationArrayFromSystem;

        //执行登陆
        loginObject = SystemHandler.studentLogin(studentId, studentPassword);
        //判断登陆状态
        if (loginObject.get("code").equals(true)) {
            examinationArrayFromSystem = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
            //状态码永远在数组第0位
            String statusCode = examinationArrayFromSystem.getJSONObject(0).get("code").toString();
            //判断课表获取状态
            if (statusCode.equals("true")) {
                examinationArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                responseObject = examinationService.updateExamination(examinationArrayFromSystem, studentId, schoolYear);
                return responseObject;
            } else {
                return examinationArrayFromSystem.getJSONObject(0);
            }
        } else {
            logger.info("用户 [{}] 未登录",studentId);
            return loginObject;
        }
    }

    //    删除课表
    @RequestMapping(value = "/examination", method = RequestMethod.DELETE)
    public JSONObject deleteClassTable(@RequestBody JSONArray studentIdArray){
//        构造一个存放学号的列表
        List<Long> studentIdList = new ArrayList<>();
        for(int i=0; i<studentIdArray.size(); i++){
//            将学号格式化成Long,存入列表
            String tmpStr = studentIdArray.getString(i);
            studentIdList.add(Long.parseLong(tmpStr));
        }
        JSONObject responseObject = examinationService.deleteExamination(studentIdList);
        return responseObject;
    }
}
