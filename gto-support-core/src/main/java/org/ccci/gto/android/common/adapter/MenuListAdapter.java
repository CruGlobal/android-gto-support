package org.ccci.gto.android.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ccci.gto.android.common.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;

    private Menu mMenu;
    private List<MenuItem> mItems = Collections.emptyList();

    private int mLayout;

    private int titleResourceId = 0;
    private int iconResourceId = 0;

    public MenuListAdapter(final Context context, final int layout) {
        this(context, layout, null);
    }

    public MenuListAdapter(final Context context, final int layout, final Menu menu) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mLayout = layout;
        mMenu = menu;

        this.synchronizeMenu();
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(final Menu menu) {
        mMenu = menu;
        synchronizeMenu();
    }

    public void setTitleResourceId(final int id) {
        titleResourceId = id;
    }

    public void setIconResourceId(final int id) {
        iconResourceId = id;
    }

    public final void synchronizeMenu() {
        if (mMenu != null) {
            // find all visible MenuItems
            final List<MenuItem> items = new ArrayList<MenuItem>();
            for (int i = 0; i < mMenu.size(); i++) {
                final MenuItem item = mMenu.getItem(i);
                if (item.isVisible()) {
                    items.add(item);
                }
            }

            // update the items list
            mItems = items;
            notifyDataSetChanged();
        } else {
            mItems = Collections.emptyList();
            notifyDataSetInvalidated();
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MenuItem getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getItemId();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final MenuItem item = getItem(position);
        final View view = convertView != null ? convertView : createView(item, parent);
        bindView(view, item);
        return view;
    }

    protected View createView(final MenuItem item, final ViewGroup parent) {
        return mInflater.inflate(mLayout, parent, false);
    }

    protected void bindView(final View view, final MenuItem item) {
        final TextView titleView = ViewUtils.findView(view, TextView.class, this.titleResourceId);
        if (titleView != null) {
            titleView.setText(item.getTitle());
        }
        final ImageView iconView = ViewUtils.findView(view, ImageView.class, this.iconResourceId);
        if (iconView != null) {
            iconView.setImageDrawable(item.getIcon());
        }
    }

    public abstract static class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public final void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final Adapter adapter = parent.getAdapter();
            if (adapter instanceof MenuListAdapter) {
                this.onItemClick(parent, view, position, ((MenuListAdapter) adapter).mItems.get(position));
            }
        }

        public abstract void onItemClick(AdapterView<?> parent, View view, int position, MenuItem item);
    }
}
