package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.ocelot.mapper.CourseMapper;
import com.ocelot.mapper.QualityExpansionActivitiesMapper;
import com.ocelot.model.QualityExpansionActivity;
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
    @Autowired
    QualityExpansionActivitiesMapper qualityExpansionActivitiesMapper;
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public List<QualityExpansionActivity> selectQualityExpansionActivity(String studentId) {

        return null;
    }

    @Override
    public int addQualityExpansionActivity(JSONArray qeaArray, String studentId) {
        //插入结果计数
        int insertResult = 0;
        String activityName, activitySchoolYearTerm, activitySensorStatus;
        float activityScore;
        Date activityTime, activitySensorTime;
        String qeaStr = qeaArray.toJSONString();
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        lettuceConnectionFactory.setDatabase(1);
        long studentIdLong = Long.parseLong(studentId);
        for(int i = 0; i < qeaArray.size(); i++){
            activityName = qeaArray.getJSONObject(i).getString("activityName");
            activitySchoolYearTerm = qeaArray.getJSONObject(i).getString("schoolYearTerm");
            activityTime = qeaArray.getJSONObject(i).getSqlDate("activityTime");
            activityScore = qeaArray.getJSONObject(i).getFloat("activityScore");
            activitySensorStatus = qeaArray.getJSONObject(i).getString("activitySensorStatus");
            activitySensorTime = qeaArray.getJSONObject(i).getSqlDate("activitySensorTime");
            Map<Object, Object> insertMap = new HashMap<>();
            insertMap.put("activityName", activityName);
            insertMap.put("activitySchoolYearTerm", activitySchoolYearTerm);
            insertMap.put("activityTime", activityTime);
            insertMap.put("activityScore", activityScore);
            insertMap.put("activitySensorStatus", activitySensorStatus);
            insertMap.put("activitySensorTime", activitySensorTime);
            insertMap.put("studentId", studentIdLong);
            insertResult += qualityExpansionActivitiesMapper.addQEActivities(insertMap);
        }
        System.out.println("已插入数据: "+insertResult);
        operations.set(studentId, qeaStr);
        return insertResult;
    }
}
