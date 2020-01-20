package ru.motrichkin.cloudstorage.utils.processing;

import ru.motrichkin.cloudstorage.utils.messages.AbstractMessage;

public class MessageProcessingResult {

    private AbstractMessage answerMessage = null;

    private String newToken = null;

    public MessageProcessingResult() {}

    public MessageProcessingResult(AbstractMessage answerMessage) {
        setAnswerMessage(answerMessage);
    }

    public void setAnswerMessage(AbstractMessage answerMessage) {
        this.answerMessage = answerMessage;
    }

    public AbstractMessage getAnswerMessage() {
        return this.answerMessage;
    }

    public String getNewToken() {
        return newToken;
    }

    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

}
