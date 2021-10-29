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
    public String courseWeekDay;
    //课程节次(jcdm)
    @JSONField(alternateNames = "jcdm")
    public String courseClass;
    //周次(zc)
    @JSONField(alternateNames = "zc")
    public String courseWeek;
    //学年学期(xnxq)
    @JSONField(alternateNames = "xnxqdm")
    public String courseSchoolYearTerm;
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

    public String getCourseWeekDay() {
        return courseWeekDay;
    }

    public void setCourseWeekDay(String courseWeekDay) {
        this.courseWeekDay = courseWeekDay;
    }

    public String getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(String courseClass) {
        this.courseClass = courseClass;
    }

    public String getCourseWeek() {
        return courseWeek;
    }

    public void setCourseWeek(String courseWeek) {
        this.courseWeek = courseWeek;
    }

    public String getCourseSchoolYear() {
        return courseSchoolYearTerm;
    }

    public void setCourseSchoolYear(String courseSchoolYear) {
        this.courseSchoolYearTerm = courseSchoolYear;
    }
}
