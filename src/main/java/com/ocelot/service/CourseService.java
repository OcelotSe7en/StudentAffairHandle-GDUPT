package com.ocelot.service;

import com.ocelot.model.Course;

import java.util.List;
import java.util.Map;

public interface CourseService {
    //查询课表
    public List<Course> selectCourseTable(String studentId, String schoolYear);
    //新增课表
    public void addCourseTable(Map<String, String> map);
    //删除指定用户的课表
    public void deleteCourseTable(String studentId);
}

