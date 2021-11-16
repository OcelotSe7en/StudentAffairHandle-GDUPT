package com.ocelot.service;

import com.alibaba.fastjson.JSONArray;
import com.ocelot.model.QualityExpansionActivity;

import java.util.List;
import java.util.Map;

public interface QualityExpansionActivitiesService {
    //查询素拓活动
    List<QualityExpansionActivity> selectQualityExpansionActivity(String studentId);
    //新增素拓活动
    int addQualityExpansionActivity(JSONArray qeaArray, String studentId);
    //更新素拓活动
    int updateQualityExpansionActivity(JSONArray qeaArray, String studentId);
    //删除素拓活动
    int deleteQualityExpansionActivityById(String studentId);
}
