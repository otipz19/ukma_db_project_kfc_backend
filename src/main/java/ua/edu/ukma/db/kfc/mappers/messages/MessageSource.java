package ua.edu.ukma.db.kfc.mappers.messages;

import java.util.Locale;

public interface MessageSource {

    String getMessage(String code);

    String getMessage(String code, Locale locale);
}
