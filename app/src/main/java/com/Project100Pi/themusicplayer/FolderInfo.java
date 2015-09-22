package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 9/1/2015.
 */
public class FolderInfo {
    private String folderName,currentPath;
    private Long folderId;
    private int sNo;

    public FolderInfo(int no,String name,String path){
        this.folderName = name;
        this.sNo = no;
        this.currentPath = path;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public void setFolderName(String folderName) {
        folderName = folderName;
    }

    public int getsNo() {
        return sNo;
    }

    public String getFolderName() {
        return folderName;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}
