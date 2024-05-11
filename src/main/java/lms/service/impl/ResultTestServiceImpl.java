package lms.service.impl;

import lms.repository.ResultTestRepository;
import lms.service.ResultTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class ResultTestServiceImpl implements ResultTestService {
    private final ResultTestRepository resultTestRepository;
}
