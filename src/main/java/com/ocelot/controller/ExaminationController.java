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

        if (studentId != null && !studentId.isEmpty() && !studentId.isBlank()) {
            //从数据库/Redis获取成绩
            List<Examination> examinationList = examinationService.selectExamination(studentId, schoolYear);

            /*判断有无成绩,有则带状态码返回成绩,无则进入系统获取*/
            if (!examinationList.isEmpty()) {//判空
                String courseStr = JSON.toJSONString(examinationList);
                examinationArray = JSONArray.parseArray(courseStr);
                responseObject.put("data", examinationArray);
                responseObject.put("code", 200);
                return responseObject;
            } else {
                if (studentPassword != null && !studentPassword.isEmpty() && !studentPassword.isBlank()) {
                    //执行登陆
                    loginObject = SystemHandler.studentLogin(studentId, studentPassword);
                    //判断登陆状态
                    if (loginObject.get("code").equals(200)) {
                        examinationArrayFromSystem = SystemHandler.takeExamination();
                        //状态码永远在数组第0位
                        String statusCode = examinationArrayFromSystem.getJSONObject(0).get("code").toString();
                        //判断课表获取状态
                        if (statusCode.equals("200")) {
                            examinationArrayFromSystem.remove(0);//判断为200后,将数组首位的状态码删除
                            examinationService.addExamination(examinationArrayFromSystem, studentId);
                            return getExamination(studentId, schoolYear, studentPassword);
                        } else {
                            responseObject = examinationArrayFromSystem.getJSONObject(0);
                        }
                    } else {
                        logger.info("用户 [{}] 未登录", studentId);
                        return loginObject;
                    }
                } else {
                    responseObject.put("msg", "请输入教务系统的账号密码!");
                    responseObject.put("code", 403);
                }
            }
        } else {
            responseObject.put("msg", "请输入学号!");
            responseObject.put("code", 403);
        }
        return responseObject;
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

        if (studentId != null && studentPassword != null && !studentId.isEmpty() && !studentId.isBlank() && !studentPassword.isEmpty() && !studentPassword.isBlank()) {

            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(200)) {
                examinationArrayFromSystem = SystemHandler.takeExamination();
                //状态码永远在数组第0位
                String statusCode = examinationArrayFromSystem.getJSONObject(0).get("code").toString();
                //判断课表获取状态
                if (statusCode.equals("200")) {
                    examinationArrayFromSystem.remove(0);//判断为200后,将数组首位的状态码删除
                    responseObject = examinationService.updateExamination(examinationArrayFromSystem, studentId, schoolYear);

                } else {
                    responseObject =  examinationArrayFromSystem.getJSONObject(0);
                }
            } else {
                logger.info("用户 [{}] 未登录",studentId);
                return loginObject;
            }
        }else{
            responseObject.put("msg", "请输入教务系统的账号密码!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }

    //    删除课表
    @RequestMapping(value = "/examination", method = RequestMethod.DELETE)
    public JSONObject deleteClassTable(@RequestBody JSONArray studentIdArray){
        JSONObject responseObject = new JSONObject();

        if(!studentIdArray.isEmpty()){
            //        构造一个存放学号的列表
            List<Long> studentIdList = new ArrayList<>();
            for(int i=0; i<studentIdArray.size(); i++){
//            将学号格式化成Long,存入列表
                String tmpStr = studentIdArray.getString(i);
                studentIdList.add(Long.parseLong(tmpStr));
            }
            responseObject = examinationService.deleteExamination(studentIdList);
        }else{
            responseObject.put("msg", "请至少输入一个学号!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }
}
