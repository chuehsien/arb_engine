package com.dapp.opti.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.dapp.opti.domain.solver.ArbConstraintConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;


@PlanningSolution
public class ArbSolution {

	private long id;

	private RatioProvider ratioProvider;

	@ConstraintConfigurationProvider
	private ArbConstraintConfiguration constraintConfiguration;

	private ConstraintMatchInfo constraintMatchInfo;

	@ValueRangeProvider(id = "anchorTokenRange")
	@ProblemFactCollectionProperty
	private List<AnchorToken> anchorTokenList;

	@ValueRangeProvider(id = "ghostTokenRange")
	@ProblemFactCollectionProperty
	private List<GhostToken> ghostTokenList;

	@ValueRangeProvider(id = "tokenRange")
	@PlanningEntityCollectionProperty
	private List<Token> tokenList;

	@PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 2)
	private BendableScore score;

	// Ignored by OptaPlanner, used by the UI to display solve or stop solving
	// button
	private SolverStatus solverStatus;

	public boolean isEquals(ArbSolution other) {
		boolean sameId = this.id == other.getId();
		boolean sameRatioProvider = this.ratioProvider == other.getRatioProvider();
		boolean sameConstraintConfig = this.getConstraintConfiguration() == other.getConstraintConfiguration();
		
		System.out.println("Anchors");
		for (AnchorToken a : this.getAnchorTokenList()) {
			System.out.println(a);
		}
		for (AnchorToken a : other.getAnchorTokenList()) {
			System.out.println(a);
		}
		
		System.out.println("Ghost");
		for (GhostToken a : this.getGhostTokenList()) {
			System.out.println(a);
		}
		for (GhostToken a : other.getGhostTokenList()) {
			System.out.println(a);
		}
		
		System.out.println("Token");
		for (Token a : this.getTokenList()) {
			System.out.println(a);
		}
		for (Token a : other.getTokenList()) {
			System.out.println(a);
		}
		
//		System.out.println(String.format("%s %s %s %s %s %s", sameId, sameRatioProvider, sameConstraintConfig, sameAnchor, sameGhost, sameTokens));
		
//		return sameId && sameRatioProvider && sameConstraintConfig && sameAnchor && sameGhost && sameTokens;
		return true;
	}

//	private <T extends ITokenOrStart> boolean arrEqual(List<T> a, List<T> b) {
//		if (a.size()!=b.size()) {
//			return false;
//		}
//		Collections.sort(a);
//		Collections.sort((List<T>) b);
//		return false;
//	}
	private ArbSolution() {
	}

	public void initialiseRatioProvider(List<PairInfo> pairs) {
		this.ratioProvider.loadPairInfo(pairs);
	}

	public ArbSolution(List<AnchorToken> anchorTokenList, List<GhostToken> ghostTokenList, List<Token> tokenList) {
		super();
		this.anchorTokenList = anchorTokenList;
		this.ghostTokenList = ghostTokenList;
		this.tokenList = tokenList;
	}

	private EntityWithId findById(List<? extends EntityWithId> arr, Long id) {
		for (EntityWithId a : arr) {
			if (a.getId().equals(id)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return String.format("[Solution]\n%s\n%s\n%s\n%s", 
				this.anchorTokenList, this.tokenList, this.ghostTokenList,this.ratioProvider.getRatioMap());
	}

	public boolean ensureHasId(EntityWithId e) {
		return (e.getId() != null);
	}

	public boolean objEquals(Object a, Object b) {
		return (a == null && b == null) || (a.equals(b) && a.hashCode() == b.hashCode());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public RatioProvider getRatioProvider() {
		return ratioProvider;
	}

	public void setRatioProvider(RatioProvider ratioProvider) {
		this.ratioProvider = ratioProvider;
	}

	public ArbConstraintConfiguration getConstraintConfiguration() {
		return constraintConfiguration;
	}

	public void setConstraintConfiguration(ArbConstraintConfiguration constraintConfiguration) {
		this.constraintConfiguration = constraintConfiguration;
	}

	public List<AnchorToken> getAnchorTokenList() {
		return anchorTokenList;
	}

	public void setAnchorTokenList(List<AnchorToken> anchorTokenList) {
		this.anchorTokenList = anchorTokenList;
	}

	public List<GhostToken> getGhostTokenList() {
		return ghostTokenList;
	}

	public void setGhostTokenList(List<GhostToken> ghostTokenList) {
		this.ghostTokenList = ghostTokenList;
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList) {
		this.tokenList = tokenList;
	}

	public BendableScore getScore() {
		return score;
	}

	public void setScore(BendableScore score) {
		this.score = score;
	}

	public SolverStatus getSolverStatus() {
		return solverStatus;
	}

	public void setSolverStatus(SolverStatus solverStatus) {
		this.solverStatus = solverStatus;
	}

	public ConstraintMatchInfo getConstraintMatchInfo() {
		return constraintMatchInfo;
	}

	public void setConstraintMatchInfo(ConstraintMatchInfo constraintMatchInfo) {
		this.constraintMatchInfo = constraintMatchInfo;
	}
	private ProfitableChain getProfitableChain(AnchorToken a){
		ITokenOrStart t = a;
		List<String> j = new ArrayList<String>();
		float latestRatio = 1.0f;
		while (t != null) {
			j.add(t.getShortDesc());
			
			if (t instanceof Token) {
				latestRatio =  ((Token)t).getRatioFromPrevious();
			}
			t = t.getNextToken();
			
//			if (t==null) {
//				//end of chain
//				if (Math.pow(10,latestRatio) <= 1.0) {
//					return null;
//				}
//			}
		}
		return new ProfitableChain(j, latestRatio);
	}
	private String printChainInfo(AnchorToken a) {
		ITokenOrStart t = a;
		StringJoiner j = new StringJoiner(" -> ");
		while (t != null) {
			String additional = (t instanceof Token) ? "(" + ((Token)t).getRatioFromPrevious() + ")" : "";
			j.add(t.getShortDesc()+additional);
			t = t.getNextToken();
		}
		return j.toString();
	}
	@JsonIgnore
	public String getChain() {
		List<String> chains = new ArrayList<String>();
		for (AnchorToken a : this.getAnchorTokenList()) {
			chains.add(printChainInfo(a));			
		}
		return String.join("\n", chains);
	}
	public List<ProfitableChain> getProfitableTokenChains() {
		List<ProfitableChain> r = new ArrayList<ProfitableChain>();
		for (AnchorToken a : this.getAnchorTokenList()) {
			if (a.getNextToken() != null) {
				ProfitableChain temp = getProfitableChain(a);
				if (temp != null) {					
					r.add(temp);
				}
			}
		}
		return r;
	}
	
//	public String toString() {
//		StringJoiner s = new StringJoiner("\n");
//		s.add(anchorTokenList.toString());
//		s.add(ghostTokenList.toString());
//		s.add(tokenList.toString());
//		s.add(this.getRatioProvider().getRatioMap().toString());
//
//		return s.toString();
//		
//		
//	}

}
