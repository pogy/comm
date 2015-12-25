package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Resource;

public class ResourceAdapter extends XmlAdapter<Resource, Resource> {

	@Override
	public Resource unmarshal(Resource resource) throws Exception {
		return resource;
	}

	@Override
	public Resource marshal(Resource resource) throws Exception {
		if(resource == null) {
			return null;
		}else {
			Resource simplifiedResource = new Resource();
			simplifiedResource.setId(resource.getId());
			simplifiedResource.setUrl(resource.getUrl());
			return simplifiedResource;
		}	
	}
}
