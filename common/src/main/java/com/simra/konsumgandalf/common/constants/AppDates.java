package com.simra.konsumgandalf.common.constants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class AppDates {

	public static final Date START_OF_RECORDING = Date
		.from(LocalDate.parse("2019-01-01").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

	public static final Date FALLBACK_DATE = Date
		.from(LocalDate.parse("2000-01-01").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

	public static final Long FALLBACK_DATE_MILLIS = FALLBACK_DATE.getTime();

}
