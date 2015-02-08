/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author 
 */
public class Artist {
    private String firstName;
    private String lastName;
    private String artisticName;
    private String sex;
    private Date birthDay;
    private String birthPlace;
    private ArrayList<MusicGroup> group;
    private MusicGenre genre;
    
    public Artist(String firstName,String lastName,String artisticName,String sex,Date birthDay,String birthPlace){
        this.firstName=firstName;
        this.lastName=lastName;
        this.artisticName=artisticName;
        this.sex=sex;
        this.birthDay=birthDay;
        this.birthPlace=birthPlace;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getArtisticName() {
        return artisticName;
    }

    public void setArtisticName(String artisticName) {
        this.artisticName = artisticName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    
     public void add(MusicGroup groups){
        group.add(groups);
    }
    
    public Iterator<MusicGroup>getArtist(){
        return group.iterator();
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }
    
    public String toString(){
        return(firstName+lastName+group+genre);
    }
}
