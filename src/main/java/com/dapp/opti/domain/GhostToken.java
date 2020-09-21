package com.dapp.opti.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
//@PlanningEntity(difficultyComparatorClass = SwabTeamDayDifficultyComparator.class)
public class GhostToken extends AnchorToken {
	

	public GhostToken() {
		super();
		this.service = null;
		this.name = null;
		this.ghost = true;
	}
	public ITokenOrStart getPreviousTokenOrStart() {
		return null;
	}

	@Override
	public AnchorToken getAnchorToken() {
		return null;
	}

	@Override
	public String toString() {
		return "GHOST";
	}
	@Override
	public void setPreviousTokenOrStart(ITokenOrStart t) {
		// TODO Auto-generated method stub
		
	}
}
