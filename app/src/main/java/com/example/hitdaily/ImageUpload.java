package com.example.hitdaily;

public class ImageUpload {
    private String imageName;
    private String mImageUri;
    public ImageUpload(){
        //Empty constructor
    }

    public ImageUpload(String imageName, String mImageUri) {
        if(imageName.trim().equals("")){
            imageName = "No name";
        }
        this.imageName = imageName;
        this.mImageUri = mImageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }
}
