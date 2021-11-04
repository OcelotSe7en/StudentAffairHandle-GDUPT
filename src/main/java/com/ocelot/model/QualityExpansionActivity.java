package com.ocelot.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

//素拓的Model, 暂未找到素拓分的信达雅翻译, 只能直球翻译
public class QualityExpansionActivity {
    //素拓活动名称
    @JSONField(alternateNames = "ktmc")
    String activityName;
    //素拓活动的学年学期时间
    @JSONField(alternateNames = "xnxqdm")
    String schoolYearTerm;
    //素拓活动开展时间
    @JSONField(alternateNames = "kzsj")
    String activityTime;
    //素拓分
    @JSONField(alternateNames = "hdxf")
    float activityScore;
    //素拓分认定状态
    @JSONField(alternateNames = "xsrdzt")
    String activitySensorStatus;
    //素拓审核时间
    @JSONField(alternateNames = "shrq")
    String activitySensorTime;

    //学号
    long studentId;

    @Override
    public String toString() {
        return "QualityExpansionActivity{" +
                "activityName='" + activityName + '\'' +
                ", schoolYear='" + schoolYearTerm + '\'' +
                ", activityTime='" + activityTime + '\'' +
                ", activityScore='" + activityScore + '\'' +
                ", activitySensorStatus='" + activitySensorStatus + '\'' +
                ", activitySensorTime='" + activitySensorTime + '\'' +
                '}';
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getSchoolYearTerm() {
        return schoolYearTerm;
    }

    public void setSchoolYearTerm(String schoolYearTerm) {
        this.schoolYearTerm = schoolYearTerm;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public float getActivityScore() {
        return activityScore;
    }

    public void setActivityScore(float activityScore) {
        this.activityScore = activityScore;
    }

    public String getActivitySensorStatus() {
        return activitySensorStatus;
    }

    public void setActivitySensorStatus(String activitySensorStatus) {
        this.activitySensorStatus = activitySensorStatus;
    }

    public String getActivitySensorTime() {
        return activitySensorTime;
    }

    public void setActivitySensorTime(String activitySensorTime) {
        this.activitySensorTime = activitySensorTime;
    }
}
