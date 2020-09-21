package com.dapp.opti;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dapp.opti.persistence.ArbSolutionRepository;

@SpringBootApplication
public class ArbApp {
	
	
	public static void main(String[] args) {
		SpringApplication.run(ArbApp.class, args);
		
	}

//	@Bean
//	public CommandLineRunner demoData(SwabAssignmentSolutionRepository swabAssignmentSolutionRepository, LabRepository labRepository, SwabRequestRepository swabRequestRepository,
//			LabPreferenceRepository swabStartRepository, SwabTeamDayRepository swabTeamDayRepository,
//			SwabTeamRepository swabTeamRepository) {
//		
		
	@Bean
	public CommandLineRunner demoData(ArbSolutionRepository arbSolutionRepository) {
		
	
		
//		XStreamSolutionFileIO<SwabAssignmentSolution> test = new XStreamSolutionFileIO<SwabAssignmentSolution>();
//		test.write(swabAssignmentSolutionRepository.getScenario(4,10,10,5), new File("./small.xml"));
//		test.write(swabAssignmentSolutionRepository.getScenario(15,40,50,10), new File("./med.xml"));
//		test.write(swabAssignmentSolutionRepository.getScenario(20,80,100,20), new File("./large.xml"));
//		PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
//		        "benchmark.xml");
//		PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
//		benchmark.benchmarkAndShowReportInBrowser();
		
		return (args) -> {
//			arbSolutionRepository.resetScenario();
		};
	}



	
	public enum DemoData {
		NONE, SMALL, LARGE
	}

}
