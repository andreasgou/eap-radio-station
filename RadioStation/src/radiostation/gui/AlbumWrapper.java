/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;

import java.util.ArrayList;
import java.util.List;
import radiostation.Album;

/**
 *
 * @author a.gounaris
 */
public class AlbumWrapper {
    
    private String albumTitle;
    private List<Album> albums;
    
    public AlbumWrapper(Album album) {
        this.albumTitle = album.getTitle();
        this.albums = new ArrayList<>();
        this.albums.add(album);
    }
}
