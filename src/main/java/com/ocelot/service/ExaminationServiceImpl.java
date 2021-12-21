package com.ocelot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.mapper.ExaminationMapper;
import com.ocelot.model.Course;
import com.ocelot.model.Examination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ExaminationService")
public class ExaminationServiceImpl implements ExaminationService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    @Autowired
    ExaminationMapper examinationMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<Examination> selectExamination(String studentId, String schoolYearAndTerm) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        List<Examination> examinationList;
        Map<Object,Object> selectMap = new HashMap<>();
        String key = studentId + "_Examination";

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            String examStr = operations.get(key);
            examinationList = JSON.parseArray(examStr, Examination.class);
        } else {
            selectMap.put("studentId", studentId);
            selectMap.put("schoolYear", schoolYearAndTerm);
            examinationList = examinationMapper.findExaminationByCondition(selectMap);
        }
        return examinationList;
    }

    @Override
    public JSONObject addExamination(JSONArray examinationArray, String studentId) {
        //新增结果计数
        int insertResult = 0;
        JSONObject returnObject = new JSONObject();

        String key = studentId + "_Examination";
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

//        判空
        if(!examinationArray.isEmpty()){
            //将成绩数组转换为成绩列表
            List<Examination> examinationList = examinationArray.toJavaList(Examination.class);
            //获得返回的统计结果
            insertResult =  examinationMapper.addMultiExamination(examinationList);
            operations.set(key, examinationArray.toJSONString());

            returnObject.put("msg", "成功插入数据 " + insertResult + "条");
            returnObject.put("code", true);
            logger.info("用户[{}]成功新增数据[{}]条", studentId, insertResult);
        }else{
            returnObject.put("msg", "插入失败! 成绩数组不得为空!");
            returnObject.put("code", false);
            logger.warn("插入失败! 成绩数组不得为空!");
        }
        return returnObject;
    }

    @Override
    public JSONObject updateExamination(JSONArray systemExaminationArray, String studentId, String schoolYear) {
        JSONObject returnObject = new JSONObject();
        int updateResult = 0;

        String key = studentId + "_Examination";
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();

        //        判空
        if(!systemExaminationArray.isEmpty()){
            //将成绩数组转换为成绩列表
            List<Examination> examinationList = systemExaminationArray.toJavaList(Examination.class);
            //获得返回的统计结果
            updateResult =  examinationMapper.updateMultiExamination(examinationList);
            operations.set(key, systemExaminationArray.toJSONString());

            returnObject.put("msg", "成功插入数据 " + updateResult + "条");
            returnObject.put("code", true);
            logger.debug("更新的数据为: [{}]", examinationList);
            logger.info("用户[{}]成功更新数据[{}]条", studentId, updateResult);
        }else{
            returnObject.put("msg", "插入失败! 成绩数组不得为空!");
            returnObject.put("code", false);
            logger.warn("插入失败! 成绩数组不得为空!");
        }

        return returnObject;
    }

    @Override
    public JSONObject deleteExamination(List<Long> studentIdList) {
        //删除结果计数
        int deleteResult = 0;
        JSONObject responseObject = new JSONObject();

        if (!studentIdList.isEmpty()) {
            for (int i = 0; i < studentIdList.size(); i++) {
                redisTemplate.delete(studentIdList.get(i) + "_Course");
            }
            deleteResult = examinationMapper.deleteMultiExamination(studentIdList);
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
}
