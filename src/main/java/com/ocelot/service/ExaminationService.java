package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ocelot.model.Course;
import com.ocelot.model.Examination;

import java.util.List;

public interface ExaminationService {
    //查询成绩
    List<Examination> selectExamination(String studentId, String schoolYearAndTerm);

    //新增成绩
    JSONObject addExamination(JSONArray courseArray, String studentId);

    //更新成绩
    JSONObject updateExamination(JSONArray systemCourseArray, String studentId, String schoolYear);

    //删除成绩
    JSONObject deleteExamination(List<Long> studentIdList);
}
