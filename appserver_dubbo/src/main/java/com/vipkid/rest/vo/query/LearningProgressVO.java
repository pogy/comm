package com.vipkid.rest.vo.query;

import com.vipkid.model.LearningProgress;

import java.util.List;

/**
 * Created by zfl on 2015/6/19.
 */
public class LearningProgressVO {
    private Long id;
    private LearningProgress.Status status;
    private OnlineClassVO firstCompletedOnlineClass;
    private OnlineClassVO lastCompletedOnlineClass;
    private List<OnlineClassVO> completedOnlineClasses;
    private LessonVO nextShouldTakeLesson;
    private int leftClassHour;// 当前剩余课时
    private int totalClassHour;// 总课时
    private CourseView course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LearningProgress.Status getStatus() {
        return status;
    }

    public void setStatus(LearningProgress.Status status) {
        this.status = status;
    }

    public OnlineClassVO getFirstCompletedOnlineClass() {
        return firstCompletedOnlineClass;
    }

    public void setFirstCompletedOnlineClass(OnlineClassVO firstCompletedOnlineClass) {
        this.firstCompletedOnlineClass = firstCompletedOnlineClass;
    }

    public OnlineClassVO getLastCompletedOnlineClass() {
        return lastCompletedOnlineClass;
    }

    public void setLastCompletedOnlineClass(OnlineClassVO lastCompletedOnlineClass) {
        this.lastCompletedOnlineClass = lastCompletedOnlineClass;
    }

    public List<OnlineClassVO> getCompletedOnlineClasses() {
        return completedOnlineClasses;
    }

    public void setCompletedOnlineClasses(List<OnlineClassVO> completedOnlineClasses) {
        this.completedOnlineClasses = completedOnlineClasses;
    }

    public LessonVO getNextShouldTakeLesson() {
        return nextShouldTakeLesson;
    }

    public void setNextShouldTakeLesson(LessonVO nextShouldTakeLesson) {
        this.nextShouldTakeLesson = nextShouldTakeLesson;
    }

    public int getLeftClassHour() {
        return leftClassHour;
    }

    public void setLeftClassHour(int leftClassHour) {
        this.leftClassHour = leftClassHour;
    }

    public int getTotalClassHour() {
        return totalClassHour;
    }

    public void setTotalClassHour(int totalClassHour) {
        this.totalClassHour = totalClassHour;
    }

    public CourseView getCourse() {
        return course;
    }

    public void setCourse(CourseView course) {
        this.course = course;
    }
}
