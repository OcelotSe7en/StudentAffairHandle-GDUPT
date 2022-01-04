package com.ocelot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        logger.info(redisTemplate.hasKey(key).toString());
        if(redisTemplate.hasKey(key)){
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
        JSONArray redisArray = new JSONArray();
        long studentIdLong = Long.parseLong(studentId);

        String key = studentId+"_QEActivities";

        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        for(int i = 0; i < qeaArray.size(); i++){
            activityId = qeaArray.getJSONObject(i).getLong("activityId");
            activityName = qeaArray.getJSONObject(i).getString("activityName");
            activitySchoolYearTerm = qeaArray.getJSONObject(i).getString("schoolYearTerm");
            activityTime = (Date) qeaArray.getJSONObject(i).getSqlDate("activityTime");
            activityScore = qeaArray.getJSONObject(i).getFloat("activityScore");
            activitySensorStatus = qeaArray.getJSONObject(i).getString("activitySensorStatus");
            activitySensorTime = (Date) qeaArray.getJSONObject(i).getSqlDate("activitySensorTime");
            Map<Object, Object> insertMap = new HashMap<>();
            insertMap.put("activityId", activityId);
            insertMap.put("activityName", activityName);
            insertMap.put("activitySchoolYearTerm", activitySchoolYearTerm);
            insertMap.put("activityTime", activityTime);
            insertMap.put("activityScore", activityScore);
            insertMap.put("activitySensorStatus", activitySensorStatus);
            insertMap.put("activitySensorTime", activitySensorTime);
            insertMap.put("studentId", studentIdLong);
            //将插入的数据存入jsonArray,方便存入redis
            redisArray.add(insertMap);

            insertResult += qualityExpansionActivitiesMapper.addQEActivities(insertMap);

        }
//        logger.debug("插入的数据为 [{}]", redisArray);
        String qeaStr = redisArray.toJSONString();//转换成字符串,方便存进redis
        //将素拓活动存进redis
        operations.set(key, qeaStr);
        logger.info("用户: [{}] 已新增 [{}] 条素拓活动信息", studentId, insertResult);
        return insertResult;
    }

    @Override
    public JSONObject updateQualityExpansionActivity(JSONArray qeaArray, String studentId) {
        JSONObject responseObject = new JSONObject();
        int updateResult = 0;
        String activitySensorStatus;
        Date activitySensorTime;
        float activityScore;
        long activityId;
        JSONArray redisArray = new JSONArray();

        String key = studentId+"_Course";
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        if(!qeaArray.isEmpty() && studentId!=null && !studentId.equals("")){
            Long studentIdLong = Long.parseLong(studentId);
            for(int i=0; i < qeaArray.size(); i++){
                activityId = qeaArray.getJSONObject(i).getLong("activityId");
                activityScore = qeaArray.getJSONObject(i).getFloat("activityScore");
                activitySensorStatus = qeaArray.getJSONObject(i).getString("activitySensorStatus");
                activitySensorTime = (Date) qeaArray.getJSONObject(i).getSqlDate("activitySensorTime");
                Map<Object, Object> updateMap = new HashMap<>();
                updateMap.put("activityId", activityId);
                updateMap.put("activityScore", activityScore);
                updateMap.put("activitySensorStatus", activitySensorStatus);
                updateMap.put("activitySensorTime", activitySensorTime);
                updateMap.put("studentId", studentIdLong);
                updateResult += qualityExpansionActivitiesMapper.updateQEActivity(updateMap);
                redisArray.add(updateMap);
            }
            //将数据存入Redis
            operations.set(key, redisArray.toJSONString());
            logger.debug("用户: [{}] 更新数据为: [{}], 共[{}]条数据", studentId, redisArray, updateResult);
            responseObject.put("msg","更新成功");
            responseObject.put("code",200);
            logger.info("用户: [{}] 已更新 [{}] 条课程信息", studentId, updateResult);
        }else{
            responseObject.put("msg","即将更新的素拓分列表为空!");
            responseObject.put("code", 403);
        }
        return responseObject;
    }

    @Override
    public JSONObject deleteQualityExpansionActivity(List<Long> studentIdList) {
        //删除结果计数
        int deleteResult = 0;
        JSONObject responseObject = new JSONObject();

        if(!studentIdList.isEmpty()){
            for(int i=0; i<studentIdList.size(); i++){
                redisTemplate.delete(studentIdList.get(i)+"_Course");
            }
            deleteResult = qualityExpansionActivitiesMapper.deleteQEActivitiesByStudentId(studentIdList);
            responseObject.put("result", deleteResult);
            responseObject.put("code", true);
            logger.info("已批量删除: [{}] 的素拓分记录, 共删除: [{}] 条", studentIdList, deleteResult);
        }else {
            responseObject.put("msg", "学号列表不能为空");
            responseObject.put("code", false);
            logger.warn("学号List不能为空");
        }
        return responseObject;
    }
}
