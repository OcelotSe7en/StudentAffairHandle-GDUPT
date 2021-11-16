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
    List<QualityExpansionActivity> selectQEActivitiesByStudentId(Map<Object,Object> map);
    //新增素拓活动
    int addQEActivities(Map<Object, Object> map);
    //更新素拓活动
    int updateQEActivity(Map<Object, Object> map);
    //删除指定用户的素拓活动
    int deleteQEActivitiesByStudentId(Long studentId);
}
