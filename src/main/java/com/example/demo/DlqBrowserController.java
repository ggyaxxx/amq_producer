package com.example.demo;

import jakarta.jms.*;
import org.springframework.beans.factory.annotation.Autowired; // Per l'iniezione
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Enumeration;

@RestController
public class DlqBrowserController {

    private static final Logger log = LoggerFactory.getLogger(DlqBrowserController.class);


    @Autowired
    private ConnectionFactory connectionFactory;

    @GetMapping("/browse-queue")
    public ResponseEntity<String> browseDlq(@RequestParam(defaultValue = "MyQUEUE") String queueName) {

        log.info("Browsing queue '{}'", queueName);

        int counter = 0;
        StringBuilder sb = new StringBuilder();

        // Usiamo la factory iniettata dal contesto
        // Nota: non serve passare user/pass qui, sono già dentro la factory grazie allo YAML
        try (Connection connection = connectionFactory.createConnection()) {

            connection.start();

            // Sessione non transazionale, auto-ack (per sola lettura va bene)
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

                Queue queue = session.createQueue(queueName);

                // Il browser permette di guardare senza consumare
                try (QueueBrowser browser = session.createBrowser(queue)) {
                    Enumeration<?> e = browser.getEnumeration();

                    while (e.hasMoreElements()) {
                        Message m = (Message) e.nextElement();
                        counter++;

                        // Esempio di estrazione dati sicuro
                        String correlationId = m.getJMSCorrelationID();
                        // Controllo se la proprietà esiste per evitare null pointer (opzionale ma consigliato)
                        String e2eId = m.getStringProperty("E2EID");

                        sb.append(String.format(
                                "Message %d: JMSCorrelationID=%s, E2EID=%s%n",
                                counter,
                                correlationId,
                                (e2eId != null ? e2eId : "N/A")
                        ));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error while browsing queue {}", queueName, ex);
            return ResponseEntity.internalServerError().body("Error: " + ex.getMessage());
        }

        if (counter == 0) {
            sb.append("No messages found in ").append(queueName);
        } else {
            sb.append("\nTotal messages browsed: ").append(counter);
        }

        return ResponseEntity.ok(sb.toString());
    }
}