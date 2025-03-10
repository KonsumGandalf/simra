package com.simra.konsumgandalf.common.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.simra.konsumgandalf.common.models.classes.SafetyMetricsIDKey;
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
 * This entity represents the safety metrics of a ride.
 */
@Entity
@Table(indexes = {
		@Index(name = "idx_sm_line_traffic_time_week_day", columnList = "trafficTime, weekDay"),
		@Index(name = "idx_sm_line_safety_metrics_dangerous_score", columnList = "dangerousScore DESC, trafficTime, weekDay")
})
@IdClass(SafetyMetricsIDKey.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "osmId")
public class SafetyMetricsPlanetOsmLine extends SafetyMetrics<SafetyMetricsPlanetOsmLine> {

	@Id
	@Column(name = "osm_id")
	private Long osmId;

	/**
	 * The {@link PlanetOsmLine} entity that is associated with this safety metrics
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private PlanetOsmLine planetOsmLine;

	public SafetyMetricsPlanetOsmLine() {
		super();
	}

	public SafetyMetricsPlanetOsmLine(TrafficTimes trafficTime, WeekDays weekDays) {
		super(trafficTime, weekDays);
	}

	public SafetyMetricsPlanetOsmLine(int numberOfRides, int numberOfIncidents, int numberOfScaryIncidents, float dangerousScore,
									  int numberOfClosePasses, int numberOfPullInOuts, int numberOfNearLeftRightHooks,
									  int numberOfHeadOnApproaches, int numberOfTailgating, int numberOfNearDoorings, int numberOfObstacleDodges,
									  String dangerousColor) {
		super(numberOfRides, numberOfIncidents, numberOfScaryIncidents, dangerousScore, numberOfClosePasses, numberOfPullInOuts,
				numberOfNearLeftRightHooks, numberOfHeadOnApproaches, numberOfTailgating, numberOfNearDoorings, numberOfObstacleDodges,
				dangerousColor);
	}

	public PlanetOsmLine getPlanetOsmLine() {
		return planetOsmLine;
	}

	public void setPlanetOsmLine(PlanetOsmLine planetOsmLine) {
		this.planetOsmLine = planetOsmLine;
	}

	public Long getOsmId() {
		return osmId;
	}

	public void setOsmId(Long planetOsmLineId) {
		this.osmId = planetOsmLineId;
	}
}
