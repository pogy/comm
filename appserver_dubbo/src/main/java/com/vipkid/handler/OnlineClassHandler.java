package com.vipkid.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Student;
import com.vipkid.model.StudentComment;
import com.vipkid.model.TeacherComment;
import com.vipkid.rest.vo.query.CommentVO;
import com.vipkid.rest.vo.query.CourseView;
import com.vipkid.rest.vo.query.DemoReportVO;
import com.vipkid.rest.vo.query.LearningCycleVO;
import com.vipkid.rest.vo.query.LessonVO;
import com.vipkid.rest.vo.query.OnlineClassVO;
import com.vipkid.rest.vo.query.StudentQueryStaffView;
import com.vipkid.rest.vo.query.TeacherQueryCourseView;
import com.vipkid.rest.vo.query.UnitVO;
import com.vipkid.rest.vo.query.UserVO;
import com.vipkid.util.TextUtils;

/**
 * Created by zfl on 2015/6/12.
 */
public class OnlineClassHandler {
    private OnlineClassHandler(){

    };
    public static OnlineClassVO conver2VO(OnlineClass onlineClass) {
        if (null == onlineClass) {
            return null;
        }
        OnlineClassVO onlineClassVO = new OnlineClassVO();
        onlineClassVO.setId(onlineClass.getId());
        onlineClassVO.setOnlineClassName(onlineClass.getOnlineClassName());
        onlineClassVO.setScheduledDateTime(onlineClass.getScheduledDateTime());
        onlineClassVO.setFinishType(onlineClass.getFinishType());
        onlineClassVO.setStatus(onlineClass.getStatus());
        if (null != onlineClass.getLesson()) {
            LessonVO lessonVO = new LessonVO();
            lessonVO.setId(onlineClass.getLesson().getId());
            lessonVO.setSerialNumber(onlineClass.getLesson().getSerialNumber());
            onlineClassVO.setLesson(lessonVO);
        }
        if (null != onlineClass.getTeacher()) {
            TeacherQueryCourseView teacherQueryCourseView = new TeacherQueryCourseView();
            teacherQueryCourseView.setId(onlineClass.getTeacher().getId());
            teacherQueryCourseView.setRealName(onlineClass.getTeacher().getRealName());
            teacherQueryCourseView.setName(onlineClass.getTeacher().getName());
            onlineClassVO.setTeacher(teacherQueryCourseView);
        }
        if (CollectionUtils.isNotEmpty(onlineClass.getStudents())) {
            StudentQueryStaffView studentQueryStaffView = new StudentQueryStaffView();
            studentQueryStaffView.setId(onlineClass.getStudents().get(0).getId());
            studentQueryStaffView.setName(onlineClass.getStudents().get(0).getName());
            studentQueryStaffView.setEnglishName(onlineClass.getStudents().get(0).getEnglishName());
            if (null != onlineClass.getStudents().get(0).getSales()) {
                UserVO sales = new UserVO();
                sales.setId(onlineClass.getStudents().get(0).getSales().getId());
                sales.setName(onlineClass.getStudents().get(0).getSales().getName());
                sales.setSafeName(onlineClass.getStudents().get(0).getSales().getSafeName());
                studentQueryStaffView.setSales(sales);
            }
            onlineClassVO.setStudents(new StudentQueryStaffView[]{studentQueryStaffView});
        }
        if (null != onlineClass.getDemoReport()) {
            DemoReportVO demoReportVO = new DemoReportVO();
            demoReportVO.setId(onlineClass.getDemoReport().getId());
            if (null != onlineClass.getDemoReport().getLifeCycle()) {
                demoReportVO.setLifeCycle(onlineClass.getDemoReport().getLifeCycle().name());
            }
            onlineClassVO.setDemoReport(demoReportVO);
        }
        return onlineClassVO;
    }

