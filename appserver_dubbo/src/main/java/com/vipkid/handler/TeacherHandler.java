package com.vipkid.handler;

import com.google.common.collect.Lists;
import com.vipkid.model.Teacher;
import com.vipkid.service.pojo.TeacherInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by zfl on 2015/6/17.
 */
public class TeacherHandler {
    private TeacherHandler(){

    }
    public static List<TeacherInfoVO> convertVOList(List<Teacher> teacherList) {
        List<TeacherInfoVO> teacherInfoVOs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(teacherList)){
            TeacherInfoVO teacherInfoVO;
            for (Teacher teacher:teacherList) {
                teacherInfoVO = convertVO(teacher);
                teacherInfoVOs.add(teacherInfoVO);
            }
        }
        return teacherInfoVOs;
    }
    public static TeacherInfoVO convertVO(Teacher teacher) {
        TeacherInfoVO teacherInfoVO = null;
        if (null != teacher) {
            teacherInfoVO = new TeacherInfoVO();
            BeanUtils.copyProperties(teacher, teacherInfoVO, "certificatedCourses");
            teacherInfoVO.setId(teacher.getId());
            teacherInfoVO.setAvatar(teacher.getAvatar());
            teacherInfoVO.setStatus(teacher.getStatus());
            teacherInfoVO.setName(teacher.getName());
            teacherInfoVO.setGraduatedFrom(teacher.getGraduatedFrom());
            teacherInfoVO.setBankAccountName(teacher.getBankAccountName());
            teacherInfoVO.setBankCardNumber(teacher.getBankCardNumber());
            teacherInfoVO.setBankAddress(teacher.getBankAddress());
            teacherInfoVO.setBankName(teacher.getBankName());
            teacherInfoVO.setBankSWIFTCode(teacher.getBankSWIFTCode());
            teacherInfoVO.setPayPalAccount(teacher.getPayPalAccount());
            teacherInfoVO.setCertificatedCourses(CourseHandler.convert2VOList(teacher.getCertificatedCourses()));
        }
        return teacherInfoVO;
    }
    public static Teacher convertModel(TeacherInfoVO teacherInfoVO) {
        Teacher teacher = null;
        if (null != teacherInfoVO) {
            teacher = new Teacher();
            BeanUtils.copyProperties(teacherInfoVO, teacher, "certificatedCourses");
            teacher.setId(teacherInfoVO.getId());
            teacher.setStatus(teacherInfoVO.getStatus());
            teacher.setName(teacherInfoVO.getName());
        }
        return teacher;
    }
}
