package com.dapp.opti.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class PairInfo {
	String a;
	String b;
	Float rate;
	
	public PairInfo(String a, String b, Float rate) {
		super();
		this.a = a;
		this.b = b;
		this.rate = rate;
	}

	public Set<String> getNames(){
		Set<String> s = new HashSet<String>();
		s.add(this.a.split("_")[1]);
		s.add(this.b.split("_")[1]);
		return s;
	}
	public Set<String> getServices(){
		Set<String> s = new HashSet<String>();
		s.add(this.a.split("_")[0]);
		s.add(this.b.split("_")[0]);
		return s;
	}
	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}
	
	@Override
	public String toString() {
		return String.format("%s->%s:%s", a,b,rate);
	}
	
}
