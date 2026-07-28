package com.fintech.wallet.utilities;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Utils {
	
	public static BigDecimal convertToFloatingPoint(BigInteger amount) {
		int scale = 2;
		BigDecimal floatingPoint = new BigDecimal(amount, scale);
		return floatingPoint;
	}
}
