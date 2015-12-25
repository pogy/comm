package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.EducationalComment;

public class EducationalCommentAdapter extends XmlAdapter<EducationalComment, EducationalComment> {

	@Override
	public EducationalComment unmarshal(EducationalComment educationalComment) throws Exception {
		return educationalComment;
	}

	@Override
	public EducationalComment marshal(EducationalComment educationalComment) throws Exception {
		if(educationalComment == null) {
			return null;
		}else {
			EducationalComment simplifiedEducationalComment = new EducationalComment();
			simplifiedEducationalComment.setId(educationalComment.getId());
			simplifiedEducationalComment.setEmpty(educationalComment.isEmpty());
			return simplifiedEducationalComment;
		}	
	}

}
