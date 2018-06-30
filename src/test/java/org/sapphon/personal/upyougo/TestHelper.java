package org.sapphon.personal.upyougo;

import java.util.List;
import static org.junit.Assert.*;

public class TestHelper {
	public static void assertListEquals(List list1, List list2){
		assertArrayEquals(list1.toArray(), list2.toArray());
	}
}
