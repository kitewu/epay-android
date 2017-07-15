package com.xbw.unman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imgload.ImageLoader;
import com.xbw.unman.R;
import com.xbw.unman.model.GoodsModel;

import java.util.List;

/**
 * Created by xubowen on 2017/5/25.
 */
public class ListGoodsAdapter extends BaseAdapter{
    private List<GoodsModel> mDate;
    private Context mContext;
    public ImageLoader imageLoaders;

    public ListGoodsAdapter(Context mContext, List<GoodsModel> mDate) {
        this.mContext = mContext;
        this.mDate = mDate;
        imageLoaders = new ImageLoader(mContext.getApplicationContext());
    }
    @Override
    public int getCount() {
        return mDate.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mDate.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_list_goods, null);
            viewHolder = new ViewHolder();
            viewHolder.a = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.b = (TextView) convertView.findViewById(R.id.textView3);
            viewHolder.c = (ImageView) convertView.findViewById(R.id.imageView2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GoodsModel model = mDate.get(position);

        viewHolder.a.setText(model.getGoodsName());
        viewHolder.b.setText(model.getGoodsPrice()+" ¥");
        imageLoaders.DisplayImage(model.getGoodsImg(), viewHolder.c);
        return convertView;
    }
    static class ViewHolder {
        TextView a;
        TextView b;
        ImageView c;
    }
}
