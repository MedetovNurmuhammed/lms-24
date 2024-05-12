package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.CheckAnswerRequest;
import lms.dto.response.FilterAnswerOfTaskResponse;
import lms.dto.request.AnswerTaskRequest;
import lms.dto.response.AnswerTaskResponse;
import lms.dto.response.SimpleResponse;
import lms.enums.TaskAnswerStatus;
import java.util.List;

public interface AnswerTaskService {
    SimpleResponse save(Long taskId, AnswerTaskRequest answerTaskRequest) throws MessagingException;

    List<FilterAnswerOfTaskResponse> filterAnswerTask(Long taskId, TaskAnswerStatus answerStatus);

    SimpleResponse update(Long answerTaskId, AnswerTaskRequest answerTaskRequest) throws MessagingException;

    AnswerTaskResponse findAnswerByTaskId(Long taskId);

    AnswerTaskResponse getAnswerById(Long answerId);

    SimpleResponse checkAnswer(Long answerId, CheckAnswerRequest checkAnswerRequest) throws MessagingException;

    List<String> getNotAnswered(Long taskId);
}
