package com.ocelot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ocelot.config.RedisConfig;
import com.ocelot.mapper.CourseMapper;
import com.ocelot.model.Course;
import com.ocelot.util.SystemHandler;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("CourseService")
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 从Redis/MySql查询课表
     * @param studentId 学号,不允许为空
     * @param schoolYear 学年学期,允许为空,格式为"年份+学期(01/02)",例如"202101"表示2021-2022学年第一学期
     * @return courseList 返回一个List类型的课表
     */
    @Override
    public List<Course> selectCourseTable(String studentId, String schoolYear) throws NullPointerException {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        List<Course> courseList;
        Map<String, String> selectMap = new HashMap<>();

        String key = studentId + "_Course";
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            String classStr = operations.get(key);
            courseList = JSON.parseArray(classStr, Course.class);
            return courseList;
        } else{
            selectMap.put("studentId", studentId);
            selectMap.put("schoolYear", schoolYear);
            courseList = courseMapper.selectCourseTable(selectMap);
            return courseList;
        }
    }

    @Override
    public void addCourseTable(JSONArray courseArray, String studentId) {
        String courseName, courseTeacher, courseLocation, courseWeekDay, courseClass, courseWeek, courseSchoolYear;
        String courseStr = courseArray.toJSONString();
        String key = studentId+"_Course";

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        for(int i = 0; i < courseArray.size(); i++){
            courseName = courseArray.getJSONObject(i).getString("courseName");
            courseTeacher = courseArray.getJSONObject(i).getString("courseTeacher");
            courseLocation = courseArray.getJSONObject(i).getString("courseLocation");
            courseWeekDay = courseArray.getJSONObject(i).getString("courseWeekDay");
            courseWeek = courseArray.getJSONObject(i).getString("courseWeek");
            courseClass = courseArray.getJSONObject(i).getString("courseClass");
            courseSchoolYear = courseArray.getJSONObject(i).getString("courseSchoolYear");
            Map<String, String> insertMap = new HashMap<>();
            insertMap.put("courseName", courseName);
            insertMap.put("courseTeacher", courseTeacher);
            insertMap.put("courseLocation", courseLocation);
            insertMap.put("courseWeekDay", courseWeekDay);
            insertMap.put("courseWeek", courseWeek);
            insertMap.put("courseClass", courseClass);
            insertMap.put("courseSchoolYear", courseSchoolYear);
            insertMap.put("studentId", studentId);
            int insertResult = courseMapper.addCourseTable(insertMap);
            System.out.println("已插入数据: "+insertResult);
        }
        operations.set(key, courseStr);
    }

    @Override
    public void deleteCourseTable(String studentId) {

    }

    @Override
    public List<Course> selectCourseTableFromRedis(String studentId) {

        return null;
    }

    @Override
    public void addCourseTableToRedis(JSONArray courseArray, String studentId) {
        String courseStr = courseArray.toJSONString();
        String key = studentId+"_Course";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, courseStr);
    }

    @Override
    public void deleteCourseTableInRedis(String studentId) {

    }
}
