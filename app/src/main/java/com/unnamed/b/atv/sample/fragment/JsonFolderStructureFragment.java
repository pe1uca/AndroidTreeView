package com.unnamed.b.atv.sample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.sample.view.JsonTreeView;

/**
 * Created by Sumeet Patel on 24/02/2020.
 */

public class JsonFolderStructureFragment extends Fragment {
    private TextView statusBar;
    private JsonTreeView tView;
    private static final String PREFS_JSONTREEVIEW_DATA = "jsonTreeViewData";
    private ViewGroup containerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_default, null, false);
        containerView = (ViewGroup) rootView.findViewById(R.id.container);

        statusBar = (TextView) rootView.findViewById(R.id.status_bar);

        tView = new JsonTreeView(getActivity());
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        tView.setNodeListener(new JsonTreeView.NodeListener() {
            @Override
            public void onNodesUpdated() {
                //Save on new or deleted nodes
                saveJson();
            }
        });

        TreeNode root = TreeNode.root();
        tView.setRoot(root);

        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        //Load from settings
        loadJson();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        //Enable the Save/Load JSON options
        menu.findItem(R.id.saveJson).setEnabled(true);
        menu.findItem(R.id.loadJson).setEnabled(true);
        menu.findItem(R.id.clearJson).setEnabled(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expandAll:
                tView.expandAll();
                break;
            case R.id.collapseAll:
                tView.collapseAll();
                break;
            case R.id.saveJson:
                saveJson();
                break;
            case R.id.loadJson:
                loadJson();
                break;
            case R.id.clearJson:
                clearJson();
                break;
        }
        return true;
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
        IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        statusBar.setText("Last clicked: " + item.text);

        if (item.imageUrl != null) {
            Uri uri = Uri.parse(item.imageUrl);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        }
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(getActivity(), "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private Activity getParentActivity(){
        return getActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    private void saveJson(){
        String jsonString = null;
        try {
            jsonString = tView.getJson();
        } catch (Exception ex){
            //todo: maybe alert?
        }

        //Save to preferences
        Context ctx = this.getActivity().getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        preferences.edit().putString(PREFS_JSONTREEVIEW_DATA, jsonString).apply();

        Log.d("App", "Saved: " + jsonString);
        ShowShortToast("Saved JSON");
    }

    private void ShowShortToast(String message){
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void loadJson(){
        String defaultImportJsonString = "[{\"children\": [], \"icon\":2131427517,\"isExpanded\":true,\"isSelectable\":true,\"isSelected\":false,\"text\":\"My Folder\"}]";

        //Load the Json string from preferences
        Context ctx = this.getActivity().getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String importJsonString = preferences.getString(PREFS_JSONTREEVIEW_DATA, defaultImportJsonString);

        //Deserialize and load
        try {
            tView.deselectAll();
            tView.putJson(importJsonString);
            Log.d("App", "Loaded: " + tView.getJson());
        } catch (Exception ex){
            //todo: maybe alert?
        }

        ShowShortToast("Loaded JSON");
    }

    private void clearJson() {
        //Clear it out
        try {
            Context ctx = this.getActivity().getApplicationContext();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            preferences.edit().remove(PREFS_JSONTREEVIEW_DATA).commit();
            Log.d("App", "Cleared: " + tView.getJson());
        } catch (Exception ex) {
            //todo: maybe alert?
        }

        loadJson();
    }
}
