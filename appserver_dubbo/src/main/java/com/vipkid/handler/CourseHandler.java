package com.vipkid.handler;

import com.google.common.collect.Lists;
import com.vipkid.model.Course;
import com.vipkid.rest.vo.query.CourseView;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2015/6/19.
 */
public class CourseHandler {
    private CourseHandler(){

    }
    public static CourseView convert2VO(Course course) {
        if (null == course) {
            return null;
        }
        CourseView view = new CourseView();
        view.setId(course.getId());
        view.setMode(course.getMode());
        view.setName(course.getName());
        view.setSequential(course.isSequential());
        view.setType(course.getType());
        return view;
    }
    public static List<CourseView> convert2VOList(List<Course> courseList) {
        List<CourseView> courseViewList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(courseList)) {
            courseViewList.addAll(courseList.stream().map(CourseHandler::convert2VO).collect(Collectors.toList()));
        }
        return courseViewList;
    }
}
