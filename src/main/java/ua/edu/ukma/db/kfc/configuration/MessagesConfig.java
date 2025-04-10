package ua.edu.ukma.db.kfc.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import ua.edu.ukma.db.kfc.mappers.messages.MessageSource;
import ua.edu.ukma.db.kfc.mappers.messages.ResourceBundleMessageSource;

@ApplicationScoped
public class MessagesConfig {

    @Produces
    @ApplicationScoped
    public MessageSource getMessageSource() {
        return new ResourceBundleMessageSource("messages/errors");
    }
}
