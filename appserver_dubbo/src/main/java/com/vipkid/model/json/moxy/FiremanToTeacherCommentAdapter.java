package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.FiremanToTeacherComment;

public class FiremanToTeacherCommentAdapter extends XmlAdapter<FiremanToTeacherComment, FiremanToTeacherComment> {

	@Override
	public FiremanToTeacherComment unmarshal(FiremanToTeacherComment firemanToTeacherComment) throws Exception {
		return firemanToTeacherComment;
	}

	@Override
	public FiremanToTeacherComment marshal(FiremanToTeacherComment firemanToTeacherComment) throws Exception {
		if(firemanToTeacherComment == null) {
			return null;
		}else {
			FiremanToTeacherComment simplifiedFiremanToTeacherComment = new FiremanToTeacherComment();
			simplifiedFiremanToTeacherComment.setId(firemanToTeacherComment.getId());
			simplifiedFiremanToTeacherComment.setEmpty(firemanToTeacherComment.isEmpty());
			simplifiedFiremanToTeacherComment.setTeacherITProblem(firemanToTeacherComment.getTeacherITProblem());
			simplifiedFiremanToTeacherComment.setTeacherBehaviorProblem(firemanToTeacherComment.getTeacherBehaviorProblem());
			simplifiedFiremanToTeacherComment.setTeacher(firemanToTeacherComment.getTeacher());
			return simplifiedFiremanToTeacherComment;
		}	
	}

}
