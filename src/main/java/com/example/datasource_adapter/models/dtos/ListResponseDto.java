package com.example.datasource_adapter.models.dtos;

import java.util.List;

public record ListResponseDto<T>(List<T> data, long total, int page, int pageSize) {
}
