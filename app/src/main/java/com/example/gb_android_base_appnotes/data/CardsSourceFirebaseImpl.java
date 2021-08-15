package com.example.gb_android_base_appnotes.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardsSourceFirebaseImpl implements CardsSource {
    private static final String CARDS_COLLECTION = "cards";
    private static final String TAG = "[FirebaseImpl]";

    private FirebaseFirestore store = FirebaseFirestore.getInstance();
    private CollectionReference collection = store.collection(CARDS_COLLECTION);

    private List<CardNote> cardsNote = new ArrayList<>();

    @Override
    public CardsSource init(CardsSourceResponse cardsSourceResponse) {

        collection.orderBy(CardNoteMapping.Fields.DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            cardsNote = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Map<String, Object> doc = document.getData();
                                String id = document.getId();
                                CardNote cardNote = CardNoteMapping.toCardNote(id, doc);
                                cardsNote.add(cardNote);
                            }
                            Log.d(TAG, "success " + cardsNote.size() + " qnt");
                            cardsSourceResponse.initialized(CardsSourceFirebaseImpl.this);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "get failed with ", e);
                    }
                });

        return this;
    }

    @Override
    public CardNote getNoteData(int position) {
        return cardsNote.get(position);
    }

    @Override
    public int size() {
        if (cardsNote == null){
            return 0;
        }
        return cardsNote.size();
    }

    @Override
    public void deleteCardNote(int position) {
        collection.document(cardsNote.get(position).getId()).delete();
        cardsNote.remove(position);
    }

    @Override
    public void updateCardNote(int position, CardNote cardNote) {
        String id = cardNote.getId();
        collection.document(id).set(CardNoteMapping.toDocument(cardNote));
    }

    @Override
    public void addCardNote(CardNote cardNote) {
        collection.add(CardNoteMapping.toDocument(cardNote))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        cardNote.setId(documentReference.getId());
                    }
                });
    }

    @Override
    public void clearCardNote() {
        for (CardNote cardNote: cardsNote) {
            collection.document(cardNote.getId()).delete();
        }
        cardsNote = new ArrayList<CardNote>();
    }
}
