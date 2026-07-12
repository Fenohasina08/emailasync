package com.example.demo.endpoint.rest.controller.subscription;

import com.example.demo.service.subscription.SubscriptionService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SubscriptionController {
  private final SubscriptionService subscriptionService;

  @PostMapping("/subscriptions")
  public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest request) {
    subscriptionService.subscribe(request.userId(), request.courseId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public record SubscriptionRequest(UUID userId, UUID courseId) {}
}
