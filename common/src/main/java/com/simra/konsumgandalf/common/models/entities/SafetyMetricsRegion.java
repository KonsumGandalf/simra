package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@Table(
		indexes = {
				@Index(name = "idx_sm_region_traffic_time_week_day", columnList = "trafficTime, weekDay"),
				@Index(name = "idx_sm_region_safety_metrics_dangerous_score", columnList = "dangerousScore DESC, trafficTime, weekDay")
		})
@IdClass(SafetyMetricsNameKey.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class SafetyMetricsRegion extends SafetyMetrics<SafetyMetricsRegion> {
	@Id
	@Column
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference
	private Region region;

	public SafetyMetricsRegion() {
		super();
	}

	public SafetyMetricsRegion(TrafficTimes trafficTime, WeekDays weekDay,
							   int numberOfRides, int numberOfIncidents,
							   int numberOfScaryIncidents,
							   int numberOfClosePasses, int numberOfPullInOuts,
							   int numberOfNearLeftRightHooks,
							   int numberOfHeadOnApproaches,
							   int numberOfTailgating, int numberOfNearDoorings,
							   int numberOfObstacleDodges) {
		super(trafficTime, weekDay, numberOfRides, numberOfIncidents,
				numberOfScaryIncidents,
				numberOfClosePasses, numberOfPullInOuts,
				numberOfNearLeftRightHooks,
				numberOfHeadOnApproaches, numberOfTailgating,
				numberOfNearDoorings, numberOfObstacleDodges);
	}

	@Override
	public SafetyMetricsRegion addUpSafetyMetric(
			SafetyMetricsRegion newSafetyMetric) {
		SafetyMetricsRegion combineSafetyMetrics =
				super.addUpSafetyMetric(newSafetyMetric);

		combineSafetyMetrics.setTrafficTime(newSafetyMetric.getTrafficTime());
		combineSafetyMetrics.setWeekDay(newSafetyMetric.getWeekDay());

		return combineSafetyMetrics;
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
}
