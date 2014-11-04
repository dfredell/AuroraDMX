package com.AuroraByteSoftware.AuroraDMX;

import java.util.Comparator;

public class CueSorter implements Comparator<CueObj>{

	@Override
	public int compare(CueObj lhs, CueObj rhs) {
		return (int) (lhs.getCueNum()*100.0-rhs.getCueNum()*100.0);
	}

}
