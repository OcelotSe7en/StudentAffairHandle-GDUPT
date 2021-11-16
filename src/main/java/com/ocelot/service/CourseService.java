package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.ocelot.model.Course;

import java.util.List;
import java.util.Map;

public interface CourseService {
    //查询课表
    List<Course> selectCourseTable(String studentId, String schoolYear);
    //新增课表
    void addCourseTable(JSONArray courseArray, String studentId);
    //更新课表
    void updateCourseTable(JSONArray systemCourseArray, String studentId, String schoolYear);
    //删除指定用户的课表
    int deleteCourseTable(String studentId);
}

