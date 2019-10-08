package org.sapphon.upwise.time;

import java.sql.Timestamp;
import java.time.Instant;

public class TimeLord {
	public static Timestamp getNow(){
		Instant now = Instant.now();
		Timestamp timeStamp = new Timestamp(now.toEpochMilli());
		timeStamp.setNanos(now.getNano());
		return timeStamp;
	}

	public static Timestamp getNowWithOffset(long offset){
		Instant now = Instant.now();
		Timestamp timeStamp = new Timestamp(now.toEpochMilli() + offset);
		timeStamp.setNanos(now.getNano());
		return timeStamp;
	}

	public static Timestamp getTimestampForMillis(long millisSinceEpoch){
		return new Timestamp(millisSinceEpoch);
	}
}
