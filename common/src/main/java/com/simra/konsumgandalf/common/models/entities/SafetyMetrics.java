package com.simra.konsumgandalf.common.models.entities;

import com.simra.konsumgandalf.common.constants.DangerousColors;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.ColumnDefault;

/**
 * This entity represents the safety metrics of an object
 */
@MappedSuperclass
public abstract class SafetyMetrics<T extends SafetyMetrics<T>> {
	@Id
	@Column(length = 21)
	@Enumerated(EnumType.STRING)
	private TrafficTimes trafficTime;

	@Id
	@Column(length = 12)
	@Enumerated(EnumType.STRING)
	private WeekDays weekDay;

	@Column(length = 7)
	private String dangerousColor = DangerousColors.NEUTRAL_200;

	@Column()
	@ColumnDefault("2000")
	private int year;

	/**
	 * The total number of rides registered here
	 */
	@Column
	private int numberOfRides;

	/**
	 * The total number of incidents occurred here
	 */
	@Column
	private int numberOfIncidents;

	/**
	 * The number of incidents that were scary
	 */
	@Column
	private int numberOfScaryIncidents;

	/**
	 * The calculated risk to the rider to drive on this path
	 */
	@Column
	private float dangerousScore;

	@Column
	private int numberOfClosePasses;

	@Column
	private int numberOfPullInOuts;

	@Column
	private int numberOfNearLeftRightHooks;

	@Column
	private int numberOfHeadOnApproaches;

	@Column
	private int numberOfTailgating;

	@Column
	private int numberOfNearDoorings;

	@Column
	private int numberOfObstacleDodges;

	public SafetyMetrics() {
		this.numberOfRides = 0;
		this.numberOfIncidents = 0;
		this.numberOfScaryIncidents = 0;
		this.dangerousScore = 0;
		this.numberOfClosePasses = 0;
		this.numberOfPullInOuts = 0;
		this.numberOfNearLeftRightHooks = 0;
		this.numberOfHeadOnApproaches = 0;
		this.numberOfTailgating = 0;
		this.numberOfNearDoorings = 0;
		this.numberOfObstacleDodges = 0;
	}

	public SafetyMetrics(TrafficTimes trafficTime, WeekDays weekDays) {
		this.numberOfRides = 0;
		this.numberOfIncidents = 0;
		this.numberOfScaryIncidents = 0;
		this.dangerousScore = 0;
		this.numberOfClosePasses = 0;
		this.numberOfPullInOuts = 0;
		this.numberOfNearLeftRightHooks = 0;
		this.numberOfHeadOnApproaches = 0;
		this.numberOfTailgating = 0;
		this.numberOfNearDoorings = 0;
		this.numberOfObstacleDodges = 0;
	}

	public SafetyMetrics(int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents, float dangerousScore,
									  int numberOfClosePasses, int numberOfPullInOuts, int numberOfNearLeftRightHooks,
									  int numberOfHeadOnApproaches, int numberOfTailgating, int numberOfNearDoorings, int numberOfObstacleDodges,
									  String dangerousColor) {
		this.numberOfRides = numberOfRides;
		this.numberOfIncidents = numberOfIncidents;
		this.numberOfScaryIncidents = numberOfScaryIncidents;
		this.dangerousScore = dangerousScore;
		this.numberOfClosePasses = numberOfClosePasses;
		this.numberOfPullInOuts = numberOfPullInOuts;
		this.numberOfNearLeftRightHooks = numberOfNearLeftRightHooks;
		this.numberOfHeadOnApproaches = numberOfHeadOnApproaches;
		this.numberOfTailgating = numberOfTailgating;
		this.numberOfNearDoorings = numberOfNearDoorings;
		this.numberOfObstacleDodges = numberOfObstacleDodges;
		this.dangerousColor = dangerousColor;
	}

	public SafetyMetrics(TrafficTimes trafficTime, WeekDays weekDay, int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents,
									  int numberOfClosePasses, int numberOfPullInOuts, int numberOfNearLeftRightHooks,
									  int numberOfHeadOnApproaches, int numberOfTailgating, int numberOfNearDoorings, int numberOfObstacleDodges) {
		this.trafficTime = trafficTime;
		this.weekDay = weekDay;
		this.numberOfRides = numberOfRides;
		this.numberOfIncidents = numberOfIncidents;
		this.numberOfScaryIncidents = numberOfScaryIncidents;
		this.numberOfClosePasses = numberOfClosePasses;
		this.numberOfPullInOuts = numberOfPullInOuts;
		this.numberOfNearLeftRightHooks = numberOfNearLeftRightHooks;
		this.numberOfHeadOnApproaches = numberOfHeadOnApproaches;
		this.numberOfTailgating = numberOfTailgating;
		this.numberOfNearDoorings = numberOfNearDoorings;
		this.numberOfObstacleDodges = numberOfObstacleDodges;
	}

