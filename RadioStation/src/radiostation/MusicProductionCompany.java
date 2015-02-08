/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class MusicProductionCompany {
    private String name;
    private String address;
    private Integer telephone;
    private ArrayList<Album>albums;
    
    public MusicProductionCompany(String name,String address,Integer telephone){
    this.name=name;
    this.address=address;
    this.telephone=telephone;
    albums=new ArrayList<Album>();
    }

    public void add(Album album){
        albums.add(album);
    }
    
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTelephone() {
        return telephone;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
    }

    public String toString(){
        return name+address+telephone;
    }
   
    
}
