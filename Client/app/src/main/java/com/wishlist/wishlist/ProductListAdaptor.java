package com.wishlist.wishlist;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by honey on 4/20/2016.
 */
public class ProductListAdaptor implements android.widget.ListAdapter {
    private List<Product> productList ;
    public ProductListAdaptor(List<Product> productList){
        this.productList=productList;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getProductId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView rowProductName;
        public TextView rowProductDescription;
        public TextView rowDate;
        public TextView rowAdderName;
        public ImageView productImage;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.rowProductName = (TextView)convertView.findViewById(R.id.product_name);
            viewHolder.rowAdderName=(TextView)convertView.findViewById(R.id.adder_name);
            viewHolder.rowProductDescription=(TextView)convertView.findViewById(R.id.product_description);
            viewHolder.rowDate=(TextView)convertView.findViewById(R.id.added_date);
            viewHolder.productImage=(ImageView) convertView.findViewById(R.id.thumbnailViewImage);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.rowProductName.setText(productList.get(position).getProductName());
        viewHolder.rowProductDescription.setText(productList.get(position).getProductDescription());
        viewHolder.rowAdderName.setText(productList.get(position).getProductAdder());

        if (productList.get(position).ownsImage()) {
            String productId = productList.get(position).getServerId();
            if (productId != null) {
                String imageUrl = "image/" + productId;
                String token = AuthHelper.getAuthToken(parent.getContext());
                HttpClient.downloadImage(imageUrl + "?token=" + token, AuthHelper.getName(), AuthHelper.getFamilyName(), productId, viewHolder.productImage);
            }
        }

        Date date=productList.get(position).getAddingDate();
        SimpleDateFormat destDf = new SimpleDateFormat("MM/dd/yyyy");
        String mydate = destDf.format(date);

        //String stringDate=String.valueOf(date.getTime());
        viewHolder.rowDate.setText(mydate);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
