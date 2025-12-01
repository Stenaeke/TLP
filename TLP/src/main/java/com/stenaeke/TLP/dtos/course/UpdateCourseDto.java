package com.stenaeke.TLP.dtos.course;

import com.stenaeke.TLP.domain.Course;

public sealed interface UpdateCourseDto permits UpdateCourseTitleDto, UpdateCourseDescriptionDto {
}
