/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : minicampus

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 21/12/2021 21:10:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for coursetable
-- ----------------------------
DROP TABLE IF EXISTS `coursetable`;
CREATE TABLE `coursetable`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名',
  `course_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师',
  `course_location` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上课地点',
  `course_week_day` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '星期几',
  `course_class` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节次',
  `course_week` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第几周',
  `course_school_year_term` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年学期',
  `student_id` bigint NOT NULL COMMENT '学号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `studentId`(`student_id`) USING BTREE COMMENT '学号作为索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1319 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for examination
-- ----------------------------
DROP TABLE IF EXISTS `examination`;
CREATE TABLE `examination`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学号',
  `exam_year_and_term` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年学期(形如2021-2022-1)',
  `exam_course_number` int NOT NULL COMMENT '课程编号',
  `exam_course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `exam_result` int NOT NULL COMMENT '考试成绩',
  `exam_point` float NOT NULL COMMENT '绩点',
  `exam_credit` float NOT NULL COMMENT '学分',
  `exam_result_number` int NOT NULL COMMENT '成绩编号',
  `exam_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试性质',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quality_expansion_activities
-- ----------------------------
DROP TABLE IF EXISTS `quality_expansion_activities`;
CREATE TABLE `quality_expansion_activities`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `activity_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动名称',
  `activity_school_year_term` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年学期',
  `activity_time` date NULL DEFAULT NULL COMMENT '活动开展时间',
  `activity_score` float(3, 1) NOT NULL COMMENT '素拓分',
  `activity_sensor_status` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '素拓分认定状态(通过状态)',
  `activity_sensor_time` date NULL DEFAULT NULL COMMENT '审核时间',
  `student_id` bigint NOT NULL COMMENT '学号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
