package lms.dto.response;

import lms.enums.StudyFormat;
import lombok.Builder;

@Builder
public record StudentResponse(
  String fullName,
  String phoneNumber,
  String email,
  String groupName,
  StudyFormat studyFormat
){}
