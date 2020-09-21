package com.dapp.opti.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import com.dapp.opti.Utils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity
public abstract class ITokenOrStart extends EntityWithId{
	
	String name;
	String service;
	boolean ghost;
	
	@JsonBackReference
	@InverseRelationShadowVariable(sourceVariableName = "previousTokenOrStart")
	protected Token nextToken;
	
	public ITokenOrStart() {
		super();
		this.setId(Utils.getRandomId());
	}

	@Override
	public String toString() {
		return String.format("%s_%s",name, service); 
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public Token getNextToken() {
		return nextToken;
	}

	public void setNextToken(Token nextToken) {
		this.nextToken = nextToken;
	}
	
	public abstract ITokenOrStart getPreviousTokenOrStart();
	public abstract void setPreviousTokenOrStart(ITokenOrStart t);
	
	public String getShortDesc() {
		return String.format("%s_%s", this.service, this.name);
	}

	public abstract AnchorToken getAnchorToken();
	public boolean hasNextToken() {
		return this.nextToken != null;
	}
	public boolean noNextToken() {
		return this.nextToken == null;
	}
	private boolean nullOrEquals(Object a, Object b) {
		if (a==null && b == null) {
			return true;
		} else if (a!=null && b!=null){
			
			return a.equals(b);
		} else {
			return false;
		}
	}
	public boolean equals(ITokenOrStart other) {
		
		
		
		if(!(nullOrEquals(this.service,other.getService()) && nullOrEquals(this.name,other.getName()))){
			return false;
		}
		if (other instanceof AnchorToken) {
			if (((AnchorToken)this).isGhost() != ((AnchorToken)other).isGhost()){
				return false;
			}
			return true;
		}
		if (other instanceof Token) {
			if (((Token)this).getRatioFromPrevious() != ((Token)other).getRatioFromPrevious()) {
				return false;
			}
			return true;
		}
		return true;
	}
}
