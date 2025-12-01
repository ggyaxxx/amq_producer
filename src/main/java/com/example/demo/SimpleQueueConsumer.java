package com.example.demo; // Il tuo package

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleQueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(SimpleQueueConsumer.class);

    /**
     * Questo è tutto.
     * Spring Boot avvia questo listener automaticamente.
     * Si connette usando i parametri del tuo application.yaml.
     * Rimane in ascolto sulla coda "MyQueue".
     */
    @JmsListener(destination = "MyQueue")
    public void receiveMessage(String message) {

        // Il messaggio arriva già come String (Spring lo converte)
        log.info("✅ MESSAGGIO RICEVUTO: '{}'", message);

        // Qui metti la tua logica (salva su DB, ecc.)

        // Se questo metodo finisce senza errori, Spring manda
        // un "ACK" al broker e il messaggio viene rimosso dalla coda.
    }
}