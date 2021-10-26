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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class ClassTableController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);
    @Autowired
    CourseService courseService;
    int count = 2;//回调计数

    @RequestMapping(value = "/classtable", method = RequestMethod.POST)
    public JSONObject getClassTables(String studentId, String schoolYear, String password) throws IOException {
        logger.info("已为用户: "+studentId+"调取getClassTables");
        JSONObject loginObject;//从studentLogin返回的JSON对象
        JSONObject returnObject = new JSONObject();//初始化函数返回的JSON对象
        JSONArray courseArray;//定义课表数组
        JSONArray classArrayFromSystem;//从教务系统获取的课表
        List<Course> courseList = courseService.selectCourseTable(studentId, schoolYear);//从数据库/Redis获取的课表
        //判断有无课表
        if(count != 0) {
            if(!courseList.isEmpty()){//判空
                String courseStr = JSON.toJSONString(courseList);
                courseArray = JSONArray.parseArray(courseStr);
                returnObject.put("data", courseArray);
                returnObject.put("code", true);
                return returnObject;
            }else{
                count -= 1;
                loginObject =  SystemHandler.studentLogin(studentId,password);
                if(loginObject.get("code").equals(true)) {
                    classArrayFromSystem = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
                    String statusCode = classArrayFromSystem.getJSONObject(0).get("code").toString();
                    if(statusCode.equals("true")){
                        classArrayFromSystem.remove(0);//判断为true后,将数组首位的状态码删除
                        courseService.addCourseTable(classArrayFromSystem, studentId);
                    }
                    else{
                        return classArrayFromSystem.getJSONObject(0);
                    }
                    return getClassTables(studentId, schoolYear, password);
                }else{
                    return loginObject;
                }
            }
        }else{
            returnObject.put("code", false);
            returnObject.put("error", "三次调取失败,请检查日志");
            return returnObject;
        }
    }
}