	public int getNumberOfRides() {
		return numberOfRides;
	}

	public void setNumberOfRides(int numberOfRides) {
		this.numberOfRides = numberOfRides;
	}

	public int getNumberOfIncidents() {
		return numberOfIncidents;
	}

	public void setNumberOfIncidents(int numberOfIncidents) {
		this.numberOfIncidents = numberOfIncidents;
	}

	public int getNumberOfScaryIncidents() {
		return numberOfScaryIncidents;
	}

	public void setNumberOfScaryIncidents(int numberOfScaryIncidents) {
		this.numberOfScaryIncidents = numberOfScaryIncidents;
	}

	public float getDangerousScore() {
		return dangerousScore;
	}

	public void setDangerousScore(float dangerousScore) {
		this.dangerousScore = dangerousScore;
	}

	public int getNumberOfClosePasses() {
		return numberOfClosePasses;
	}

	public void setNumberOfClosePasses(int numberOfClosePasses) {
		this.numberOfClosePasses = numberOfClosePasses;
	}

	public int getNumberOfPullInOuts() {
		return numberOfPullInOuts;
	}

	public void setNumberOfPullInOuts(int numberOfPullInOuts) {
		this.numberOfPullInOuts = numberOfPullInOuts;
	}

	public int getNumberOfNearLeftRightHooks() {
		return numberOfNearLeftRightHooks;
	}

	public void setNumberOfNearLeftRightHooks(int numberOfNearLeftRightHooks) {
		this.numberOfNearLeftRightHooks = numberOfNearLeftRightHooks;
	}

	public int getNumberOfHeadOnApproaches() {
		return numberOfHeadOnApproaches;
	}

	public void setNumberOfHeadOnApproaches(int numberOfHeadOnApproaches) {
		this.numberOfHeadOnApproaches = numberOfHeadOnApproaches;
	}

	public int getNumberOfTailgating() {
		return numberOfTailgating;
	}

	public void setNumberOfTailgating(int numberOfTailgating) {
		this.numberOfTailgating = numberOfTailgating;
	}

	public int getNumberOfNearDoorings() {
		return numberOfNearDoorings;
	}

	public void setNumberOfNearDoorings(int numberOfNearDoorings) {
		this.numberOfNearDoorings = numberOfNearDoorings;
	}

	public int getNumberOfObstacleDodges() {
		return numberOfObstacleDodges;
	}

	public void setNumberOfObstacleDodges(int numberOfObstacleDodges) {
		this.numberOfObstacleDodges = numberOfObstacleDodges;
	}

	public String getDangerousColor() {
		return dangerousColor;
	}

	public void setDangerousColor(String dangerousColor) {
		this.dangerousColor = dangerousColor;
	}

	public T addUpSafetyMetric(
			T newSafetyMetric) {
		if (newSafetyMetric == null) {
			return (T) this;
		}

		this.numberOfRides += newSafetyMetric.getNumberOfRides();
		this.numberOfIncidents += newSafetyMetric.getNumberOfIncidents();
		this.numberOfScaryIncidents += newSafetyMetric.getNumberOfScaryIncidents();
		this.numberOfClosePasses += newSafetyMetric.getNumberOfClosePasses();
		this.numberOfPullInOuts += newSafetyMetric.getNumberOfPullInOuts();
		this.numberOfNearLeftRightHooks += newSafetyMetric.getNumberOfNearLeftRightHooks();
		this.numberOfHeadOnApproaches += newSafetyMetric.getNumberOfHeadOnApproaches();
		this.numberOfTailgating += newSafetyMetric.getNumberOfTailgating();
		this.numberOfNearDoorings += newSafetyMetric.getNumberOfNearDoorings();
		this.numberOfObstacleDodges += newSafetyMetric.getNumberOfObstacleDodges();
		return (T) this;
	}

	public TrafficTimes getTrafficTime() {
		return trafficTime;
	}

	public void setTrafficTime(TrafficTimes trafficTime) {
		this.trafficTime = trafficTime;
	}

	public WeekDays getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WeekDays weekDay) {
		this.weekDay = weekDay;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
