package com.dapp.opti.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import com.dapp.opti.domain.solver.PreviousVariableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.javaparser.utils.Log;

@JsonInclude(Include.NON_NULL)
@PlanningEntity
public class Token extends ITokenOrStart {

//	planning variables
	@JsonIgnore
	private ITokenOrStart previousTokenOrStart;
	
	@JsonIgnore
	protected AnchorToken anchorToken;
	private Float ratioFromPrevious;

	public Token() {
		super();
	}
	public Token (String serviceName) {
		super();
		this.service = serviceName.split("_")[0];
		this.name = serviceName.split("_")[1];
		this.ratioFromPrevious = 1.0f;
	}
	public Token(String service, String name) {
		super();
		this.service = service;
		this.name = name;
		this.ratioFromPrevious = 1.0f;
	}
	
	
	@PlanningVariable(valueRangeProviderRefs = { "tokenRange", "anchorTokenRange",
			"ghostTokenRange" }, graphType = PlanningVariableGraphType.CHAINED)
	public ITokenOrStart getPreviousTokenOrStart() {
		return previousTokenOrStart;
	}

	public void setPreviousTokenOrStart(ITokenOrStart previousTokenOrStart) {
		this.previousTokenOrStart = previousTokenOrStart;
	}

	@AnchorShadowVariable(sourceVariableName = "previousTokenOrStart")
	public AnchorToken getAnchorToken() {
		return this.anchorToken;
	}

	@CustomShadowVariable(variableListenerClass = PreviousVariableListener.class, sources = {
			@PlanningVariableReference(variableName = "previousTokenOrStart")})
	public Float getRatioFromPrevious() {
		return ratioFromPrevious;
	}

	public void setRatioFromPrevious(Float ratioFromPrevious) {
		this.ratioFromPrevious = ratioFromPrevious;
	}

	public void setAnchorToken(AnchorToken anchorToken) {
		this.anchorToken = anchorToken;
	}

	

	@Override
	public String toString() {
		ITokenOrStart p = this.getPreviousTokenOrStart();
		if (p == null) {
			return String.format("%s_%s (prev: null)(%s)", this.service, this.name, this.ratioFromPrevious);
		} else {
			if (p instanceof Token) {
				return String.format("%s_%s (prev: %s, anchor: %s)(%s)", this.service, this.name,
						((Token) p).getShortDesc(),
						this.getAnchorToken() == null ? "null" : this.getAnchorToken().toString(), this.ratioFromPrevious);
			} else if (p instanceof GhostToken) {
				return String.format("%s_%s (prev: GHOST)(%s)", this.service, this.name, this.ratioFromPrevious);
			} else {
				return String.format("%s_%s (anchor: %s)(%s)", this.service, this.name,
						this.getAnchorToken() == null ? "null" : this.getAnchorToken().toString(), this.ratioFromPrevious);
			}
		}
	}

	public boolean isPrevGhost() {
		if (this.getPreviousTokenOrStart() != null && this.getPreviousTokenOrStart() instanceof GhostToken) {
			return true;
		}
		return false;
	}

	public boolean isPrevTokenSameServiceOrSameName() {
		if (isPrevGhost()) {
			return true;
		}
		else if (this.getPreviousTokenOrStart() instanceof AnchorToken) {
			return (this.getPreviousTokenOrStart().getName().equals(this.getName()));
		}
		else if (this.getPreviousTokenOrStart() != null) {
			ITokenOrStart s = this.getPreviousTokenOrStart();
//			System.out.println("Comparing: " + s.getService() + " : " + this.getService());
//			System.out.println("Comparing: " + s.getName() + " : " + this.getName());
			if (s.getService().equals(this.getService())) {
				return true;
			} else if (s.getName().equals(this.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			Log.error("Something wrong");
			return true;
		}
	}

	public boolean notPrevTokenSameServiceOrSameName() {
		return !isPrevTokenSameServiceOrSameName();
	}

	public boolean prevIsNonGhost() {
		return !isPrevGhost();
	}
	public boolean hasPreviousToken() {
		return this.getPreviousTokenOrStart() != null;
	}
	
	public String getPreviousTokenOrStartDesc() {
		if (this.previousTokenOrStart != null) {
			return this.previousTokenOrStart.getShortDesc();
		} else {
			return "null";
		}
	}
	public String getNextTokenDesc() {
		if (this.getNextToken() != null) {
			return this.getNextToken().getShortDesc();
		} else {
			return "-";
		}
	}
}
