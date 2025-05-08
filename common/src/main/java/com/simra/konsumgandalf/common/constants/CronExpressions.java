package com.simra.konsumgandalf.common.constants;

/**
 * Defines reusable cron expressions.
 */
public final class CronExpressions {

	public static final String EVERY_15_MINUTES = "0 0/15 * ? * *";

	// Every hour on the hour (e.g., 01:00, 02:00, ...)
	public static final String EVERY_HOUR = "0 0 * ? * *";

	// Every day at 00:10 (10 minutes after midnight)
	public static final String EVERY_DAY = "0 10 0 * * ?";

	// Every Monday at 00:30 (30 minutes after midnight)
	public static final String EVERY_WEEK = "0 30 0 ? * MON";

}
