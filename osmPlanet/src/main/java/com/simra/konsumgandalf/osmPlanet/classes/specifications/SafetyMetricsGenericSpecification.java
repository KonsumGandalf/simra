package com.simra.konsumgandalf.osmPlanet.classes.specifications;

import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SafetyMetricsGenericSpecification {

	/**
	 * Creates a generic specification for safety metrics filtering.
	 * @param joinField The name of the join property (e.g. "region" or "planetOsmLine")
	 * @param id An id prefix to filter the joined entity's "id"
	 * @param name A name pattern to filter the joined entity's "name"
	 * @param highwayType List of highway types for the joined entity
	 * @param minDangerousScore Minimum dangerousScore on the safety metrics
	 * @param maxDangerousScore Maximum dangerousScore on the safety metrics
	 * @param minNumberOfRides Minimum number of rides
	 * @param minNumberOfIncidents Minimum number of incidents
	 * @param trafficTime Allowed traffic times
	 * @param weekDay Allowed week days
	 * @param year Allowed years
	 * @param <T> The type of safety metrics entity
	 * @return A Specification for filtering T
	 */
	public static <T> Specification<T> filterBy(String joinField, Long id, String name, List<String> highwayType,
			Float minDangerousScore, Float maxDangerousScore, Integer minNumberOfRides, Integer minNumberOfIncidents,
			List<TrafficTimes> trafficTime, List<WeekDays> weekDay, List<Integer> year, Geometry regionWay) {

		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			Join<T, ?> join = root.join(joinField);

			if (id != null) {
				predicates.add(cb.like(join.get("id").as(String.class), id + "%"));
			}
			if (highwayType != null && !highwayType.isEmpty()) {
				predicates.add(join.get("highway").in(highwayType));
			}
			if (name != null) {
				predicates.add(cb.like(join.get("name"), "%" + name + "%"));
			}
			if (minDangerousScore != null && maxDangerousScore != null) {
				predicates.add(cb.between(root.get("dangerousScore"), minDangerousScore, maxDangerousScore));
			}
			else if (minDangerousScore != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("dangerousScore"), minDangerousScore));
			}
			else if (maxDangerousScore != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("dangerousScore"), maxDangerousScore));
			}
			if (minNumberOfRides != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("numberOfRides"), minNumberOfRides));
			}
			if (minNumberOfIncidents != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("numberOfIncidents"), minNumberOfIncidents));
			}
			if (trafficTime != null) {
				predicates.add(root.get("trafficTime").in(trafficTime));
			}
			if (weekDay != null) {
				predicates.add(root.get("weekDay").in(weekDay));
			}
			if (year != null) {
				predicates.add(root.get("year").in(year));
			}
			if (regionWay != null) {
				Expression<Object> regionBoundary = cb.literal(regionWay);
				predicates.add(cb.isTrue(cb.function("ST_INTERSECTS", Boolean.class, join.get("way"), regionBoundary)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static <T> Specification<T> filterBy(String joinField, String name, Float minDangerousScore,
			Integer minNumberOfRides, Integer minNumberOfIncidents, List<TrafficTimes> trafficTime,
			List<WeekDays> weekDay, List<Integer> year) {

		return SafetyMetricsGenericSpecification.filterBy(joinField, null, name, null, minDangerousScore, null,
				minNumberOfRides, minNumberOfIncidents, trafficTime, weekDay, year, null);
	}

}
