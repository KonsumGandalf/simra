package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.simra.konsumgandalf.common.models.classes.SafetyMetricsIDKey;
import com.simra.konsumgandalf.common.models.classes.SafetyMetricsNameKey;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * This entity represents the safety metrics of a region.
 */
@Entity
@Table(
		indexes = {
				@Index(name = "idx_sm_simra_region_traffic_time_week_day", columnList = "trafficTime, weekDay"),
				@Index(name = "idx_sm_simra_region_safety_metrics_dangerous_score", columnList = "dangerousScore DESC, trafficTime, weekDay")
		})
@IdClass(SafetyMetricsNameKey.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class SafetyMetricsSimraRegion extends SafetyMetrics<SafetyMetricsSimraRegion> {
	@Id
	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonManagedReference
	private SimraRegion region;

	public SafetyMetricsSimraRegion() {
		super();
	}

	public SafetyMetricsSimraRegion(TrafficTimes trafficTime, WeekDays weekDay, int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents,
									int numberOfClosePasses, int numberOfPullInOuts, int numberOfNearLeftRightHooks,
									int numberOfHeadOnApproaches, int numberOfTailgating, int numberOfNearDoorings, int numberOfObstacleDodges) {
		super(trafficTime, weekDay, numberOfRides, numberOfIncidents, numberOfScaryIncidents,
		numberOfClosePasses, numberOfPullInOuts, numberOfNearLeftRightHooks,
		numberOfHeadOnApproaches, numberOfTailgating, numberOfNearDoorings, numberOfObstacleDodges);
	}

	public SimraRegion getRegion() {
		return region;
	}

	public void setRegion(SimraRegion region) {
		this.region = region;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
