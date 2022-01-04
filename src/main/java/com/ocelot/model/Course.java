package com.ocelot.model;

import com.alibaba.fastjson.annotation.JSONField;

public class Course {
    //课程名称(kcmc)
    @JSONField(alternateNames = "kcmc")
    public String courseName;
    //任课老师(teaxms)
    @JSONField(alternateNames = "teaxms")
    public String courseTeacher;
    //上课地点(jxcdmc)
    @JSONField(alternateNames = "jxcdmc")
    public String courseLocation;
    //星期x(xq)
    @JSONField(alternateNames = "xq")
    public int courseWeekDay;
    //课程节次(jcdm)
    @JSONField(alternateNames = "jcdm")
    public int courseClass;
    //周次(zc)
    @JSONField(alternateNames = "zc")
    public int courseWeek;
    //学年学期(xnxq)
    @JSONField(alternateNames = "xnxqdm")
    public int courseSchoolYearTerm;
    //学生id
    public long studentId;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public int getCourseWeekDay() {
        return courseWeekDay;
    }

    public void setCourseWeekDay(int courseWeekDay) {
        this.courseWeekDay = courseWeekDay;
    }

    public int getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(int courseClass) {
        this.courseClass = courseClass;
    }

    public int getCourseWeek() {
        return courseWeek;
    }

    public void setCourseWeek(int courseWeek) {
        this.courseWeek = courseWeek;
    }

    public int getCourseSchoolYearTerm() {
        return courseSchoolYearTerm;
    }

    public void setCourseSchoolYearTerm(int courseSchoolYearTerm) {
        this.courseSchoolYearTerm = courseSchoolYearTerm;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }
}
