package com.example.demo.service.subscription;

import com.example.demo.entity.Course;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import jakarta.mail.internet.InternetAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
  private final Mailer mailer;

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
    sendConfirmationEmail(user, course);
  }

  @SneakyThrows
  private void sendConfirmationEmail(User user, Course course) {
    var to = new InternetAddress(user.getEmail());
    var subject = "Confirmation d'inscription - " + course.getTitle();
    var body =
        "<p>Bonjour "
            + user.getFirstName()
            + ", votre inscription au cours <b>"
            + course.getTitle()
            + "</b> est confirmée.</p>";
    mailer.accept(new Email(to, List.of(), List.of(), subject, body, List.of()));
  }
}
