package com.lqwawa.libs.mediapaper.io;

import android.graphics.drawable.Drawable;

public class FileItem implements Comparable<FileItem> {

    String title;
    String path;
    Drawable icon;
    String size;
    boolean  mIsFolder;
    boolean mIsSelect;
    public FileItem(String title,String path, Drawable bullet,String size,boolean bIsfolder)
    {
        this.title = title;
        this.path = path;
        this.icon = bullet;
        this.size = size;
        this.mIsFolder = bIsfolder;
        this.mIsSelect = false;
        
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileItem)) {
            return false;
        }

        FileItem that = (FileItem) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result;
        result = (title != null ? title.hashCode() : 0);
        return result;
    }
    public String getFilePath()
    {
    	StringBuffer s = new StringBuffer(path);
    	s.append("/");
    	s.append(title);
    	return s.toString();
    }
    /** Make IconifiedText comparable by its name */
    public int compareTo(FileItem other)
    {
        if (this.title != null)
        {
             return this.title.compareTo(other.title);
        }
        else
        {
             throw new IllegalArgumentException();
      }
   }
}
