package lms.service.impl;

import jakarta.transaction.Transactional;
import lms.dto.response.DataResponses;
import lms.repository.GroupRepository;
import lms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {
    private final GroupRepository groupRepository;
    @Override
    public List<DataResponses> getAllAnalyticsCount() {
        LocalDate currentDate = LocalDate.now();
        List<DataResponses> dataResponses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int year = currentDate.minusYears(i).getYear();
            dataResponses.addAll(groupRepository.getAnalyticsForYear(year));
        }
        dataResponses.sort(Comparator.comparing(DataResponses::getYear));
        return dataResponses;
    }
}

