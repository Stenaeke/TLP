package com.stenaeke.TLP.dtos.course;

import com.stenaeke.TLP.domain.Course;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCourseTitleDto implements UpdateCourseDto {
    @NotBlank
    private String title;

    @Override
    public void applyToCourse(Course course) {
        course.setTitle(title);
    }
}
