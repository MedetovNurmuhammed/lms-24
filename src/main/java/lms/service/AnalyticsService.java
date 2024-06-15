package lms.service;

import lms.dto.response.DataResponses;

import java.util.List;

public interface AnalyticsService {
    List<DataResponses> getAllAnalyticsCount();
}
