package com.example.tidu.reply;

import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchResult {
   String name,status,image,id;

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public SearchResult(String name, String status, String image, String id) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.id = id;
    }
}
