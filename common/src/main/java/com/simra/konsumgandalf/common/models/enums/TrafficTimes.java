package com.simra.konsumgandalf.common.models.enums;

/**
 * This enum represents the different traffic times of the day based on general traffic
 * times and rush hours in Berlin.
 *
 * @see <a href=
 * "https://www.researchgate.net/figure/Segmentation-of-day-time-24-h-into-time-segments-Times-of-the-Day_tbl1_316668245">Segmentation
 * of day-time into time segments</a>
 * @see <a href="https://www.bcdtravel.com/blog/a-business-travelers-guide-to-berlin/">A
 * Business Traveler's Guide to Berlin</a>
 * @see <a href="https://www.essen.de/leben/mobilitaet/verkehrserhebung.de.html">Essen
 * Traffic Survey</a>
 * @see <a href=
 * "https://www.berlin.de/sen/uvk/_assets/verkehr/verkehrsmanagement/verkehrserhebungen/ergebnisbericht-2019-teil-a.pdf">Berlin
 * Traffic Report 2019</a>
 */
public enum TrafficTimes {

	/**
	 * Includes all hours of the day.
	 */
	ALL_DAY,

	/**
	 * 7:30 - 9:59
	 */
	EARLY_RUSH_HOUR,

	/**
	 * 10:00 - 15:29
	 */
	MID_DAY,

	/**
	 * 15:30 - 18:59
	 */
	LATE_RUSH_HOUR,

	/**
	 * 19:00 - 7:29
	 */
	EVENING_NIGHT_MORNING,

}
