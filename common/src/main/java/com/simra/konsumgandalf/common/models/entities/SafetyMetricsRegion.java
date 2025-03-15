package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.simra.konsumgandalf.common.models.classes.SafetyMetricsIDKey;
import com.simra.konsumgandalf.common.models.classes.SafetyMetricsNameKey;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.*;

/**
 * This entity represents the safety metrics of a region.
 */
@Entity
@Table(indexes = { @Index(name = "idx_sm_region_traffic_time_week_day_year", columnList = "trafficTime, weekDay, year"),
		@Index(name = "idx_sm_region_safety_metrics_dangerous_score",
				columnList = "dangerousScore DESC, trafficTime, weekDay, year") })
@IdClass(SafetyMetricsNameKey.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class SafetyMetricsRegion extends SafetyMetrics<SafetyMetricsRegion> {

	@Id
	@Column
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Region region;

	@Column
	private Float totalDistance;

	public SafetyMetricsRegion() {
		super();
	}

	public SafetyMetricsRegion(Float totalDistance, TrafficTimes trafficTime, WeekDays weekDay, Integer year,
			int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents, int numberOfClosePasses,
			int numberOfPullInOuts, int numberOfNearLeftRightHooks, int numberOfHeadOnApproaches,
			int numberOfTailgating, int numberOfNearDoorings, int numberOfObstacleDodges) {
		super(trafficTime, weekDay, year, numberOfRides, numberOfIncidents, numberOfScaryIncidents, numberOfClosePasses,
				numberOfPullInOuts, numberOfNearLeftRightHooks, numberOfHeadOnApproaches, numberOfTailgating,
				numberOfNearDoorings, numberOfObstacleDodges);
		this.totalDistance = totalDistance;
	}

	@Override
	public SafetyMetricsRegion addUpSafetyMetric(SafetyMetricsRegion newSafetyMetric) {
		SafetyMetricsRegion combined = new SafetyMetricsRegion();

		combined.setTrafficTime(newSafetyMetric.getTrafficTime());
		combined.setWeekDay(newSafetyMetric.getWeekDay());
		combined.setYear(newSafetyMetric.getYear());

		combined.setNumberOfRides(this.getNumberOfRides() + newSafetyMetric.getNumberOfRides());
		combined.setNumberOfIncidents(this.getNumberOfIncidents() + newSafetyMetric.getNumberOfIncidents());
		combined
			.setNumberOfScaryIncidents(this.getNumberOfScaryIncidents() + newSafetyMetric.getNumberOfScaryIncidents());
		combined.setNumberOfClosePasses(this.getNumberOfClosePasses() + newSafetyMetric.getNumberOfClosePasses());
		combined.setNumberOfPullInOuts(this.getNumberOfPullInOuts() + newSafetyMetric.getNumberOfPullInOuts());
		combined.setNumberOfNearLeftRightHooks(
				this.getNumberOfNearLeftRightHooks() + newSafetyMetric.getNumberOfNearLeftRightHooks());
		combined.setNumberOfHeadOnApproaches(
				this.getNumberOfHeadOnApproaches() + newSafetyMetric.getNumberOfHeadOnApproaches());
		combined.setNumberOfTailgating(this.getNumberOfTailgating() + newSafetyMetric.getNumberOfTailgating());
		combined.setNumberOfNearDoorings(this.getNumberOfNearDoorings() + newSafetyMetric.getNumberOfNearDoorings());
		combined
			.setNumberOfObstacleDodges(this.getNumberOfObstacleDodges() + newSafetyMetric.getNumberOfObstacleDodges());
		combined.setTotalDistance(this.getTotalDistance() + newSafetyMetric.getTotalDistance());

		return combined;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Float totalDistance) {
		this.totalDistance = totalDistance;
	}

}
