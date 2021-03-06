package com.conestogac.musicplayer.model;

/**
 * Author Hassan Nahhal
 */
public class Playlist {
    private int id;
    private String name;
    private int numberOfSong;

    public Playlist(String name, int numberOfSong) {
        this.name = name;
        this.numberOfSong = numberOfSong;
    }

    public Playlist(int id, String name, int numberOfSong) {
        this(name, numberOfSong);
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSong() {
        return this.numberOfSong;
    }

    public void setNumberOfSong(int numberOfSong) {
        this.numberOfSong = numberOfSong;
    }
}
