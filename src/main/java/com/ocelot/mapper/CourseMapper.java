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
    int addCourseTable(Map<Object, Object> map);
    //更新课表
    int updateCourseTable(Map<Object, Object> map);
    //删除指定用户的课表
    int deleteCourseTable(Long studentId);

}
