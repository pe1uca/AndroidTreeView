package com.unnamed.b.atv.sample.holder;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.activity.ImagePickerActivity;
import com.unnamed.b.atv.sample.activity.MainActivity;
import com.unnamed.b.atv.sample.activity.SingleFragmentActivity;
import com.unnamed.b.atv.sample.view.JsonTreeView;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        //New folder action
        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode newNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "New Folder"));
                getTreeView().addNode(node, newNode);
                nodesChanged();
            }
        });

        //Gallery action
        view.findViewById(R.id.btn_addImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleFragmentActivity activity = (SingleFragmentActivity)context;
                Intent intent = new Intent(context, ImagePickerActivity.class);
                activity.startActivityForResult(intent, 100);

                //Binding
                activity.setImagePickerListener(new SingleFragmentActivity.ImagePickerListener() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
                        //Image is picked and ready to add the node
                        TreeNode newNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Image", imageUri.toString()));
                        getTreeView().addNode(node, newNode);

                        //Notify the JsonTreeView
                        nodesChanged();
                    }
                });
            }
        });

        //Delete action
        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);

                //Notify the JsonTreeView
                nodesChanged();
            }
        });

        //Top level don't show delete
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.INVISIBLE);
        }

        //Image don't show add nesting new folder
        if (value.icon == R.string.ic_photo) {
            arrowView.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.btn_addFolder).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    public void nodesChanged(){
        //Serves as an event, we don't override inside JsonTreeView since there are getTreeView methods that circumvent the inheritance
        JsonTreeView jsonTreeView = (JsonTreeView) getTreeView();
        if (jsonTreeView != null){
            jsonTreeView.nodesUpdated();
        }
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public String imageUrl;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
            this.imageUrl = null;
        }

        public IconTreeItem(int icon, String text, String imageUrl) {
            this.icon = icon;
            this.text = text;
            this.imageUrl = imageUrl;
        }
    }
}
