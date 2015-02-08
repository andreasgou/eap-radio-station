/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

// import java.time.Period;

import java.util.Date;


/**
 *
 * @author user
 */
public class Song {
    private String title;
    private Long duration;
    private Integer trackNr;
    private Album album;
    
    public Song(String title, Long duration,Integer trackNr){
        this.title=title;
        this.duration=duration;
        this.trackNr=trackNr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getTrackNr() {
        return trackNr;
    }

    public void setTrackNr(Integer trackNr) {
        this.trackNr = trackNr;
    }
    
    
    
    
}
