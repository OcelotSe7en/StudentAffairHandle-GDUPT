package com.ocelot.mapper;

import com.ocelot.model.Examination;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ExaminationMapper {
    //查询成绩
    List<Examination> findExaminationByCondition(Map<Object,Object> map);
    //批量添加成绩
    int addMultiExamination(List<Examination> examinationList);
    //批量更新成绩
    int updateMultiExamination(List<Examination> examinationList);
    //批量删除成绩
    int deleteMultiExamination(List<Long> studentIdList);
}
