package com.example.gamescrud.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FireBaseGameContract {
    private CollectionReference gamesRef;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;

    public FireBaseGameContract(){
        String PATH = "games";
        if(firebaseFirestore ==null){
            firebaseFirestore = FirebaseFirestore.getInstance();
            storageRef = FirebaseStorage.getInstance().getReference();
        }
        gamesRef = firebaseFirestore.collection(PATH);

    }

    public void setPath(String path){
        gamesRef = firebaseFirestore.collection(path);
    }

    public void createGame(GameModel game, Uri imageUri,OnSuccessListener<List<Object>> success ){
        game.setImagePath(UUID.randomUUID().toString());
        Tasks.whenAllSuccess(gamesRef.add(game), uploadImage(imageUri, game.getImagePath())).addOnSuccessListener(success);
    }

    public void updateGame(@NonNull GameModel game, Uri imageUri,OnSuccessListener<List<Object>> success){
        if(imageUri !=null){
            Log.d("PATH", game.getImagePath());
            Tasks.whenAllSuccess(gamesRef.document(game.getId()).set(game)).addOnSuccessListener(success);

//            Tasks.whenAllSuccess(gamesRef.document(game.getId()).set(game), uploadImage(imageUri, game.getImagePath())).addOnSuccessListener(success);
        }
    }

    public void deleteGame(String id, OnSuccessListener<Void> success){

        this.gamesRef.document(id).delete().addOnSuccessListener(success);
    }

    public void getGames(EventListener<QuerySnapshot> eventListener){
        gamesRef.addSnapshotListener(eventListener);
    }

    public StorageReference getImage(String path) throws IOException {
        return this.storageRef.child(path);
    }

    public UploadTask uploadImage(Uri image, String path){
        StorageReference ref = this.storageRef.child(path);
        return ref.putFile(image);
    }


}
