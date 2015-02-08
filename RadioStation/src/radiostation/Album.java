/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author user
 */
public class Album {
    private String title;
    private Date releaseDate;
    private String type;
    private Integer diskNumber;
    private ArrayList<Song>songs;
    private ArrayList<MusicGroup>groups;
    private ArrayList<Artist>artists;
    private MusicProductionCompany company;
    
    public Album(String title,Date releaseDate,String type,Integer diskNumber){
        this.title=title;
        this.releaseDate=releaseDate;
        this.type=type;
        this.diskNumber=diskNumber;
        songs=new ArrayList<Song>();
    }

        public void add(Song song){
        songs.add(song);
    }
        
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDiskNumber() {
        return diskNumber;
    }

    public void setDiskNumber(Integer diskNumber) {
        this.diskNumber = diskNumber;
    }

    public ArrayList<MusicGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<MusicGroup> groups) {
        this.groups = groups;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public MusicProductionCompany getCompany() {
        return company;
    }

    public void setCompany(MusicProductionCompany company) {
        this.company = company;
    }
    
    public String toString(){
        return(title+releaseDate+type+diskNumber);
    }
}
