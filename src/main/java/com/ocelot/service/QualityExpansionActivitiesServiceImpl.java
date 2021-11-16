package com.ocelot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ocelot.mapper.CourseMapper;
import com.ocelot.mapper.QualityExpansionActivitiesMapper;
import com.ocelot.model.Course;
import com.ocelot.model.QualityExpansionActivity;
import com.ocelot.util.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("QualityExpansionActivitiesService")
public class QualityExpansionActivitiesServiceImpl implements QualityExpansionActivitiesService{
    private static final Logger logger = LoggerFactory.getLogger(QualityExpansionActivitiesServiceImpl.class);
    @Autowired
    QualityExpansionActivitiesMapper qualityExpansionActivitiesMapper;
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public List<QualityExpansionActivity> selectQualityExpansionActivity(String studentId) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        List<QualityExpansionActivity> qeaList;
        Map<Object, Object> selectMap = new HashMap<>();
        Long studentIdLong = Long.parseLong(studentId);
        String key = studentId + "_QEActivities";

        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            String classStr = operations.get(key);
            qeaList = JSON.parseArray(classStr, QualityExpansionActivity.class);
        } else{
            selectMap.put("studentId", studentIdLong);
            qeaList = qualityExpansionActivitiesMapper.selectQEActivitiesByStudentId(selectMap);
        }
        return qeaList;
    }

    @Override
    public int addQualityExpansionActivity(JSONArray qeaArray, String studentId) {
        //新增结果计数
        int insertResult = 0;

        String activityName, activitySchoolYearTerm, activitySensorStatus;
        float activityScore;
        Date activityTime, activitySensorTime;
        long activityId;
        long studentIdLong = Long.parseLong(studentId);

        String key = studentId+"_QEActivities";
        String qeaStr = qeaArray.toJSONString();//转换成字符串,方便存进redis
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        for(int i = 0; i < qeaArray.size(); i++){
            activityId = qeaArray.getJSONObject(i).getLong("activityId");
            activityName = qeaArray.getJSONObject(i).getString("activityName");
            activitySchoolYearTerm = qeaArray.getJSONObject(i).getString("schoolYearTerm");
            activityTime = qeaArray.getJSONObject(i).getSqlDate("activityTime");
            activityScore = qeaArray.getJSONObject(i).getFloat("activityScore");
            activitySensorStatus = qeaArray.getJSONObject(i).getString("activitySensorStatus");
            activitySensorTime = qeaArray.getJSONObject(i).getSqlDate("activitySensorTime");
            Map<Object, Object> insertMap = new HashMap<>();
            insertMap.put("activityId", activityId);
            insertMap.put("activityName", activityName);
            insertMap.put("activitySchoolYearTerm", activitySchoolYearTerm);
            insertMap.put("activityTime", activityTime);
            insertMap.put("activityScore", activityScore);
            insertMap.put("activitySensorStatus", activitySensorStatus);
            insertMap.put("activitySensorTime", activitySensorTime);
            insertMap.put("studentId", studentIdLong);
            insertResult += qualityExpansionActivitiesMapper.addQEActivities(insertMap);
            logger.debug("正在插入 "+ insertMap);
        }
        operations.set(key, qeaStr);
        logger.info("用户: "+studentId+" 已新增 "+insertResult+" 条素拓活动信息");
        return insertResult;
    }

    @Override
    public int updateQualityExpansionActivity(JSONArray qeaArray, String studentId) {
        int updateResult = 0;
        String activitySensorStatus;
        Date activitySensorTime;
        float activityScore;
        long activityId;

        if(!qeaArray.isEmpty() && studentId!=null && !studentId.equals("")){
            Long studentIdLong = Long.parseLong(studentId);
            for(int i=0; i < qeaArray.size(); i++){
                activityId = qeaArray.getJSONObject(i).getLong("activityId");
                activityScore = qeaArray.getJSONObject(i).getFloat("activityScore");
                activitySensorStatus = qeaArray.getJSONObject(i).getString("activitySensorStatus");
                activitySensorTime = qeaArray.getJSONObject(i).getSqlDate("activitySensorTime");
                Map<Object, Object> updateMap = new HashMap<>();
                updateMap.put("activityId", activityId);
                updateMap.put("activityScore", activityScore);
                updateMap.put("activitySensorStatus", activitySensorStatus);
                updateMap.put("activitySensorTime", activitySensorTime);
                updateMap.put("studentId", studentIdLong);
                updateResult += qualityExpansionActivitiesMapper.updateQEActivity(updateMap);
                logger.debug("正在插入 "+ updateMap);
            }
            logger.info("用户: "+studentId+" 已更新 "+updateResult+" 条素拓活动信息");
            return updateResult;
        }
        return 0;
    }

    @Override
    public int deleteQualityExpansionActivityById(String studentId) {
        int deleteResult = 0;

        if(studentId!=null && !studentId.equals("")){
            Long studentIdLong = Long.parseLong(studentId);
            redisTemplate.delete(studentId);
            deleteResult +=  qualityExpansionActivitiesMapper.deleteQEActivitiesByStudentId(studentIdLong);
            logger.info("已删除用户: "+studentId+"的"+deleteResult+"条素拓活动信息");
            return deleteResult;
        }else{
            logger.error("学号不能为空");
            return 0;
        }
    }
}
