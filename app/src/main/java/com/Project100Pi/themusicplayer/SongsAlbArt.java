package com.Project100Pi.themusicplayer;

import java.util.LinkedHashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class SongsAlbArt implements Parcelable{

	String name;
	
    public SongsAlbArt(Parcel in) {
		// TODO Auto-generated constructor stub
	     readFromParcel(in);
	 }
    public SongsAlbArt(String title){
    	
    	this.name = title;
    }
    

/* public SongsUnderArtists getInstance(String name)
	{
		if(this.name.equals(name))
		    return 
		
	}*/
	
	
	//ArrayList<String> contents =new ArrayList<String>();
    LinkedHashMap<String,String> contents=new LinkedHashMap<String,String>();
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		 dest.writeMap(contents);
	}
	
	private void readFromParcel(Parcel in) {
	      in.readMap(contents,String.class.getClassLoader());
	}

public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    @Override
    public SongsAlbArt createFromParcel(Parcel in) {
        return new SongsAlbArt(in);
    }

    @Override
    public SongsAlbArt[] newArray(int size) {
        return new SongsAlbArt[size];
    }
};


}
