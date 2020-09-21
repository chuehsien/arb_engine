package com.dapp.opti.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dapp.opti.Utils;
import com.dapp.opti.domain.AnchorToken;
import com.dapp.opti.domain.ArbSolution;
import com.dapp.opti.domain.ITokenOrStart;
import com.dapp.opti.domain.PairInfo;
import com.dapp.opti.domain.RatioProvider;
import com.dapp.opti.domain.Token;
import com.dapp.opti.solver.ArbConstraintProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

@SpringBootTest(properties = { "optaplanner.solver.termination.spent-limit=1h", // Effectively disable this termination
																				// in favor of the best-score-limit
		"optaplanner.solver.termination.best-score-limit=[0/*]hard/[*/*]soft" })
public class ArbControllerTest {

//    @Autowired
//    private TestAllocationController solutionController;
	@Autowired
	private RatioProvider ratioProvider;
	
	private ConstraintVerifier<ArbConstraintProvider, ArbSolution> constraintVerifier = ConstraintVerifier
			.build(new ArbConstraintProvider(), ArbSolution.class, Token.class);



//	// Hard 1
//	maxProfit(constraintFactory),
//	
//	// soft
//	minimiseConnectionToNonGhost(constraintFactory)
//	

	
	private void chain(ITokenOrStart... tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (i > 0) {
				tokens[i].setPreviousTokenOrStart(tokens[i - 1]);
			}
			if (i < tokens.length-1) {
				tokens[i].setNextToken((Token) tokens[i + 1]);
			}
		}
	}
	@Test
	public void mustBeProfitable1() {
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("service1_t1", "service1_t2",1.1f));
		pairs.add(new PairInfo("service1_t2", "service2_t2",1.0f));
		pairs.add(new PairInfo("service1_t2", "service2_t1",0.9f));
		this.ratioProvider.loadPairInfo(pairs);
		
		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service1", "t2");
		Token t3 = new Token("service2", "t2");
		Token t4 = new Token("service2", "t1");
		chain(new ITokenOrStart[] { a1, t1, t2, t3, t4 });
		t1.setAnchorToken(a1);
		t2.setAnchorToken(a1);
		t3.setAnchorToken(a1);
		t4.setAnchorToken(a1);
		
		// set shadowvars
		t1.setRatioFromPrevious(1f);
		t2.setRatioFromPrevious(1.1f);
		t3.setRatioFromPrevious(1.0f);
		t4.setRatioFromPrevious(0.9f);
		
//		constraintVerifier.verifyThat(ArbConstraintProvider::mustBeProfitable).given(a1, t1, t2, t3, t4)
//				.penalizesBy(1);

	}
	@Test
	public void mustBeProfitable2() {
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("service1_t1", "service1_t2",0.9f));
		pairs.add(new PairInfo("service1_t2", "service2_t2",1.0f));
		pairs.add(new PairInfo("service1_t2", "service2_t1",1.12f));
		this.ratioProvider.loadPairInfo(pairs);
		
		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service1", "t2");
		Token t3 = new Token("service2", "t2");
		Token t4 = new Token("service2", "t1");
		chain(new ITokenOrStart[] { a1, t1, t2, t3, t4 });
		t1.setAnchorToken(a1);
		t2.setAnchorToken(a1);
		t3.setAnchorToken(a1);
		t4.setAnchorToken(a1);
		
		// set shadowvars
		t1.setRatioFromPrevious(1f);
		t2.setRatioFromPrevious(0.9f);
		t3.setRatioFromPrevious(1.0f);
		t4.setRatioFromPrevious(1.12f);
		
