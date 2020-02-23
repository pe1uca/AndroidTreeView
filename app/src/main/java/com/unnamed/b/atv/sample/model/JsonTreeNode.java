package com.unnamed.b.atv.sample.model;

import java.util.List;

/**
 * Created by Sumeet Patel on 13/02/2020.
 */

public class JsonTreeNode {
    //TreeNode Fields
    public List<JsonTreeNode> children;
    public boolean isSelected;
    public boolean isSelectable;
    public boolean isExpanded;

    //View Holder Fields
    public int icon;
    public String text;
    public String imageUrl;

    public JsonTreeNode(){

    }

    public JsonTreeNode(int icon, String text, String imageUrl, boolean isSelected, boolean isSelectable, boolean isExpanded){
        this.icon = icon;
        this.text = text;
        this.imageUrl = imageUrl;

        this.children = children;
        this.isSelected = isSelected;
        this.isSelectable = isSelectable;
        this.isExpanded = isExpanded;
    }
}
