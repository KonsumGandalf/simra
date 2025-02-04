package com.simra.konsumgandalf.common.constants;

/**
 * Defines reusable cron expressions.
 */
public final class CronExpressions {

	public static final String EVERY_DAY = "0 0 0 * * ?";

	public static final String EVERY_MINUTE = "0 * * ? * *";

	public static final String EVERY_10_SECONDS = "0/10 * * ? * *";

	public static final String EVERY_HOUR = "0 0 * ? * *";

}
