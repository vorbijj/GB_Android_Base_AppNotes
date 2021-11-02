package com.example.gb_android_base_appnotes.data;


import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class CardNoteMapping {
    public static class Fields {
        public final static String TITLE = "title";
        public final static String DATE = "date";
        public final static String DESCRIPTION = "description";
        public final static String LIKE = "like";
    }

    public static CardNote toCardNote(String id, Map<String, Object> doc) {
        Timestamp timeStamp = (Timestamp) doc.get(Fields.DATE);
        CardNote answer = new CardNote((String) doc.get(Fields.TITLE),
                timeStamp.toDate(),
                (String) doc.get(Fields.DESCRIPTION),
                (boolean) doc.get(Fields.LIKE));
        answer.setId(id);
        return answer;
    }

    public static Map<String, Object> toDocument(CardNote cardNote) {
        Map<String, Object> answer = new HashMap<>();
        answer.put(Fields.TITLE, cardNote.getTitle());
        answer.put(Fields.DESCRIPTION, cardNote.getDescription());
        answer.put(Fields.LIKE, cardNote.isLike());
        answer.put(Fields.DATE, cardNote.getDate());
        return answer;
    }
}
