package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.service.CourseService;
import com.ocelot.util.SystemHandler;
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
    @Autowired
    CourseService courseService;
    int count = 2;//回调计数

    @RequestMapping(value = "/classtable", method = RequestMethod.POST)
    public JSONObject getClassTables(String studentId, String schoolYear, String password) throws IOException {
        JSONObject loginObject;//从studentLogin返回的JSON对象
        JSONObject returnObject = new JSONObject();//初始化函数返回的JSON对象
        JSONArray courseArray;//定义课表数组
        List<Course> courseList = courseService.selectCourseTable(studentId, schoolYear);
        //判断有无课表
        if(count != 0) {
            if(!courseList.isEmpty()){//判空
                String courseStr = JSON.toJSONString(courseList);
                courseArray = JSONArray.parseArray(courseStr);
                returnObject.put("data",courseArray);
                returnObject.put("code","True");
                return returnObject;
            }else{
                count -= 1;
                loginObject =  SystemHandler.studentLogin(studentId,password);
                JSONArray classArray = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
                courseService.addCourseTable(classArray, studentId);
                return getClassTables(studentId, schoolYear, password);
            }
        }else{
            returnObject.put("code", "False");
            returnObject.put("error", "false");
            return returnObject;
        }
    }
}
