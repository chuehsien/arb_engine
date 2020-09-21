package com.dapp.opti.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintMatchInfo {
	private static final Logger logger = LoggerFactory.getLogger(ConstraintMatchInfo.class);

	private String name;
	private String score;
	private List<ConstraintMatchInfo> subConstraintInfo;

	public ConstraintMatchInfo(String name, String score) {
		super();
		this.name = name;
		this.score = score;
	}

	private boolean allZero(BendableScore score) {
		for (int i = 0; i < score.getLevelsSize(); i++) {
			if (score.getHardOrSoftScore(i) != 0) {
				return false;
			}
		}
		return true;
	}

	public void registerConstraintMatch(Collection<ConstraintMatchTotal> constraintMatchTotals) {

		List<ConstraintMatchInfo> totalI = new ArrayList<ConstraintMatchInfo>();
		this.subConstraintInfo = totalI;
		for (ConstraintMatchTotal constraintMatchTotal : constraintMatchTotals) {
			String constraintName = constraintMatchTotal.getConstraintName();
			// The score impact of that constraint

			BendableScore totalScore = (BendableScore) constraintMatchTotal.getScore();
//			logger.info(String.format("%s - %s", constraintName, totalScore));

			ConstraintMatchInfo i = new ConstraintMatchInfo(constraintName, totalScore.toString());
			totalI.add(i);
			List<ConstraintMatchInfo> totalSubI = new ArrayList<ConstraintMatchInfo>();
			if (constraintMatchTotal.getConstraintMatchSet().size() > 0) {
				i.setSubConstraintInfo(totalSubI);
			}
			for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
				List<Object> justificationList = constraintMatch.getJustificationList();
				BendableScore score = (BendableScore) constraintMatch.getScore();

				if (!allZero(score)) {
					// logger.info(String.format("-----%s - %s", justificationList, score));
					ConstraintMatchInfo subI = new ConstraintMatchInfo(justificationList.toString(), score.toString());
					totalSubI.add(subI);
				}
			}
		}

	}

	public List<ConstraintMatchInfo> getSubConstraintInfo() {
		return subConstraintInfo;
	}

	public void setSubConstraintInfo(List<ConstraintMatchInfo> subConstraintInfo) {
		this.subConstraintInfo = subConstraintInfo;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
