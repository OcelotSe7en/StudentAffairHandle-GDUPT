package com.ocelot.model;

import com.alibaba.fastjson.annotation.JSONField;

//考试类(成绩)
public class Examination {
    //学年学期(xnxqmc)
    @JSONField(alternateNames = "xnxqmc")
    public String examYearAndTerm;
    //课程编号(kcbh)
    @JSONField(alternateNames = "kcbh")
    public int examCourseNumber;
    //课程名称(kcmc)
    @JSONField(alternateNames = "kcmc")
    public String examCourseName;
    //考试成绩(zcj)
    @JSONField(alternateNames = "zcj")
    public int examResult;
    //考试绩点(cjjd)
    @JSONField(alternateNames = "cjjd")
    public float examPoint;
    //学分(xf)
    @JSONField(alternateNames = "xf")
    public float examCredit;
    //考试成绩编号(cjdm)
    @JSONField(alternateNames = "cjdm")
    public int examResultNumber;
    //考试性质(ksxzmc)
    @JSONField(alternateNames = "ksxzmc")
    public String examType;
    //学号(xsbh)
    @JSONField(alternateNames = "xsbh")
    public long studentId;

    public String getExamYearAndTerm() {
        return examYearAndTerm;
    }

    public void setExamYearAndTerm(String examYearAndTerm) {
        this.examYearAndTerm = examYearAndTerm;
    }

    public int getExamCourseNumber() {
        return examCourseNumber;
    }

    public void setExamCourseNumber(int examCourseNumber) {
        this.examCourseNumber = examCourseNumber;
    }

    public String getExamCourseName() {
        return examCourseName;
    }

    public void setExamCourseName(String examCourseName) {
        this.examCourseName = examCourseName;
    }

    public int getExamResult() {
        return examResult;
    }

    public void setExamResult(int examResult) {
        this.examResult = examResult;
    }

    public float getExamPoint() {
        return examPoint;
    }

    public void setExamPoint(float examPoint) {
        this.examPoint = examPoint;
    }

    public float getExamCredit() {
        return examCredit;
    }

    public void setExamCredit(float examCredit) {
        this.examCredit = examCredit;
    }

    public int getExamResultNumber() {
        return examResultNumber;
    }

    public void setExamResultNumber(int examResultNumber) {
        this.examResultNumber = examResultNumber;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }
}
