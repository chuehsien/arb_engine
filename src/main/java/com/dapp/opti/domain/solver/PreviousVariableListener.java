package com.dapp.opti.domain.solver;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.dapp.opti.domain.AnchorToken;
import com.dapp.opti.domain.ArbSolution;
import com.dapp.opti.domain.ITokenOrStart;
import com.dapp.opti.domain.RatioProvider;
import com.dapp.opti.domain.Token;

public class PreviousVariableListener implements VariableListener<Token> {
	
	
	protected void update(ScoreDirector scoreDirector, Token sourceToken) {
		updateDownstream(scoreDirector, sourceToken);
	}
	
	// incrementally update previous ratio, ratio so far from predeccessors, ratio forward
	protected void updateDownstream(ScoreDirector scoreDirector, Token sourceToken) {
		ArbSolution s = (ArbSolution) scoreDirector.getWorkingSolution();
		RatioProvider ratioProvider = s.getRatioProvider();
//		System.out.println("AnchorUpdatingVariableListener: ---" + sourceToken.getShortDesc() + "-----");
		
		// propagate downstream
//		System.out.println("Propagate downstream");
//		System.out.println("Previous: " + sourceToken.getPreviousTokenOrStart());
		
		ITokenOrStart prev = sourceToken.getPreviousTokenOrStart();
		float ratioPrev = 1;
		if (prev != null) {
			if (prev.isGhost()) {
				ratioPrev = 0;
			}
			else if (prev instanceof AnchorToken) {
				ratioPrev = 1;
			} else {
				ratioPrev = ((Token)prev).getRatioFromPrevious() * ratioProvider.getRatio(prev, sourceToken);
			}
		} else if (prev == null) {
			ratioPrev = 0;
		}
		
		Token nextToken = sourceToken;
		
		while (nextToken != null) {
//			System.out.println(nextToken.getShortDesc() + " : " + ratioPrev);
			scoreDirector.beforeVariableChanged(nextToken, "ratioFromPrevious");
			nextToken.setRatioFromPrevious(ratioPrev);
			scoreDirector.afterVariableChanged(nextToken, "ratioFromPrevious");
			
			Token upNext = nextToken.getNextToken();
			if (upNext == null) {
				break;
			}
			ratioPrev *= ratioProvider.getRatio(nextToken, upNext);
			nextToken = upNext;
//			System.out.println("nextToken: " + nextToken.getShortDesc());
			 

		}
	}
	
	@Override
	public void beforeEntityAdded(ScoreDirector scoreDirector, Token entity) {
	}

	@Override
	public void afterEntityAdded(ScoreDirector scoreDirector, Token entity) {
		update(scoreDirector, entity);
	}

	@Override
	public void beforeVariableChanged(ScoreDirector scoreDirector, Token entity) {
//		update(scoreDirector, entity);
	}

	@Override
	public void afterVariableChanged(ScoreDirector scoreDirector, Token entity) {
		update(scoreDirector, entity);
	}

	@Override
	public void beforeEntityRemoved(ScoreDirector scoreDirector, Token entity) {
	}

	@Override
	public void afterEntityRemoved(ScoreDirector scoreDirector, Token entity) {
		update(scoreDirector, entity);
	}

}
