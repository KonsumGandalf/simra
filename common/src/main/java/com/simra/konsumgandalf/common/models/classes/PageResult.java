package com.simra.konsumgandalf.common.models.classes;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements) {
}
