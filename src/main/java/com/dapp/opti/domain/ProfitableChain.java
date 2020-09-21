package com.dapp.opti.domain;

import java.util.List;

public class ProfitableChain {
	List<String> chain;
	float score;
	
	
	public ProfitableChain(List<String> chain, float score) {
		super();
		this.chain = chain;
		this.score = score;
	}
	public List<String> getChain() {
		return chain;
	}
	public void setChain(List<String> chain) {
		this.chain = chain;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return String.format("%s : %s", chain.toString(), score);
	}
}
