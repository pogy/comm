package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.FiremanToStudentComment;

public class FiremanToStudentCommentAdapter extends XmlAdapter<FiremanToStudentComment, FiremanToStudentComment> {

	@Override
	public FiremanToStudentComment unmarshal(FiremanToStudentComment firemanToStudentComment) throws Exception {
		return firemanToStudentComment;
	}

	@Override
	public FiremanToStudentComment marshal(FiremanToStudentComment firemanToStudentComment) throws Exception {
		if(firemanToStudentComment == null) {
			return null;
		}else {
			FiremanToStudentComment simplifiedFiremanToStudentComment = new FiremanToStudentComment();
			simplifiedFiremanToStudentComment.setId(firemanToStudentComment.getId());
			simplifiedFiremanToStudentComment.setEmpty(firemanToStudentComment.isEmpty());
			simplifiedFiremanToStudentComment.setStudentITProblem(firemanToStudentComment.getStudentITProblem());
			simplifiedFiremanToStudentComment.setStudentBehaviorProblem(firemanToStudentComment.getStudentBehaviorProblem());
			simplifiedFiremanToStudentComment.setSupplement(firemanToStudentComment.getSupplement());
			simplifiedFiremanToStudentComment.setStudent(firemanToStudentComment.getStudent());
			
			return simplifiedFiremanToStudentComment;
		}	
	}

}
