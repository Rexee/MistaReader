package com.mistareader.util;

import com.mistareader.model.Message;
import com.mistareader.model.Reply;

import java.util.ArrayList;
import java.util.List;

public class MessagesUtils {

    private static Message getMessageByN(int n, List<Message> messages) {
        for (Message locMessage : messages) {
            if (locMessage.n == n) {
                return locMessage;
            }
        }
        return null;
    }

    private static boolean isReplyInArray(int n, ArrayList<Reply> replies) {
        for (Reply loceReply : replies) {
            if (loceReply.n == n) {
                return true;
            }
        }
        return false;
    }

    public static void setQuotesInMessages(Message newMessage, List<Message> messages) {
        ArrayList<Integer> repliedTo = newMessage.getRepliedTo();
        if (Empty.is(repliedTo)) {
            return;
        }

        Message locMessage;
        for (int i = 0; i < repliedTo.size(); i++) {
            locMessage = getMessageByN(repliedTo.get(i), messages);
            if (locMessage != null) {
                if (locMessage.getQuote() == null) {
                    locMessage.setQuote(new ArrayList<>(1));
                }

                if (!isReplyInArray(newMessage.n, locMessage.getQuote())) {
                    locMessage.getQuote().add(new Reply(newMessage.id, newMessage.n));
                    locMessage.setQuoteRepresentation(locMessage.getQuoteRepresentation() + " (" + newMessage.n + ")");
                }
            }
        }
    }

    public static ArrayList<Integer> extractReplies(String s) {
        ArrayList<Integer> replies = new ArrayList<>();
        int startPos, endPos, n;

        startPos = s.indexOf("(");

        while (startPos != -1) {
            endPos = s.indexOf(")", startPos + 1);
            if (endPos == -1) {
                break;
            }

            String messNum = s.substring(startPos + 1, endPos);
            if (messNum.length() <= 4 && isNumeric(messNum)) {
                n = Integer.parseInt(messNum);
                replies.add(n);
            }

            startPos = s.indexOf("(", endPos + 1);
        }

        return replies;
    }

    private static boolean isNumeric(String s) {
        if (s == null)
            return false;

        if (s.length() == 0)
            return false;

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }
}
