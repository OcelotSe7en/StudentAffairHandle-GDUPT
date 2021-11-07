package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.ocelot.model.Course;

import java.util.List;
import java.util.Map;

public interface CourseService {
    //Mysql查询课表
    public List<Course> selectCourseTable(String studentId, String schoolYear);
    //Mysql新增课表
    public void addCourseTable(JSONArray courseArray, String studentId);

    public void updateCourseTable(JSONArray systemCourseArray, String studentId, String schoolYear);

    //Mysql删除指定用户的课表
    public void deleteCourseTable(String studentId);

}

