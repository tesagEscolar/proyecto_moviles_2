package com.example.gamescrud.models;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

public class GameModel implements Parcelable {
    @PropertyName("name")
    String name;
    @PropertyName("id")
    String id;
    @PropertyName("imagePath")
    String imagePath;
    @PropertyName("gameDesc")
    String gameDesc;
    @PropertyName("gameTrophies")
    String gameTrophies;
    @PropertyName("gameCategory")
    String gameCategory;
    @PropertyName("gameBestAward")
    String gameBestAward;
    @PropertyName("releaseDate")
    String releaseDate;



    public GameModel(){

    }

    public GameModel(String name){
        this.name = name;
    }

    public GameModel(String name, String id){
        this.name = name;
        this.id = id;
    }

    public GameModel(String name, String gameDesc, String gameTrophies, String gameCategory, String gameBestAward){
        this.name = name;
        this.gameDesc = gameDesc;
        this.gameCategory = gameCategory;
        this.gameBestAward = gameBestAward;
        this.gameTrophies = gameTrophies;
    }

    protected GameModel(Parcel in) {
        name = in.readString();
        id = in.readString();
        imagePath = in.readString();
        gameDesc = in.readString();
        gameTrophies = in.readString();
        gameCategory = in.readString();
        gameBestAward = in.readString();
    }

    public static final Creator<GameModel> CREATOR = new Creator<GameModel>() {
        @Override
        public GameModel createFromParcel(Parcel in) {
            return new GameModel(in);
        }

        @Override
        public GameModel[] newArray(int size) {
            return new GameModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    public String getGameDesc() {
        return gameDesc;
    }

    public String getGameCategory() {
        return gameCategory;
    }

    public String getGameBestAward() {
        return gameBestAward;
    }

    public String getGameTrophies() {
        return gameTrophies;
    }

    public void setGameBestAward(String gameBestAward) {
        this.gameBestAward = gameBestAward;
    }

    public void setGameCategory(String gameCategory) {
        this.gameCategory = gameCategory;
    }

    public void setGameDesc(String gameDesc) {
        this.gameDesc = gameDesc;
    }

    public void setGameTrophies(String gameTrophies) {
        this.gameTrophies = gameTrophies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(imagePath);
        parcel.writeString(gameDesc);
        parcel.writeString(gameTrophies);
        parcel.writeString(gameCategory);
        parcel.writeString(gameBestAward);
    }
}
