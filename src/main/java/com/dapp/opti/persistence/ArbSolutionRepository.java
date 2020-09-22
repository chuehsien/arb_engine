package com.dapp.opti.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dapp.opti.domain.AnchorToken;
import com.dapp.opti.domain.ArbSolution;
import com.dapp.opti.domain.ConstraintMatchInfo;
import com.dapp.opti.domain.GhostToken;
import com.dapp.opti.domain.PairInfo;
import com.dapp.opti.domain.RatioProvider;
import com.dapp.opti.domain.Request;
import com.dapp.opti.domain.Token;
import com.dapp.opti.domain.solver.ArbConstraintConfiguration;

@Service
@Transactional
public class ArbSolutionRepository {
	private static final Logger logger = LoggerFactory.getLogger(ArbSolutionRepository.class);

	public static Random random = new Random(0);

	// There is only one time table, so there is only timeTableId (= problemId).
	public static Long SINGLETON_SOLUTION_ID = new Date().getTime();

	public static Map<Long, ArbSolution> inMemStore = new HashMap<Long, ArbSolution>();

	public ArbSolution lastLoadedScenario; 
	
	ScoreDirectorFactory<ArbSolution> scoreDirectorFactory;

	public ArbSolutionRepository() {
		super();
	}

	@Autowired
	SolverFactory<ArbSolution> solverFactory;

	@Autowired
	private ArbConstraintConfiguration constraintConfiguration;

	@Autowired
	private RatioProvider ratioProvider;

	@Autowired
	private SolverManager<ArbSolution, Long> solverManager;

	@Autowired
	private ScoreManager<ArbSolution> scoreManager;

