package com.ocelot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.mapper.CourseMapper;
import com.ocelot.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("CourseService")
public class CourseServiceImpl implements CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 从Redis/MySql查询课表
     *
     * @param studentId         学号,不允许为空
     * @param courseSchoolYearTerm 学年学期,允许为空,格式为"年份+学期(01/02)",例如"202101"表示2021-2022学年第一学期
     * @return courseList 返回一个List类型的课表
     */
    @Override
    public List<Course> selectCourseTable(String studentId, String courseSchoolYearTerm) throws NullPointerException {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        List<Course> courseList;
        Map<String, String> selectMap = new HashMap<>();
        String keys = studentId + "_Course_"+courseSchoolYearTerm;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(keys))) {
            String classStr = operations.get(keys);
            courseList = JSON.parseArray(classStr, Course.class);
            return courseList;
        } else {
            selectMap.put("studentId", studentId);
            selectMap.put("courseSchoolYearTerm", courseSchoolYearTerm);
            courseList = courseMapper.selectCourseTable(selectMap);
            return courseList;
        }
    }

    /**
     * 向Redis/Mysql新增课表
     *
     * @param courseArray 从教务系统获取的课表,不允许为空
     * @param studentId   学号,不允许为空
     */
    @Override
    public void addCourseTable(JSONArray courseArray, String studentId) {
        //新增结果计数
        int insertResult = 0;
        String courseName, courseTeacher, courseLocation,
                courseWeekDay, courseClass, courseWeek, courseSchoolYearTerm;
        JSONArray redisArray = new JSONArray();

        long studentIdLong = Long.parseLong(studentId);
        String key = studentId + "_Course_"+courseArray.getJSONObject(0).getString("courseSchoolYearTerm");
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        for (int i = 0; i < courseArray.size(); i++) {
            courseName = courseArray.getJSONObject(i).getString("courseName");
            courseTeacher = courseArray.getJSONObject(i).getString("courseTeacher");
            courseLocation = courseArray.getJSONObject(i).getString("courseLocation");
            courseWeekDay = courseArray.getJSONObject(i).getString("courseWeekDay");
            courseWeek = courseArray.getJSONObject(i).getString("courseWeek");
            courseClass = courseArray.getJSONObject(i).getString("courseClass");
            courseSchoolYearTerm = courseArray.getJSONObject(i).getString("courseSchoolYearTerm");
            Map<Object, Object> insertMap = new HashMap<>();
            insertMap.put("courseName", courseName);
            insertMap.put("courseTeacher", courseTeacher);
            insertMap.put("courseLocation", courseLocation);
            insertMap.put("courseWeekDay", courseWeekDay);
            insertMap.put("courseWeek", courseWeek);
            insertMap.put("courseClass", courseClass);
            insertMap.put("courseSchoolYearTerm", courseSchoolYearTerm);
            insertMap.put("studentId", studentIdLong);

            //将插入的数据存入jsonArray,方便存入redis
            redisArray.add(insertMap);

            insertResult += courseMapper.addCourseTable(insertMap);
        }
        logger.debug("用户: [{}] 更新数据为: [{}], 共[{}]条数据", studentId, redisArray, insertResult);
        String courseStr = JSONArray.toJSONString(redisArray);
//        将数据存入Redis
        operations.set(key, courseStr);
        logger.info("用户: [{}] 已新增 [{}] 课表信息至Mysql&Redis", studentId, insertResult);
    }

    /**
     * 向Redis/MySql更新课表
     *
     * @param systemCourseArray 从教务系统获取的课表数组，用于更新
     * @param studentId         学号,不允许为空
     * @param schoolYear        学年学期,允许为空,格式为"年份+学期(01/02)",例如"202101"表示2021-2022学年第一学期
     * @return responseObject 一个JSONObject，用于存放返回的信息
     */
    @Override
    public JSONObject updateCourseTable(JSONArray systemCourseArray, String studentId, String schoolYear) {
        JSONObject responseObject = new JSONObject();
        JSONArray redisArray = new JSONArray();
        int updateResult = 0;
        String courseName, courseTeacher, courseLocation,
                courseWeekDay, courseClass, courseWeek, courseSchoolYearTerm;
        long studentIdLong = Long.parseLong(studentId);

        String key = studentId + "_Course";
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        if (!systemCourseArray.isEmpty()) {
            for (int i = 0; i < systemCourseArray.size(); i++) {
                courseName = systemCourseArray.getJSONObject(i).getString("courseName");
                courseTeacher = systemCourseArray.getJSONObject(i).getString("courseTeacher");
                courseLocation = systemCourseArray.getJSONObject(i).getString("courseLocation");
                courseWeekDay = systemCourseArray.getJSONObject(i).getString("courseWeekDay");
                courseWeek = systemCourseArray.getJSONObject(i).getString("courseWeek");
                courseClass = systemCourseArray.getJSONObject(i).getString("courseClass");
                courseSchoolYearTerm = systemCourseArray.getJSONObject(i).getString("courseSchoolYearTerm");
                Map<Object, Object> updateMap = new HashMap<>();
                updateMap.put("courseName", courseName);
                updateMap.put("courseTeacher", courseTeacher);
                updateMap.put("courseLocation", courseLocation);
                updateMap.put("courseWeekDay", courseWeekDay);
                updateMap.put("courseWeek", courseWeek);
                updateMap.put("courseClass", courseClass);
                updateMap.put("courseSchoolYearTerm", courseSchoolYearTerm);
                updateMap.put("studentId", studentIdLong);
                updateResult += courseMapper.updateCourseTable(updateMap);
                redisArray.add(updateMap);
            }
            //将数据存入Redis
            operations.set(key, redisArray.toJSONString());
            logger.debug("用户: [{}] 更新数据为: [{}], 共[{}]条数据", studentId, redisArray, updateResult);
            responseObject.put("msg", "更新成功");
            responseObject.put("code", true);
            logger.info("用户: [{}] 已更新 [{}] 条课程信息", studentId, updateResult);
        } else {
            responseObject.put("msg", "列表不能为空!");
            responseObject.put("code", false);
        }
        return responseObject;
    }

    /**
     * 向Redis/MySql更新课表
     *
     * @param studentIdList 学号列表，用于批量删除学号
     * @return responseObject 一个JSONObject，用于存放返回的信息
     */
    @Override
    public JSONObject deleteCourseTable(List<Long> studentIdList) {
        //删除结果计数
        int deleteResult = 0;
        JSONObject responseObject = new JSONObject();

        if (!studentIdList.isEmpty()) {
            for (int i = 0; i < studentIdList.size(); i++) {
                String keys = studentIdList.get(i) + "_Course*";
                RedisCallback<Long> redisCallback = connection -> {
                    ScanOptions.ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions();
                    scanOptionsBuilder.match(keys);
                    ScanOptions scanOptions = scanOptionsBuilder.build();
                    Cursor<byte[]> cursor = connection.scan(scanOptions);
                    long count = 0;
                    while (cursor.hasNext()) {
                        count += connection.del(cursor.next());     //count为所有删除成功次数的总和
                    }
                    return count;

                };
                Long count = (Long) redisTemplate.execute(redisCallback);
            }


            deleteResult = courseMapper.deleteCourseTable(studentIdList);
            responseObject.put("result", deleteResult);
            responseObject.put("code", true);
            logger.info("已批量删除: [{}] 的课表记录, 共删除: [{}] 条", studentIdList, deleteResult);

        } else {
            responseObject.put("msg", "学号列表不能为空");
            responseObject.put("code", false);
            logger.warn("学号List不能为空");
        }
        return responseObject;
    }

    @Override
    public void addCourseTableToRedis(String studentId ,List<Course> list){
        int year = list.get(0).getCourseSchoolYearTerm();

        String key = studentId + "_Course_"+year;
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        String courseStr = JSON.toJSONString(list);
        operations.set(key, courseStr);

        logger.info("用户: [{}] 已新增课表信息至Redis", studentId);
    }


}
