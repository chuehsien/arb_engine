package com.dapp.opti.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AnchorToken extends ITokenOrStart {

	public AnchorToken() {
		super();
	}
	public AnchorToken(String name) {
		super();
		this.service = null;
		this.name = name;	
	}
	
	public ITokenOrStart getPreviousTokenOrStart() {
		return null;
	}

	@JsonIgnore
	@Override
	public AnchorToken getAnchorToken() {
		return this;
	}

	@Override
	public String toString() {
		return String.format("Anchor-%s", this.name);
	}
	@Override
	public String getShortDesc() {
		return toString();
	}
	

	@Override
	public void setPreviousTokenOrStart(ITokenOrStart t) {
		// TODO Auto-generated method stub
		
	}
	
	
}
