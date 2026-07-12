package com.example.demo.service.subscription;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SubscriptionConfirmed;
import com.example.demo.entity.Course;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class SubscriptionService {
  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final EventProducer<SubscriptionConfirmed> eventProducer;

  @Transactional
  public void subscribe(UUID userId, UUID courseId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

    var subscriptionId = new Subscription.SubscriptionId(userId, courseId);
    if (subscriptionRepository.existsById(subscriptionId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already subscribed");
    }

    subscriptionRepository.save(new Subscription(subscriptionId, user, course, Instant.now()));

    eventProducer.accept(
        List.of(
            SubscriptionConfirmed.builder()
                .to(user.getEmail())
                .firstName(user.getFirstName())
                .courseTitle(course.getTitle())
                .build()));
  }
}
