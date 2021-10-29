package com.ocelot.mapper;

import com.ocelot.model.Course;
import com.ocelot.model.QualityExpansionActivity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface QualityExpansionActivitiesMapper {
    //QualityExpansion 缩写为QE
    //查询素拓活动
    List<QualityExpansionActivity> selectQEActivitiesByStudentId(long studentId);
    //新增课表
    int addQEActivities(Map<String, String> map);
    //删除指定用户的课表
    void deleteCourseTable(String studentId);
}
