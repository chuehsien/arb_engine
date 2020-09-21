package com.dapp.opti.solver;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dapp.opti.domain.ArbSolution;
import com.dapp.opti.domain.Request;
import com.dapp.opti.persistence.ArbSolutionRepository;

@RestController
@RequestMapping("/solution")
public class ArbController {

	@Autowired
	private ArbSolutionRepository solutionRepository;
	@Autowired
	private SolverManager<ArbSolution, Long> solverManager;
	@Autowired
	private ScoreManager<ArbSolution> scoreManager;

	@GetMapping("/")
	public ArbSolution getSolution() {

		ArbSolution solution = solutionRepository.findById(ArbSolutionRepository.SINGLETON_SOLUTION_ID);

		solution.setConstraintMatchInfo(solutionRepository.makeConstraintInfo(solution));
		scoreManager.updateScore(solution); // Sets the score
		solution.setSolverStatus(solverManager.getSolverStatus(ArbSolutionRepository.SINGLETON_SOLUTION_ID));
		return solution;
	}

	@PostMapping("/solve")
	public void solve() {
		solutionRepository.solve();
	}
	
	@PostMapping("/load")
	public String load(@RequestBody Request r) {
		if (this.getSolverStatus() == SolverStatus.SOLVING_ACTIVE) {
			solverManager.terminateEarly(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
		}
		solutionRepository.makeScenario(r);
		solutionRepository.solve();
		return String.format("Loaded scenario with %s pairs, %s anchors", r.getPairs().size(), r.getAnchorNames().size());
	}
	
	public SolverStatus getSolverStatus() {
		return solverManager.getSolverStatus(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
	}

	@PostMapping("/stopSolving")
	public void stopSolving() {
		solverManager.terminateEarly(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
	}

	@PostMapping("/reset")
	public void reset() {
		if (this.getSolverStatus() == SolverStatus.SOLVING_ACTIVE) {
			solverManager.terminateEarly(ArbSolutionRepository.SINGLETON_SOLUTION_ID);
		}
		solutionRepository.resetScenario();
	}

//	@PostMapping("/add/{type}")
//	public void addEntity(@PathVariable("type") String type) {
//		solutionRepository.flushCache();
//		solutionRepository.addEntity(type,1, ArbSolutionRepository.SINGLETON_SOLUTION_ID);
//		solutionRepository.flushCache();
//	}
//
//	@PostMapping("/remove/{type}")
//	public void removeEntity(@PathVariable("type") String type) {
//		solutionRepository.flushCache();
//		solutionRepository.removeEntity(type,1, ArbSolutionRepository.SINGLETON_SOLUTION_ID);
//		solutionRepository.flushCache();
//	}

	@GetMapping("/benchmark")
	public void benchmark() {

		PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory
				.createFromSolverConfigXmlResource("solverConfigTest.xml");

		PlannerBenchmark benchmark = benchmarkFactory
				.buildPlannerBenchmark(solutionRepository.findById(ArbSolutionRepository.SINGLETON_SOLUTION_ID));
		benchmark.benchmarkAndShowReportInBrowser();
	}

}
