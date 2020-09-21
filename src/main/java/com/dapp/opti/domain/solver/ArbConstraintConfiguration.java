package com.dapp.opti.domain.solver;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@ConstraintConfiguration(constraintPackage = "com.dapp.opti.domain.solver")
@Component
public class ArbConstraintConfiguration {

	// hard 0

	@ConstraintWeight("anchorTokenHasNextToken")
	private BendableScore anchorTokenHasNextTokenScore = makeScoreLevel(0, 1);

	@ConstraintWeight("prevTokenSameServiceOrSameName")
	private BendableScore prevTokenSameServiceOrSameNameScore = makeScoreLevel(0, 1);
//	@ConstraintWeight("mustBeProfitable")
//	private BendableScore mustBeProfitableScore = makeScoreLevel(1, 1);

	
	
	@ConstraintWeight("tokenStartEndSame")
	private BendableScore tokenStartEndSameScore = makeScoreLevel(1, 1);


	@ConstraintWeight("maxProfit")
	private BendableScore maxProfitScore = makeScoreLevel(2, 1);


//	@ConstraintWeight("maxEdgeRatio")
//	private BendableScore maxEdgeRatioScore = makeScoreLevel(2, 1);

	@ConstraintWeight("minimiseConnectionToNonGhost")
	private BendableScore minimiseConnectionToNonGhost = makeScoreLevel(3, 1);

	public ArbConstraintConfiguration() {
		super();
	}

	private BendableScore makeScoreLevel(int level) {
		return this.makeScoreLevel(level, 1);
	}

	private BendableScore makeScoreLevel(int level, int multiplier) {
		switch (level) {
		case 0:
			return BendableScore.of(new int[] { multiplier, 0 }, new int[] { 0, 0 });
		case 1:
			return BendableScore.of(new int[] { 0, multiplier }, new int[] { 0, 0 });
		case 2:
			return BendableScore.of(new int[] { 0, 0 }, new int[] { multiplier, 0 });
		case 3:
			return BendableScore.of(new int[] { 0, 0 }, new int[] { 0, multiplier });
		}
		return null;
	}

}