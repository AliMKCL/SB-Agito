package com.agito.staj.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class Translator {

    private static MessageSource messageSource;

    public Translator(MessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    /**
     * Resolves the localized message for the current request's locale using LocaleContextHolder.
     *
     * @param msgKey Message key defined in messages.properties
     * @param args   Dynamic arguments to interpolate ({0}, {1}, etc.)
     * @return Localized string
     */
    public static String toLocale(String msgKey, Object... args) {
        Locale locale = LocaleContextHolder.getLocale(); // Get the locale from LocaleContextHolder
        return toLocale(msgKey, locale, args);
    }

    /**
     * Resolves the localized message for a specified locale.
     *
     * @param msgKey Message key defined in messages.properties
     * @param locale Target locale
     * @param args   Dynamic arguments to interpolate ({0}, {1}, etc.)
     * @return Localized string
     */
    public static String toLocale(String msgKey, Locale locale, Object... args) {
        if (messageSource == null) {
            if (args != null && args.length > 0) {
                try {
                    return MessageFormat.format(msgKey, args);
                } catch (Exception ignored) {
                    return msgKey;
                }
            }
            return msgKey;
        }

        try {
            return messageSource.getMessage(msgKey, args, locale != null ? locale : Locale.ENGLISH);
        } catch (NoSuchMessageException e) { // No message with key in the language resource bundle.
            if (args != null && args.length > 0) {
                try {
                    return MessageFormat.format(msgKey, args); // Retrieve the correct message
                } catch (Exception ignored) {
                    return msgKey;
                }
            }
            return msgKey;
        }
    }
}
