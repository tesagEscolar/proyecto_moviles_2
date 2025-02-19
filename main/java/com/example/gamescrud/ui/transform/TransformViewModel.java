package com.example.gamescrud.ui.transform;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gamescrud.models.FireBaseGameContract;
import com.example.gamescrud.models.GameModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<List<GameModel>> mTexts;

    public TransformViewModel() {
        mTexts = new MutableLiveData<>();


        FireBaseGameContract fbgc = new FireBaseGameContract();

        fbgc.getGames((value, error) -> {
            List<GameModel> games = new ArrayList<>();
            GameModel tempGame;
            for(QueryDocumentSnapshot doc: value){
                if(doc!=null){
                    tempGame = doc.toObject(GameModel.class);
                    tempGame.setId(doc.getId());
                    games.add(tempGame);
                }
            }
            mTexts.postValue(games);
        });



    }

    public LiveData<List<GameModel>> getTexts() {
        return mTexts;
    }
}