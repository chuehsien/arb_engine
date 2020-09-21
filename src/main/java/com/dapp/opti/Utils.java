package com.dapp.opti;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
	public static final Random random = new Random(0);

	public static Long getRandomId() {
		return Math.abs(random.nextLong() % 999999999);
	}
}
