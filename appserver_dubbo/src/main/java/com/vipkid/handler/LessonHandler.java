package com.vipkid.handler;

import com.google.common.collect.Lists;
import com.vipkid.model.Lesson;
import com.vipkid.rest.vo.query.LessonVO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by zfl on 2015/6/13.
 */
public class LessonHandler {
    private LessonHandler(){

    }
    public static List<LessonVO> conver2VOList(List<Lesson> lessonList) {
        List<LessonVO> lessonVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(lessonList)) {
            LessonVO lessonVO;
            for (Lesson lesson : lessonList) {
                lessonVO = LessonHandler.conver2VO(lesson);
                lessonVOList.add(lessonVO);
            }
        }
        return lessonVOList;
    }
    public static LessonVO conver2VO(Lesson lesson) {
        if (null == lesson) {
            return null;
        }
        LessonVO lessonVO = new LessonVO();
        lessonVO.setId(lesson.getId());
        lessonVO.setSerialNumber(lesson.getSerialNumber());
        return lessonVO;
    }
}
