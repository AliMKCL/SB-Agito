package com.agito.staj.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final Locale LOCALE_TR = Locale.forLanguageTag("tr");

    /**
     * At startup, this gets executed once and loads the default and supported locales to the resolver.
     * At request time, Spring calls localeResolver.resolveLocale(request).
     * Inspects the locales in the Accept-Language header, matches against the supported locales, applies it.
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(DEFAULT_LOCALE);
        resolver.setSupportedLocales(Arrays.asList(DEFAULT_LOCALE, LOCALE_TR));
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setDefaultLocale(DEFAULT_LOCALE);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    /**
     * Used in input validation messages.
     * When a constraint fails, validator queries the MessageSource bean using the current locale from LocaleContextHolder
     * @param messageSource
     * @return
     */
    @Bean
    @Primary
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Override
    public Validator getValidator() {
        return validator(messageSource());
    }
}
