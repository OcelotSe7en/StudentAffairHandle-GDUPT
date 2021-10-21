package com.ocelot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ocelot.model.Course;
import com.ocelot.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/classtable")
public class ClassTableController {
    @Autowired
    CourseService courseService;

    @RequestMapping("/classtables")
    public List<Course> getClassTables(){
        String account = "17034480126";
        List<Course> courseList = courseService.selectCourseTable(account,null);
        System.out.println(courseList);
        String courseStr = JSON.toJSONString(courseList);
        JSONArray courseArray = JSONArray.parseArray(courseStr);
        return courseList;
    }

}
