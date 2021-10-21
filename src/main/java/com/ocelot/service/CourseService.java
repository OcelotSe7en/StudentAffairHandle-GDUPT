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
    //Mysql删除指定用户的课表
    public void deleteCourseTable(String studentId);

    //Redis查询课表
    public List<Course> selectCourseTableFromRedis(String studentId);
    //Redis新增课表
    public void addCourseTableToRedis(JSONArray courseArray, String studentId);
    //Redis删除课表
    public void deleteCourseTableInRedis(String studentId);
}