//		constraintVerifier.verifyThat(ArbConstraintProvider::mustBeProfitable).given(a1, t1, t2, t3, t4)
//				.penalizesBy(0);

	}
	@Test
	public void maxProfit1() {
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("service1_t1", "service1_t2",1.1f));
		pairs.add(new PairInfo("service1_t2", "service2_t2",1.0f));
		pairs.add(new PairInfo("service1_t2", "service2_t1",0.9f));
		this.ratioProvider.loadPairInfo(pairs);
		
		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service1", "t2");
		Token t3 = new Token("service2", "t2");
		Token t4 = new Token("service2", "t1");
		chain(new ITokenOrStart[] { a1, t1, t2, t3, t4 });
		t1.setAnchorToken(a1);
		t2.setAnchorToken(a1);
		t3.setAnchorToken(a1);
		t4.setAnchorToken(a1);
		
		// set shadowvars
		t1.setRatioFromPrevious(1f);
		t2.setRatioFromPrevious(1.1f);
		t3.setRatioFromPrevious(1.0f);
		t4.setRatioFromPrevious(0.9f);
		
		constraintVerifier.verifyThat(ArbConstraintProvider::maxProfit).given(a1, t1, t2, t3, t4)
		.rewardsWith(0);

	}
	@Test
	public void maxProfit2() {
		List<PairInfo> pairs = new ArrayList<PairInfo>();
		pairs.add(new PairInfo("service1_t1", "service1_t2",0.9f));
		pairs.add(new PairInfo("service1_t2", "service2_t2",1.0f));
		pairs.add(new PairInfo("service1_t2", "service2_t1",1.12f));
		this.ratioProvider.loadPairInfo(pairs);
		
		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service1", "t2");
		Token t3 = new Token("service2", "t2");
		Token t4 = new Token("service2", "t1");
		chain(new ITokenOrStart[] { a1, t1, t2, t3, t4 });
		t1.setAnchorToken(a1);
		t2.setAnchorToken(a1);
		t3.setAnchorToken(a1);
		t4.setAnchorToken(a1);
		
		// set shadowvars
		t1.setRatioFromPrevious(1f);
		t2.setRatioFromPrevious(0.9f);
		t3.setRatioFromPrevious(1.0f);
		t4.setRatioFromPrevious(1.12f);
		
		constraintVerifier.verifyThat(ArbConstraintProvider::maxProfit).given(a1, t1, t2, t3, t4)
				.rewardsWith((int)(Math.round(Math.abs(1 - 0.9 * 1.0 * 1.12) * 100 * 100)));
	}
	@Test
	public void tokenStartEndSame() {
		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service1", "t2");
		Token t3 = new Token("service2", "t2");
		Token t4 = new Token("service2", "t1");
		chain(new ITokenOrStart[] { a1, t1, t2, t3, t4 });
		t1.setAnchorToken(a1);
		t2.setAnchorToken(a1);
		t3.setAnchorToken(a1);
		t4.setAnchorToken(a1);
		constraintVerifier.verifyThat(ArbConstraintProvider::tokenStartEndSame).given(a1, t1, t2, t3, t4)
				.penalizesBy(0);

		Token t5 = new Token("service2", "t3");
		t5.setAnchorToken(a1);
		chain(new ITokenOrStart[] { t4, t5 });

		constraintVerifier.verifyThat(ArbConstraintProvider::tokenStartEndSame).given(a1, t1, t2, t3, t4, t5)
				.penalizesBy(1);

	}

	@Test
	public void prevTokenSameServiceOrSameName() {
		Token t1 = new Token("service1", "t1");
		Token t2 = new Token("service2", "t2");
		Token t3 = new Token("service2", "t3");
		Token t4 = new Token("service3", "t3");
		chain(new ITokenOrStart[] { t1, t2, t3, t4 });

		constraintVerifier.verifyThat(ArbConstraintProvider::prevTokenSameServiceOrSameName).given(t2)
				.penalizesBy(1);
		constraintVerifier.verifyThat(ArbConstraintProvider::prevTokenSameServiceOrSameName).given(t3)
				.penalizesBy(0);
		constraintVerifier.verifyThat(ArbConstraintProvider::prevTokenSameServiceOrSameName).given(t4)
				.penalizesBy(0);

	}

	@Test
	public void testAnchorTokenHasNextToken() {

		AnchorToken a1 = new AnchorToken("t1");
		Token t1 = new Token("service1", "t1");

		t1.setPreviousTokenOrStart(a1);
		a1.setNextToken(t1);

		AnchorToken a2 = new AnchorToken("name1");

		constraintVerifier.verifyThat(ArbConstraintProvider::anchorTokenHasNextToken).given(a1, t1).penalizesBy(0);

		constraintVerifier.verifyThat(ArbConstraintProvider::anchorTokenHasNextToken).given(a2).penalizesBy(1);

	}
}
