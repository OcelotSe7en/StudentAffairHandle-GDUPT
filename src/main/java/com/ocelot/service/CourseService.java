package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;

import java.util.List;
import java.util.Map;

public interface CourseService {
    //查询课表
    List<Course> selectCourseTable(String studentId, String schoolYearAndTerm);
    //新增课表
    void addCourseTable(JSONArray courseArray, String studentId);
    //更新课表
    JSONObject updateCourseTable(JSONArray systemCourseArray, String studentId, String schoolYear);
    //删除课表
    JSONObject deleteCourseTable(List<Long> studentIdList);
}

