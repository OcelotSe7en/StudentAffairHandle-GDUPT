package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ocelot.model.Course;
import com.ocelot.service.CourseService;
import com.ocelot.util.SystemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api/classtable")
public class ClassTableController {
    @Autowired
    CourseService courseService;

    @RequestMapping(value = "/classtable", method = RequestMethod.POST)
    public JSONArray getClassTables(String studentId, String schoolYear, String password) throws IOException {
        JSONArray courseArray = new JSONArray();
        List<Course> courseList = courseService.selectCourseTable(studentId, schoolYear);
        //判断有无课表
        if(!courseList.isEmpty()){
            String courseStr = JSON.toJSONString(courseList);
            courseArray = JSONArray.parseArray(courseStr);
            return courseArray;
        }else{
            System.out.println("查无课表, 正在添加.........");
            SystemHandler.studentLogin(studentId,password);
            JSONArray classArray = SystemHandler.takeClassTable(Integer.parseInt(schoolYear));
            courseService.addCourseTable(classArray, studentId);
            return getClassTables(studentId, schoolYear, password);
        }
    }
}