    public static List<OnlineClassVO> convert2VOList(List<OnlineClass> onlineClassList) {
        List<OnlineClassVO> onlineClassVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(onlineClassList)) {
            OnlineClassVO onlineClassVO;
            for (OnlineClass onlineClass : onlineClassList) {
                onlineClassVO = OnlineClassHandler.conver2VO(onlineClass);
                onlineClassVOList.add(onlineClassVO);
            }
        }
        return onlineClassVOList;
    }
    
    
    public static List<OnlineClassVO> convert2VOForCommentsList(List<OnlineClass> onlineClassList) {
        List<OnlineClassVO> onlineClassVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(onlineClassList)) {
            OnlineClassVO onlineClassVO;
            LessonVO lessonVO;
            TeacherQueryCourseView teacherQueryCourseView;
            for (OnlineClass onlineClass : onlineClassList) {
                onlineClassVO = new OnlineClassVO();
                onlineClassVO.setId(onlineClass.getId());
                onlineClassVO.setOnlineClassName(onlineClass.getOnlineClassName());
                onlineClassVO.setScheduledDateTime(onlineClass.getScheduledDateTime());
                onlineClassVO.setFinishType(onlineClass.getFinishType());
                onlineClassVO.setComments(onlineClass.getComments());
                if (null != onlineClass.getLesson()) {
                    lessonVO = new LessonVO();
                    lessonVO.setId(onlineClass.getLesson().getId());
                    lessonVO.setSerialNumber(onlineClass.getLesson().getSerialNumber());
                    
                    LearningCycle learningCycle = onlineClass.getLesson().getLearningCycle();
                    //learningCycle
                    if (learningCycle != null) {
                    	LearningCycleVO learningCycleVO = new LearningCycleVO();
                    	learningCycleVO.setId(learningCycle.getId());
                    	//Unit
                    	if (learningCycle.getUnit() != null) {
                    		UnitVO unitVO = new UnitVO();
                    		unitVO.setId(learningCycle.getUnit().getId());
                    		//Course
                    		if (learningCycle.getUnit().getCourse() != null) {
                    			CourseView courseVo = new CourseView();
                    			courseVo.setId(learningCycle.getUnit().getCourse().getId());
                    			courseVo.setName(learningCycle.getUnit().getCourse().getName());
                    			unitVO.setCourse(courseVo);
                    		}
                    		learningCycleVO.setUnit(unitVO);
                    	}
                    	
                    	lessonVO.setLearningCycle(learningCycleVO);
                    }
                    onlineClassVO.setLesson(lessonVO);
                }
                if (null != onlineClass.getTeacher()) {
                    teacherQueryCourseView = new TeacherQueryCourseView();
                    teacherQueryCourseView.setId(onlineClass.getTeacher().getId());
                    teacherQueryCourseView.setRealName(onlineClass.getTeacher().getRealName());
                    teacherQueryCourseView.setName(onlineClass.getTeacher().getName());
                    onlineClassVO.setTeacher(teacherQueryCourseView);
                }
                if (CollectionUtils.isNotEmpty(onlineClass.getStudents())) {
                    List<StudentQueryStaffView> stuVoList = Lists.newArrayList();
                	for(Student stu : onlineClass.getStudents()) {
                		StudentQueryStaffView stuVo = new StudentQueryStaffView();
                		stuVo.setId(stu.getId());
                		stuVo.setName(stu.getName());
                		stuVo.setEnglishName(stu.getEnglishName());
                		stuVo.setCltName(stu.getChineseLeadTeacher() == null?"":stu.getChineseLeadTeacher().getName());
                		stuVoList.add(stuVo);
                	}
                    onlineClassVO.setStudents(stuVoList.toArray(new StudentQueryStaffView[stuVoList.size()]));

                }
                
                if (onlineClass.getFiremanToTeacherComment() != null) {
                	CommentVO commentVO = new CommentVO();
            		commentVO.setId(onlineClass.getFiremanToTeacherComment().getId());
            		commentVO.setEmpty(onlineClass.getFiremanToTeacherComment().isEmpty());
                	onlineClassVO.setFiremanToTeacherComment(commentVO);
                }
                
                if (CollectionUtils.isNotEmpty(onlineClass.getFiremanToStudentComments())) {
                	List<CommentVO> commentVOList = Lists.newArrayList();
                	for (FiremanToStudentComment comment : onlineClass.getFiremanToStudentComments()) {
                		CommentVO commentVO = new CommentVO();
                		commentVO.setId(comment.getId());
                		commentVO.setEmpty(comment.isEmpty());
                		if (comment.getStudent() != null) {
                			UserVO stu = new UserVO();
                			stu.setId(comment.getStudent().getId());
                			commentVO.setStudent(stu);
                		}
                		commentVOList.add(commentVO);
                	}
                	onlineClassVO.setFiremanToStudentComments(commentVOList.toArray(new CommentVO[commentVOList.size()]));
                }
                
                if (CollectionUtils.isNotEmpty(onlineClass.getTeacherComments())) {
                	List<CommentVO> commentVOList = Lists.newArrayList();
                	for (TeacherComment comment : onlineClass.getTeacherComments()) {
                		CommentVO commentVO = new CommentVO();
                		commentVO.setId(comment.getId());
                		if (comment.getAbilityToFollowInstructions() > 0 || comment.getActivelyInteraction() > 0 || comment.getClearPronunciation() > 0 || comment.getPerformance() > 0 
                				|| comment.getReadingSkills() > 0 || comment.getRepetition() > 0 || comment.getSpellingAccuracy() > 0 || comment.getStars() > 0 || comment.getCurrentPerforance() != null 
                				|| StringUtils.isNotBlank(comment.getReportIssues()) || StringUtils.isNotBlank(comment.getTeacherFeedback()) || StringUtils.isNotBlank(comment.getTipsForOtherTeachers())) {
                			commentVO.setEmpty(false);
                		} else {
                			commentVO.setEmpty(comment.isEmpty());
                		}
                		if (comment.getStudent() != null) {
                			UserVO stu = new UserVO();
                			stu.setId(comment.getStudent().getId());
                			commentVO.setStudent(stu);
                		}
                		commentVOList.add(commentVO);
                	}
                	onlineClassVO.setTeacherComments(commentVOList.toArray(new CommentVO[commentVOList.size()]));
                }
                if(CollectionUtils.isNotEmpty(onlineClass.getStudentComments())){
                	List<StudentComment> studentList = new ArrayList<StudentComment>();
                	for(StudentComment comment : onlineClass.getStudentComments()){
                		StudentComment aComment = new StudentComment();
                		aComment.setId(comment.getId());
                		aComment.setScores(comment.getScores());
                		aComment.setComment(comment.getComment());
                		studentList.add(aComment);
                	}
                	onlineClassVO.setStudentComments(studentList);
                }
                onlineClassVOList.add(onlineClassVO);
            }
        }
        return onlineClassVOList;
    }
}
