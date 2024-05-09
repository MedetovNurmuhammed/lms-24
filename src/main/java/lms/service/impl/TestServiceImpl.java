package lms.service.impl;

import lms.dto.response.SimpleResponse;
import lms.repository.TestRepository;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;

    @Override
    public SimpleResponse createTest() {
        return null;
    }
}
