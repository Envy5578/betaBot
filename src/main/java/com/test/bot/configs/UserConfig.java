package com.test.bot.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.test.bot.bots.UserBot;

@Configuration
@PropertySource("classpath:application.properties")
public class UserConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(UserBot userBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(userBot);
        return api;
    }

    
}