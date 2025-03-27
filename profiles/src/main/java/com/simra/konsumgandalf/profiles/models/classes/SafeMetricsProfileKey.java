package com.simra.konsumgandalf.profiles.models.classes;

import com.simra.konsumgandalf.common.models.enums.SafetyMetricsProfileGroup;

public record SafeMetricsProfileKey(SafetyMetricsProfileGroup group, String groupKey) {
}
