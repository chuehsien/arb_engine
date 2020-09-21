package com.dapp.opti.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Service
public class RatioProvider {

	private HashMap<String, HashMap<String, Float>> ratioMap;

	public RatioProvider() {
		this.ratioMap = new HashMap<String, HashMap<String, Float>>();
	}

	public void loadPairInfo(List<PairInfo> pairs) {
//		System.out.println("Loading pairs: " + pairs);
		this.ratioMap.clear();
		for (PairInfo p : pairs) {
			if (!ratioMap.containsKey(p.getA())) {
				ratioMap.put(p.getA(), new HashMap<String, Float>());
			}
			HashMap<String, Float> m = ratioMap.get(p.getA());
			m.put(p.getB(), p.getRate());
		}
//		System.out.println("Pairs loaded:");
//		System.out.println(ratioMap);
	}

	public void clear() {
		this.ratioMap.clear();
	}

	public float getRatio(ITokenOrStart t1, ITokenOrStart t2) {
		if (t2 instanceof Token) {
			if (t1 instanceof Token) {
				return getRatio(t1.getService() + "_" + t1.getName(), t2.getService() + "_" + t2.getName());
			} else if (t1 instanceof GhostToken) {
				System.out.println("boo0");
				return 0;
			} else if (t1 instanceof AnchorToken) {
				if (t1.getName().equals(t2.getName())) {
					return 1;
				} else {
					System.out.println("boo1");
					return 0;
				}
			}
//			System.out.println("===================:" + t1 + ":" + t2 + " boo1");
		}
		System.out.println("===================:" + t1 + ":" + t2 + " boo2");
		return 0;
	}

	public float getRatio(String c1, String c2) {
		if (ratioMap.containsKey(c1) && ratioMap.get(c1).containsKey(c2)) {
//			System.out.println(String.format("Getting ratio for %s -> %s: %s", c1, c2, ratioMap.get(c1).get(c2)));
			return ratioMap.get(c1).get(c2);
		} else {
			if (!c1.split("_")[0].equals(c2.split("_")[0]) && !c1.split("_")[1].equals(c2.split("_")[1])) {
				// normal
				return 0;
			} else {
				System.err.println("Cant find " + c1 + " -> " + c2);
			}
		}

		return 0;
	}

	public HashMap<String, HashMap<String, Float>> getRatioMap() {
		return ratioMap;
	}

	public void setRatioMap(HashMap<String, HashMap<String, Float>> ratioMap) {
		this.ratioMap = ratioMap;
	}

}
