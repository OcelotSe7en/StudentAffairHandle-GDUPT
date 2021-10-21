package com.ocelot.mapper;

import com.ocelot.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface CourseMapper {
    //查询课表
    List<Course> selectCourseTable(Map<String, String> map);
    //新增课表
    void addCourseTable(Map<String, String> map);
    //删除指定用户的课表
    void deleteCourseTable(String studentId);
}
