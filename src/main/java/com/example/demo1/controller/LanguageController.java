package com.example.demo1.controller;

import com.example.demo1.controller.exception.BadInputParameterException;
import com.example.demo1.service.LocaleService;
import com.example.demo1.util.Controller;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.EmptyResponse;
import com.example.demo1.util.response.IntoResponse;

import java.util.Locale;
import java.util.regex.Pattern;

@ControllerMapping("/language")
public class LanguageController extends Controller {
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("/app/language/(\\S\\S)");

    @PostMapping("/\\S\\S")
    public IntoResponse chooseLanguage(RequestContext ctx) throws BadInputParameterException {
        final var matcher = LANGUAGE_PATTERN.matcher(ctx.getUrl());

        String language;
        if (matcher.find()) {
            language = matcher.group(1);
        } else {
            throw new BadInputParameterException("expected language");
        }

        Locale locale;
        if (language.equals("ru")) {
            locale = Locale.of("ru");
        } else {
            locale = Locale.of("en");
        }

        LocaleService.setLocaleCookie(ctx.getResponse(), locale);

        return EmptyResponse.OK;
    }
}
