package com.simra.konsumgandalf.rides.models.specifications;

import com.simra.konsumgandalf.common.models.entities.RideIncident;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;
import org.springframework.data.jpa.domain.Specification;

public class RideIncidentSpecification {

	public static Specification<RideIncident> filterBy(Long lineId, TrafficTimes trafficTime, WeekDays weekDays,
			int year) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();

			predicates = cb.and(predicates, cb.equal(root.get("planetOsmLine").get("id"), lineId));

			if (trafficTime != null && !trafficTime.equals(TrafficTimes.ALL_DAY)) {
				predicates = cb.and(predicates, cb.equal(root.get("trafficTime"), trafficTime));
			}
			if (weekDays != null && !weekDays.equals(WeekDays.ALL_WEEK)) {
				predicates = cb.and(predicates, cb.equal(root.get("weekDay"), weekDays));
			}
			if (0 < year && year != 2000) {
				predicates = cb.and(predicates, cb.equal(root.get("year"), year));
			}

			return predicates;
		};
	}

}
