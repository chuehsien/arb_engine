package com.dapp.opti.solver;

import java.util.function.Function;
import static org.optaplanner.core.api.score.stream.Joiners.*;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dapp.opti.domain.AnchorToken;
import com.dapp.opti.domain.ITokenOrStart;
import com.dapp.opti.domain.RatioProvider;
import com.dapp.opti.domain.Token;

@Service
public final class ArbConstraintProvider implements ConstraintProvider {
	private static final Logger logger = LoggerFactory.getLogger(ArbConstraintProvider.class);
	
	@Autowired
	private RatioProvider ratioProvider;
		
	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
		return new Constraint[] {
				// Hard 0
			
				anchorTokenHasNextToken(constraintFactory),
				prevTokenSameServiceOrSameName(constraintFactory),
				
				// Hard 1				
//				mustBeProfitable(constraintFactory),
				tokenStartEndSame(constraintFactory),
				
				// soft 0
				maxProfit(constraintFactory),
//				maxEdgeRatio(constraintFactory),
				
				// soft 1
//				minimiseConnectionToNonGhost(constraintFactory),
				
		};
	}

	/* hard level 0 */
	
	protected Constraint anchorTokenHasNextToken(ConstraintFactory constraintFactory) {
	
		return constraintFactory.from(AnchorToken.class).filter((t) -> t.noNextToken() && !t.isGhost())
				.penalizeConfigurable("anchorTokenHasNextToken");
	}
	
	protected Constraint mustBeProfitable(ConstraintFactory constraintFactory) {
	
		return constraintFactory.from(AnchorToken.class).filter(AnchorToken::hasNextToken)
				.penalizeConfigurable("mustBeProfitable", (anchor) -> (int)(isProfit(anchor)? 0 : Math.round(Math.abs(getProfit(anchor)))));
	}
	
	protected Constraint tokenStartEndSame(ConstraintFactory constraintFactory) {
		
		return constraintFactory.from(AnchorToken.class).filter((a) -> a.hasNextToken() && !a.isGhost())
				.join(Token.class, 
						equal(Function.identity(), Token::getAnchorToken),
						filtering((a,b)-> b.noNextToken())
					)
				.penalizeConfigurable("tokenStartEndSame", (anchor, token) -> sameToken(anchor,token) ? 0:1);
	}
	
	protected Constraint prevTokenSameServiceOrSameName(ConstraintFactory constraintFactory) {
		
		return constraintFactory.from(Token.class).filter(Token::notPrevTokenSameServiceOrSameName)
				.penalizeConfigurable("prevTokenSameServiceOrSameName", (t) -> {
//					System.out.println(t);
//					System.out.println(t.getPreviousTokenOrStart().getShortDesc() + "->" + t.getShortDesc());
					return 1;
				});
	}
	
	protected Constraint maxEdgeRatio(ConstraintFactory constraintFactory) {
		
		return constraintFactory.from(Token.class).filter((t) -> t.hasPreviousToken() && t.prevIsNonGhost())
				.rewardConfigurable("maxEdgeRatio", (t) -> (int)Math.pow(t.getRatioFromPrevious(),1));
	}
	

	protected Constraint maxProfit(ConstraintFactory constraintFactory) {
		
		return constraintFactory.from(Token.class).filter((t) -> t.noNextToken() && t.getAnchorToken() != null && !t.getAnchorToken().isGhost())
				.rewardConfigurable("maxProfit", (t) ->  Math.max(0, Math.round(toPercentGain(t))));
	}
	
	protected Constraint minimiseConnectionToNonGhost(ConstraintFactory constraintFactory) {
		
		return constraintFactory.from(Token.class).filter(Token::prevIsNonGhost)
				.penalizeConfigurable("minimiseConnectionToNonGhost");
	}
	
	public boolean sameToken(ITokenOrStart anchor, ITokenOrStart token) {
		return anchor.getName().equals(token.getName());
	}
	
	private boolean isProfit(AnchorToken anchor) {
		return getProfit(anchor) > 0;
	}
	private float toPercentGain(Token t) {
		return (float)(100 * (t.getRatioFromPrevious() - 1.0));
	}
	private float getProfit(AnchorToken anchor) {
		float start = 1f;
		ITokenOrStart s = anchor;
		if (anchor.getNextToken() == null) {
			return 0;
		}else {
			s = anchor.getNextToken();
		}
		while (s != null) {
			if (s instanceof Token) {
//				System.out.println("========");
//				System.out.println(s);
//				System.out.println(((Token)s).getRatioFromPrevious());
				start *= ((Token)s).getRatioFromPrevious();
			} else {
				System.out.println("Got bug! supposed to have ratio for token " + s.toString());
			}
			s = s.getNextToken();
		}
		
		// get delta in terms of number of percent
		System.out.println("result: " +  (float) (100*(start-1.0)));
		return (float) (100*(start-1.0));
				
	}
	
}
