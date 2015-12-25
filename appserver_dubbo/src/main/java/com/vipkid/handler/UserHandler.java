package com.vipkid.handler;

import com.google.common.collect.Lists;
import com.vipkid.model.Staff;
import com.vipkid.rest.vo.query.UserVO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by zfl on 2015/6/12.
 */
public class UserHandler {
    private UserHandler(){};
    public static List<UserVO> convert2UserVOList(List<Staff> staffList){
        List<UserVO> userVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(staffList)) {
            UserVO userVO;
            for (Staff staff:staffList) {
                userVO = new UserVO();
                userVO.setId(staff.getId());
                userVO.setName(staff.getName());
                userVO.setSafeName(staff.getSafeName());
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }
}
