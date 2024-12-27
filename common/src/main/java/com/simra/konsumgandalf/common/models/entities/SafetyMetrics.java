package com.simra.konsumgandalf.common.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * This entity represents the safety metrics of a ride.
 */
@Entity
public class SafetyMetrics {

	/**
	 * The unique identifier of this entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

}
