package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Agent;

public class AgentAdapter extends XmlAdapter<Agent, Agent> {

	@Override
	public Agent unmarshal(Agent agent) throws Exception {
		return agent;
	}

	@Override
	public Agent marshal(Agent agent) throws Exception {
		if(agent == null) {
			return null;
		}else {
			Agent simplifiedAgent = new Agent();
			simplifiedAgent.setId(agent.getId());
			simplifiedAgent.setName(agent.getName());
			return simplifiedAgent;
		}	
	}

}
