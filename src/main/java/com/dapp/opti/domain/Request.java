package com.dapp.opti.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Request {
	
	List<PairInfo> pairs;
	List<String> anchorNames;
	

	public List<PairInfo> getPairs() {
		return pairs;
	}


	public void setPairs(List<PairInfo> pairs) {
		this.pairs = pairs;
	}


	public List<String> getAnchorNames() {
		return anchorNames;
	}


	public void setAnchorNames(List<String> anchorNames) {
		this.anchorNames = anchorNames;
	}

	@Override
	public String toString() {
		return String.format("Pairs: \n%s\nAnchors: %s\n", pairs, anchorNames);
	}

	
}
