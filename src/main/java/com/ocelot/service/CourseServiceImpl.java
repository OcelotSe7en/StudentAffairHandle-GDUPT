package com.ocelot.service;

import com.ocelot.mapper.CourseMapper;
import com.ocelot.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("CourseService")
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseMapper courseMapper;

    @Override
    public List<Course> selectCourseTable(String studentId, String schoolYear) {
        Map<String, String> map = new HashMap<>();
        if(!Objects.equals(studentId, "")&&!Objects.equals(studentId, null)){
            if (!Objects.equals(schoolYear, "")&&!Objects.equals(schoolYear, null)){
                map.put("studentId",studentId);
                map.put("schoolYear", schoolYear);
                return courseMapper.selectCourseTable(map);
            }else{
                map.put("studentId",studentId);
                return courseMapper.selectCourseTable(map);
            }
        }else{
            return null;
        }
    }

    @Override
    public void addCourseTable(Map<String, String> map) {

    }

    @Override
    public void deleteCourseTable(String studentId) {

    }
}
