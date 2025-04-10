package ua.edu.ukma.db.kfc.mappers.messages;

import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class ResourceBundleMessageSource implements MessageSource {

    private final String baseName;

    @Override
    public String getMessage(String code) {
        return getMessage(code, Locale.ROOT);
    }

    @Override
    public String getMessage(String code, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        try {
            return bundle.getString(code);
        } catch (MissingResourceException e) {
            return code;
        }
    }
}
