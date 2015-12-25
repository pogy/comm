package com.vipkid.handler;

import com.google.common.collect.Lists;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.OnlineClass;
import com.vipkid.rest.vo.query.CourseView;
import com.vipkid.rest.vo.query.LearningProgressVO;
import com.vipkid.rest.vo.query.LessonVO;
import com.vipkid.rest.vo.query.OnlineClassVO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2015/6/19.
 */
public class LearningProgressHandler {
    private LearningProgressHandler(){

    }
    public static List<LearningProgressVO> convertVOList(List<LearningProgress> learningProgressList) {
        List<LearningProgressVO> learningProgressVOs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(learningProgressList)) {
            learningProgressVOs.addAll(learningProgressList.stream().map(LearningProgressHandler::convertVO).collect(Collectors.toList()));
        }
        return learningProgressVOs;
    }
    public static LearningProgressVO convertVO(LearningProgress learningProgress) {
        if (null == learningProgress) {
            return null;
        }
        LearningProgressVO vo = new LearningProgressVO();
        vo.setId(learningProgress.getId());
        vo.setStatus(learningProgress.getStatus());
        vo.setLeftClassHour(learningProgress.getLeftClassHour());
        vo.setTotalClassHour(learningProgress.getTotalClassHour());
        OnlineClass firstCompletedOnlineClass  = learningProgress.getFirstCompletedOnlineClass();
        if (null != firstCompletedOnlineClass) {
            OnlineClassVO firstCompletedOnlineClassVO = OnlineClassHandler.conver2VO(firstCompletedOnlineClass);
            vo.setFirstCompletedOnlineClass(firstCompletedOnlineClassVO);
        }
        OnlineClass lastCompletedOnlineClass  = learningProgress.getLastCompletedOnlineClass();
        if (null != lastCompletedOnlineClass) {
            OnlineClassVO lastCompletedOnlineClassVO = OnlineClassHandler.conver2VO(lastCompletedOnlineClass);
            vo.setLastCompletedOnlineClass(lastCompletedOnlineClassVO);
        }
        List<OnlineClass> completedOnlineClasses  = learningProgress.getCompletedOnlineClasses();
        List<OnlineClassVO> completedOnlineClassVO = OnlineClassHandler.convert2VOList(completedOnlineClasses);
        vo.setCompletedOnlineClasses(completedOnlineClassVO);

        if (null != learningProgress.getNextShouldTakeLesson()){
            LessonVO lessonVO = LessonHandler.conver2VO(learningProgress.getNextShouldTakeLesson());
            vo.setNextShouldTakeLesson(lessonVO);
        }
        if (null != learningProgress.getCourse()) {
            CourseView courseView = CourseHandler.convert2VO(learningProgress.getCourse());
            vo.setCourse(courseView);
        }
        return vo;
    }
}
