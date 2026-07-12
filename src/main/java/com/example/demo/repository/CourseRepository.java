package com.example.demo.repository;

import com.example.demo.entity.Course;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, UUID> {}