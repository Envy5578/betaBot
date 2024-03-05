package com.test.bot.bots;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.test.bot.models.UserEntity;
import com.test.bot.repository.UserRepository;

@Component
public class UserBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    @Autowired
    private UserRepository userRepository;
    private UserEntity user = new UserEntity();
    private static final Logger LOG = LoggerFactory.getLogger(UserBot.class);
    // @Autowired
    // private UserBot userBot;
    public UserBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
    @Override
    public String getBotUsername() {
        return "testingBot";
    }
    private Map<Integer, Long> lastMessageTimes = new HashMap<>();
    private static final long MESSAGE_INTERVAL_MS = 10000; // 2 секунды
    public boolean isMessageAllowed(Message message) {
        int userId = message.getChatId().intValue();
        long currentTime = System.currentTimeMillis();

        if (lastMessageTimes.containsKey(userId)) {
            long lastMessageTime = lastMessageTimes.get(userId);
            if (currentTime - lastMessageTime < MESSAGE_INTERVAL_MS) {
                return false; // Сообщение не разрешено, так как прошло меньше 2 секунд с момента предыдущего сообщения
            }
        }
        lastMessageTimes.put(userId, currentTime); // Обновляем время последнего сообщения для пользователя
        return true; // Сообщение разрешено
    }

    @SuppressWarnings("null")
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // System.out.println(update.getMessage().getChatId());
            // System.out.println(userRepository.existsBytgId(update.getMessage().getChatId()));
            if(userRepository.existsBytgId(update.getMessage().getChatId())){
                // startCommand1(update.getMessage().getChatId());
                try{
                    execute(new SendMessage(update.getMessage().getChatId().toString(), "Вы получили уже свой gift"));
        
                }catch(TelegramApiException e){
                    LOG.error("Ошибка отправки сообщения", e);
                }
                
            }else{
                if (isMessageAllowed(update.getMessage())) {
                    switch (update.getMessage().getText()) {
                        case START -> {
                            startCommand(update.getMessage().getChatId());
                            //startCommandText(update.getMessage().getChatId(), update.getMessage().getFrom().getUserName());
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }
    // private void startCommand1(Long chatId) {
    //     try{
    //         execute(new SendMessage(chatId.toString(), "Вы получили уже свой gift"));

    //     }catch(TelegramApiException e){
    //         LOG.error("Ошибка отправки сообщения", e);
    //     }
    // }
    private void startCommand(Long chatId) {
        try {
            user.setTgId(chatId);
            user.setTake(false);
            userRepository.save(user);
            execute(new SendMessage(chatId.toString(), "Ваша рандомная цифра: " + chatId.intValue()));
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        };
    }

    
}