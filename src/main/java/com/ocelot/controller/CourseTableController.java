package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.service.CourseService;
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

@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class CourseTableController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    CourseService courseService;

    //  查询课表
    @RequestMapping(value = "/classtable", method = RequestMethod.POST)
    public JSONObject getClassTable(String studentId, String studentPassword, String schoolYear) throws IOException {
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化方法返回的JSON对象
        JSONObject responseObject = new JSONObject();
        //学期数组，学期数组包含每周课表数据。
        JSONArray termArray = new JSONArray();
        //周数组，包含每日课表数据.
        JSONArray weekArray = new JSONArray();
        //日数组，包含每节次课表数据。
        JSONArray dayArray = new JSONArray();
        JSONArray courseArray;
        Course course;
        //从教务系统获取的课表
        JSONArray classArrayFromSystem;

        if (studentId != null &&!studentId.isEmpty() && !studentId.isBlank()) {
            //从数据库/Redis获取课表
            List<Course> courseList = courseService.selectCourseTable(studentId, schoolYear);
            /*判断有无课表,有则带状态码返回课表,无则进入系统获取*/
            if (!courseList.isEmpty()) {//判空
                courseArray = JSONArray.parseArray(JSON.toJSONString(courseList));
                responseObject.put("data", courseArray);
                responseObject.put("code", 200);
            } else {
//            判断学号密码是否正确输入
                if (studentPassword != null && !studentPassword.isEmpty() && !studentPassword.isBlank()) {
                    //执行登陆
                    loginObject = SystemHandler.studentLogin(studentId, studentPassword);
                    //判断登陆状态
                    if (loginObject.get("code").equals(200)) {
                        classArrayFromSystem = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
                        //状态码永远在数组第0位
                        String statusCode = classArrayFromSystem.getJSONObject(0).get("code").toString();
                        //判断课表获取状态
                        if (statusCode.equals("true")) {
                            classArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                            courseService.addCourseTable(classArrayFromSystem, studentId);
                            courseService.addCourseTableToRedis(studentId, courseService.selectCourseTable(studentId, schoolYear));
                            return getClassTable(studentId, schoolYear, studentPassword);
                        } else {
                            return classArrayFromSystem.getJSONObject(0);
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
    @RequestMapping(value = "/classtable", method = RequestMethod.PUT)
    public JSONObject updateClassTable(String studentId, String studentPassword, String schoolYear) throws IOException {
        //从studentLogin返回的JSON对象
        JSONObject loginObject;
        //初始化函数返回的JSON对象
        JSONObject responseObject = new JSONObject();
        //定义课表数组
        JSONArray courseArray;
        //从教务系统获取的课表
        JSONArray classArrayFromSystem;

        if (studentId != null && studentPassword != null && !studentId.isEmpty() && !studentId.isBlank() && !studentPassword.isEmpty() && !studentPassword.isBlank()) {
            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(200)) {
                classArrayFromSystem = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
                //状态码永远在数组第0位
                String statusCode = classArrayFromSystem.getJSONObject(0).get("code").toString();
                //判断课表获取状态
                if (statusCode.equals("true")) {
                    classArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                    responseObject = courseService.updateCourseTable(classArrayFromSystem, studentId, schoolYear);
                    return responseObject;
                } else {
                    return classArrayFromSystem.getJSONObject(0);
                }
            } else {
                logger.info("用户 [{}] 未登录", studentId);
                return loginObject;
            }
        }else{
            responseObject.put("msg", "请输入教务系统的账号密码!");
            responseObject.put("code", 403);
            return responseObject;
        }
    }

    //    删除课表
    @RequestMapping(value = "/classtable", method = RequestMethod.DELETE)
    public JSONObject deleteClassTable(@RequestBody JSONArray studentIdArray) {
        JSONObject responseObject = new JSONObject();
//        构造一个存放学号的列表
        if(!studentIdArray.isEmpty()){
            List<Long> studentIdList = new ArrayList<>();
            for (int i = 0; i < studentIdArray.size(); i++) {
//            将学号格式化成Long,存入列表
                String tmpStr = studentIdArray.getString(i);
                studentIdList.add(Long.parseLong(tmpStr));
            }
            responseObject = courseService.deleteCourseTable(studentIdList);
        }else{
            responseObject.put("msg", "请至少输入一个学号!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }
}