	public ArbSolution findById(Long id) {

		if (!SINGLETON_SOLUTION_ID.equals(id)) {
			throw new IllegalStateException("There is no solution with id (" + id + ").");
		}

		if (inMemStore.containsKey(id)) {
//			System.out.println("Retrieving from memstore");
			return inMemStore.get(id);
		}
		logger.error("No scenario loaded yet, initialising random scenario.");
		ArbSolution s = this.getMockScenario();
		s.setId(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
		inMemStore.put(id, s);

		return s;
	}
	public ArbSolution getMockScenario() {
		ArbSolution s = this.getMockScenario3();
		System.out.println(s);
		return s;
		
	}
	public ArbSolution getMockScenario3() {
		System.out.println("getMockScenario3");
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("CURVEY_USDC", "CURVEY_USDT",0.6602420471472215f));
		pairs.add(new PairInfo("CURVEY_USDT", "CURVEY_USDC",1.4982410614412558f));
		pairs.add(new PairInfo("UNI_USDC", "CURVEY_USDC",1.0f));
		pairs.add(new PairInfo("UNI_USDT", "CURVEY_USDT",1.0f));
		pairs.add(new PairInfo("CURVEY_USDC", "UNI_USDC",1.0f));
		pairs.add(new PairInfo("UNI_USDC", "UNI_USDT",0.9558565241762697f));
		pairs.add(new PairInfo("CURVEY_USDT", "UNI_USDT",1.0f));
		pairs.add(new PairInfo("UNI_USDT", "UNI_USDC",0.9615618080212112f));
		List<String> anchors = new ArrayList<String>();
		anchors.add("USDT");
		Request r = new Request();
		r.setAnchorNames(anchors);
		r.setPairs(pairs);
		return makeScenario(r);
	}
	public ArbSolution makeScenario(Request r) {
		
//		System.out.println(r);
//		return null;
		List<PairInfo> pairsFromReq = r.getPairs();
		Set<String> service_names = new HashSet<String>();
		for (PairInfo p : pairsFromReq) {
			service_names.add(p.getA());
			service_names.add(p.getB());
		}
//		System.out.println(service_names);
		
		List<Token> tokens = new ArrayList<Token>();
		for (String s_n : service_names) {
			tokens.add(new Token(s_n.split("_")[0],s_n.split("_")[1]));
		}
//		tokens.add(new Token("UNI","USDC"));
//		tokens.add(new Token("UNI","USDT"));
//		tokens.add(new Token("CURVEY","USDT"));
//		tokens.add(new Token("CURVEY","USDC"));
		
//		Collections.shuffle(tokens);
//		service_names.stream().map((sn) -> new Token(sn)).collect(Collectors.toList());
		
		List<GhostToken> ghostTokens = new ArrayList<GhostToken>();
		for (int k = 0; k < tokens.size(); k++) {
			ghostTokens.add(new GhostToken());
		}
		
		List<String> anchors = r.getAnchorNames();
		List<AnchorToken> anchorTokens = new ArrayList<AnchorToken>();
		for (String a : anchors) {
			anchorTokens.add(new AnchorToken(a));
		}
		
		

//		// sanity check
//		Set<String> names = new HashSet<String>();
//		for (PairInfo p : pairsFromReq) {
//			names.addAll(p.getNames());
//		}
//		for (String a : anchors) {
//			if (!names.contains(a)) {
//				System.err.println(String.format("Anchor token has %s but does not exist in tokens from pairs: %s", a,
//						pairsFromReq, names));
//				return null;
//			}
//		}

		ArbSolution s = new ArbSolution(anchorTokens, ghostTokens, tokens);
		
		s.setConstraintConfiguration(constraintConfiguration);
		
		
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		for (PairInfo pi : r.getPairs()) {
			pairs.add(new PairInfo(pi.getA(),pi.getB(),pi.getRate()));
		}
		
		s.setRatioProvider(this.ratioProvider);
//		System.out.println(pairs);
		s.initialiseRatioProvider(pairs);
		
		s.setId(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
		inMemStore.put(ArbSolutionRepository.SINGLETON_SOLUTION_ID, s);
		lastLoadedScenario = s;
//		
		System.out.println(s);
//		ArbSolution test = this.getMockScenario2();
//		System.out.println("Test with mock: " + test.isEquals(s));
		return s;
	}

	public void resetLite() {
		inMemStore.clear();
	}

	public void resetScenario() {
		if (lastLoadedScenario != null) {
			inMemStore.put(ArbSolutionRepository.SINGLETON_SOLUTION_ID, lastLoadedScenario);
		} else {
			ArbSolution s = this.getMockScenario();
			s.setId(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
			inMemStore.put(ArbSolutionRepository.SINGLETON_SOLUTION_ID, lastLoadedScenario);
		}

	}

	public ArbSolution getMockScenario2() {
		System.out.println("getMockScenario2");
		List<Token> tokens = new ArrayList<Token>();
		tokens.add(new Token("UNI","USDC"));
		tokens.add(new Token("UNI","USDT"));
		tokens.add(new Token("CURVEY","USDT"));
		tokens.add(new Token("CURVEY","USDC"));
		
		List<GhostToken> ghostTokens = new ArrayList<GhostToken>();
		for (int k = 0; k < tokens.size(); k++) {
			ghostTokens.add(new GhostToken());
		}
		
		List<AnchorToken> anchorTokens = new ArrayList<AnchorToken>();
		anchorTokens.add(new AnchorToken("USDT"));
		
		ArbSolution s = new ArbSolution(anchorTokens, ghostTokens, tokens);
//		System.out.println(anchorTokens);
//		System.out.println(ghostTokens);
//		System.out.println(tokens);
		s.setConstraintConfiguration(constraintConfiguration);

		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("CURVEY_USDC", "CURVEY_USDT",0.6602420471472215f));
		pairs.add(new PairInfo("CURVEY_USDT", "CURVEY_USDC",1.4982410614412558f));
		pairs.add(new PairInfo("UNI_USDC", "CURVEY_USDC",1.0f));
		pairs.add(new PairInfo("UNI_USDT", "CURVEY_USDT",1.0f));
		pairs.add(new PairInfo("CURVEY_USDC", "UNI_USDC",1.0f));
		pairs.add(new PairInfo("UNI_USDC", "UNI_USDT",0.9558565241762697f));
		pairs.add(new PairInfo("CURVEY_USDT", "UNI_USDT",1.0f));
		pairs.add(new PairInfo("UNI_USDT", "UNI_USDC",0.9615618080212112f));
		
		
		
		// make mock pair info
		s.setRatioProvider(this.ratioProvider);
//		System.out.println(pairs);
		s.initialiseRatioProvider(pairs);
		
		s.setId(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
		inMemStore.put(ArbSolutionRepository.SINGLETON_SOLUTION_ID, s);
		lastLoadedScenario = s;
		
		return s;
	}
//	public ArbSolution getMockScenario() {
//
//		String[] names = new String[] { "DAI", "USDC", "USDT", "TUSD", "ETH" };
//		String[] platforms = new String[] { "CURVE", "UNI", "MOONI", "BAL" };
//
//		List<Token> tokens = new ArrayList<Token>();
//		for (int i = 0; i < names.length; i++) {
//			for (int j = 0; j < platforms.length; j++) {
//				String name = names[i];
//				String service = platforms[j];
//
//				tokens.add(new Token(service, name));
//			}
//		}
//
//		List<GhostToken> ghostTokens = new ArrayList<GhostToken>();
//		for (int k = 0; k < names.length * platforms.length; k++) {
//			ghostTokens.add(new GhostToken());
//		}
//
//		List<AnchorToken> anchorTokens = new ArrayList<AnchorToken>();
//
//		for (int m = 0; m < names.length; m++) {
//			anchorTokens.add(new AnchorToken(names[m]));
//		}
//
//		ArbSolution s = new ArbSolution(anchorTokens, ghostTokens, tokens);
//
//		s.setConstraintConfiguration(constraintConfiguration);
//
//		// make mock pair info
//		s.setRatioProvider(this.ratioProvider);
//		s.initialiseRatioProvider(makePairs());
//
//		return s;
//	}

	public List<PairInfo> makePairs() {
		List<PairInfo> p = new ArrayList<PairInfo>();
		String[] all_tokens = new String[] { "CURVE_DAI", "CURVE_USDT", "CURVE_USDC", "CURVE_TUSD", "UNI_ETH",
				"UNI_DAI", "UNI_USDT", "UNI_USDC", "UNI_TUSD", "MOONI_ETH", "MOONI_DAI", "MOONI_USDT", "MOONI_USDC",
				"MOONI_TUSD", "BAL_ETH", "BAL_DAI", "BAL_USDT", "BAL_USDC", "BAL_TUSD" };

		HashMap<String, HashMap<String, Float>> m = new HashMap<String, HashMap<String, Float>>();

		for (String c1 : all_tokens) {
			for (String c2 : all_tokens) {
				if (c1 == c2) {
					continue;
				}
				if ((m.containsKey(c1) && m.get(c1).containsKey(c2))
						|| (m.containsKey(c2) && m.get(c2).containsKey(c1))) {
					continue;
				}

				String serviceX = c1.split("_")[0];
				String nameX = c1.split("_")[1];
				String serviceY = c2.split("_")[0];
				String nameY = c2.split("_")[1];

				float f = 0;
				if (!serviceX.equals(serviceY) && !nameX.equals(nameY)) {

					continue;
				} else if (serviceX.equals(serviceY) && !nameX.contentEquals(nameY)) {

					float discount = (float) ((random.nextFloat() - 0.5) * 0.1);
					f = 1 + discount;
				} else if (!serviceX.equals(serviceY) && nameX.equals(nameY)) {

					f = 1;
				}
				if (!m.containsKey(c1)) {
					m.put(c1, new HashMap<String, Float>());
				}
				if (!m.containsKey(c2)) {
					m.put(c2, new HashMap<String, Float>());
				}

				m.get(c1).put(c2, f);
				m.get(c2).put(c1, 1 / f);
				p.add(new PairInfo(c1, c2, f));
				p.add(new PairInfo(c2, c1, 1 / f));
			}
		}
		return p;
	}

	public void printIndictment(ArbSolution solution) {

		ScoreDirectorFactory<ArbSolution> scoreDirectorFactory = solverFactory.getScoreDirectorFactory();
		try (ScoreDirector<ArbSolution> guiScoreDirector = scoreDirectorFactory.buildScoreDirector()) {
			guiScoreDirector.setWorkingSolution(solution);
//			Score score = guiScoreDirector.calculateScore();

			Collection<ConstraintMatchTotal> constraintMatchTotals = guiScoreDirector.getConstraintMatchTotals();
			for (ConstraintMatchTotal constraintMatchTotal : constraintMatchTotals) {
				String constraintName = constraintMatchTotal.getConstraintName();
				// The score impact of that constraint
				HardMediumSoftScore totalScore = (HardMediumSoftScore) constraintMatchTotal.getScore();
				logger.info(String.format("%s - %s", constraintName, totalScore));
				for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
					List<Object> justificationList = constraintMatch.getJustificationList();
					HardMediumSoftScore score = (HardMediumSoftScore) constraintMatch.getScore();

					logger.info(String.format("----- %s - %s", justificationList, score));
				}
			}

		}
	}
//
//	public void printSoln(ArbSolution solution) {
//		logger.info("====================================");
//		logger.info(String.format("Labs: %s", solution.getLabList().toString()));
//		logger.info(String.format("Teams: %s", solution.getSwabTeams().toString()));
//		logger.info("----REQUESTS-------");
//		for (SwabRequest r : solution.getSwabRequests()) {
//			logger.info(String.format("----%s", r.toString()));
//		}
//		logger.info("----RESULTS----");
//		for (SwabTeamDay d : solution.getSwabTeamDayList()) {
//			logger.info(String.format("----%s", d.toString()));
//		}
//		logger.info(String.format("---- Score: %s ----", solution.getScore().toString()));
//		logger.info("====================================");
//
//	}

//	@Modifying
//	@Transactional
//	private void saveSoln(ArbSolution solution) {
//		teamSlotRepository.saveAll(solution.getTeamSlots());
//		labRepository.saveAll(solution.getLabList());
//		swabTeamRepository.saveAll(solution.getSwabTeams());
//		swabRequestRepository.saveAll(solution.getSwabRequests());
//		swabTeamDayRepository.saveAll(solution.getSwabTeamDayList());
//		daySlotRepository.saveAll(solution.getDates());
//		labPreferenceRepository.saveAll(solution.getLabPreferences());
//	}

	public void newSoln(ArbSolution solution, long id) {

		System.out.println(solution.getScore() + " : " + solution.getChain());
		this.inMemStore.put(id, solution);
	}

	public ConstraintMatchInfo makeConstraintInfo(ArbSolution solution) {

		ScoreDirectorFactory<ArbSolution> scoreDirectorFactory = solverFactory.getScoreDirectorFactory();
		try (ScoreDirector<ArbSolution> guiScoreDirector = scoreDirectorFactory.buildScoreDirector()) {
			guiScoreDirector.setWorkingSolution(solution);
			Score score = guiScoreDirector.calculateScore();

			ConstraintMatchInfo cmi = new ConstraintMatchInfo("main", score.toString());
			Collection<ConstraintMatchTotal> constraintMatchTotals = guiScoreDirector.getConstraintMatchTotals();
			cmi.registerConstraintMatch(constraintMatchTotals);

			return cmi;
		}

	}

	public void solve() {
		SolverJob j = solverManager.solveAndListen(ArbSolutionRepository.SINGLETON_SOLUTION_ID,
				(id) -> this.findById(id), (soln) -> this.newSoln(soln, ArbSolutionRepository.SINGLETON_SOLUTION_ID));
	}

}
