package com.unnamed.b.atv.sample.view;

import android.content.Context;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.sample.model.JsonTreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumeet Patel on 13/02/2020.
 */

public class JsonTreeView extends AndroidTreeView {

    public interface NodeListener {
        public void onNodesUpdated();
    }
    private NodeListener nodeListener;

    public JsonTreeView(Context context) {
        super(context);
        this.nodeListener = null;
    }

    public JsonTreeView(Context context, TreeNode root) {
        super(context, root);
        this.nodeListener = null;
    }

    public JsonTreeView(Context context, String jsonString) {
        super(context);
        this.nodeListener = null;
        putJson(jsonString);
    }

    public void putJson(String jsonString) {
        //Deserialize to object
        Gson gson = new Gson();
        JsonTreeNode[] parentNodesArray = gson.fromJson(jsonString, JsonTreeNode[].class);
        List<JsonTreeNode> parentNodes = Arrays.asList(parentNodesArray);
        JsonTreeNode rootNode = new JsonTreeNode();
        rootNode.children = parentNodes;

        //Rebuild
        this.rebuildFromRootJsonTreeNode(rootNode);
    }

    public String getJson() throws Exception {
        TreeNode current = this.mRoot;
        List<JsonTreeNode> jsonParentNodes = getCleanChildren(current);

        //Serialize
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonParentNodes);
        return jsonString;
    }

    private void clearNodes(){
        TreeNode root = this.mRoot;
        List<TreeNode> children = root.getChildren();
        for (TreeNode child : children) {
            this.removeNode(child);
        }
    }

    private void rebuildFromRootJsonTreeNode(JsonTreeNode rootNode) {
        clearNodes();

        List<TreeNode> children = this.getDirtyChildren(rootNode);

        //We reuse the root node that already exists, to preserve view bindings
        if (this.mRoot == null){
            this.setRoot(TreeNode.root());
        }

        this.expandAll();

        //Note: Commented since it Prevents a redraw if clear is used
        //this.mRoot.addChildren(children);

        //Update visibility, since the super class doesn't seem to automatically handle it.
        for (TreeNode child : children) {
            //We add the children one-by-one here, it helps the view redraw rather than the bulk action
            this.addNode(this.mRoot, child);

            if (child.isExpanded()) {
                this.expandNode(child);
            }
            else {
                this.collapseNode(child);
            }
        }
    }

    private List<TreeNode> getDirtyChildren(JsonTreeNode node){
        List<JsonTreeNode> currentChildren = node.children;
        List<TreeNode> dirtyChildren = new ArrayList<>();

        //Iterate through the children
        for (JsonTreeNode child : currentChildren) {
            TreeNode childNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(child.icon, child.text, child.imageUrl));
            childNode.setSelected(child.isSelected);
            childNode.setSelectable(child.isSelectable);
            childNode.setExpanded(child.isExpanded);

            //More children
            if (child.children != null && child.children.size() > 0){
                List<TreeNode> childChildren = getDirtyChildren(child);
                childNode.addChildren(childChildren);
            }
            dirtyChildren.add(childNode);
        }

        return dirtyChildren;
    }

    private List<JsonTreeNode> getCleanChildren(TreeNode node) throws Exception {
        List<TreeNode> currentChildren = node.getChildren();
        List<JsonTreeNode> cleanChildren = new ArrayList<>();

        //Iterate through the children
        for (TreeNode child : currentChildren) {
            Object childValue = child.getValue();
            JsonTreeNode childNode = null;
            int icon = 0;
            String text = null;
            String imageUrl = null;

            //TreeItems MUST have IconTreeItem as their 'value'
            if (childValue.getClass().equals(IconTreeItemHolder.IconTreeItem.class)) {
                //Extract the view
                IconTreeItemHolder.IconTreeItem treeItem = (IconTreeItemHolder.IconTreeItem) childValue;
                text = treeItem.text;
                icon = treeItem.icon;
                imageUrl = treeItem.imageUrl;
            }
            else {
                throw new Exception("Unsupported TreeNode value");
            }

            childNode = new JsonTreeNode(icon, text, imageUrl, child.isSelected(), child.isSelectable(), child.isExpanded());

            //More children
            if (child.getChildren() != null && child.getChildren().size() > 0){
                List<JsonTreeNode> childChildren = getCleanChildren(child);
                childNode.children = childChildren;
            }
            cleanChildren.add(childNode);
        }

        return cleanChildren;
    }

    public void setNodeListener(NodeListener nodeListener) {
        this.nodeListener = nodeListener;
    }

    public void nodesUpdated(){
        if (this.nodeListener != null){
            this.nodeListener.onNodesUpdated();
        }
    }
}
