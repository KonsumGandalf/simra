package com.simra.konsumgandalf.osmPlanet.classes.keys;

import com.simra.konsumgandalf.common.models.entities.SimraRegion;
import com.simra.konsumgandalf.common.models.enums.TrafficTimes;
import com.simra.konsumgandalf.common.models.enums.WeekDays;

public class RegionTrafficTimeWeekDayKey extends TrafficTimeWeekDayKey {

	private SimraRegion region;

	public RegionTrafficTimeWeekDayKey(SimraRegion region, TrafficTimes trafficTime, WeekDays weekDay, Integer year) {
		super(trafficTime, weekDay, year);
		this.region = region;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RegionTrafficTimeWeekDayKey that = (RegionTrafficTimeWeekDayKey) o;
		return super.equals(o) && region.getName().equals(that.region.getName());
	}

	@Override
	public int hashCode() {
		return super.hashCode() + region.getName().hashCode();
	}

	public SimraRegion getSimraRegion() {
		return region;
	}

}
