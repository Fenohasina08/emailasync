package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SubscriptionConfirmed;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubscriptionConfirmedService implements Consumer<SubscriptionConfirmed> {
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(SubscriptionConfirmed event) {
    var to = new InternetAddress(event.getTo());
    var subject = "Confirmation d'inscription - " + event.getCourseTitle();
    var body =
        "<p>Bonjour "
            + event.getFirstName()
            + ", votre inscription au cours <b>"
            + event.getCourseTitle()
            + "</b> est confirmée.</p>";
    mailer.accept(new Email(to, List.of(), List.of(), subject, body, List.of()));
  }
}
