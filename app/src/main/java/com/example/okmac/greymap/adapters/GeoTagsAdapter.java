package com.example.okmac.greymap.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.okmac.greymap.R;
import com.example.okmac.greymap.Utils.AppFileManager;
import com.example.okmac.greymap.Utils.PicassoImageUtil;
import com.example.okmac.greymap.fragments.GeoTag;

import java.io.File;
import java.util.ArrayList;

public class GeoTagsAdapter extends RecyclerView.Adapter<GeoTagsAdapter.GeoTagHolder> {
    public ArrayList<GeoTag> geoTags;
    private Context context;
    private PicassoImageUtil picassoImageUtil;
    //private AppFileManager appFileManager;

    public GeoTagsAdapter(Context context) {
        this.context = context;
        geoTags = new ArrayList<>();
        picassoImageUtil = new PicassoImageUtil(context);
        //appFileManager = new AppFileManager(context);
    }

    //public abstract void onTap(GeoTag geoTag);

    @NonNull
    @Override
    public GeoTagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GeoTagHolder(LayoutInflater.from(context).inflate(R.layout.row_geo_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GeoTagHolder holder, int position) {
        final GeoTag geoTag = geoTags.get(holder.getAdapterPosition());
        if (geoTag != null) {
            //File image = appFileManager.getExistingFile(geoTag.getImageName());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                //Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                //bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/3,bitmap.getHeight()/3,true);
                //holder.ivTag.setImageBitmap(bitmap);
            } catch (Exception e) {
                //holder.ivTag.setImageResource(R.drawable.ic_placeholder);
            }
            StringBuilder strLatLng = new StringBuilder(context.getString(R.string.coordinates));
            if (geoTag.getLatitude() != null) {
                strLatLng.append(geoTag.getLatitude()).append(", ");
            }
            if (geoTag.getLongitude() != null) {
                strLatLng.append(geoTag.getLongitude());
            }

            holder.tvLatlng.setText(strLatLng);
            if (TextUtils.isEmpty(geoTag.getAddress())) {
                holder.tvAddress.setVisibility(View.GONE);
            } else {
                holder.tvAddress.setVisibility(View.VISIBLE);
                holder.tvAddress.setText(geoTag.getAddress());
            }

            /*holder.cvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTap(geoTag);
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        if (geoTags == null) {
            return 0;
        }
        return geoTags.size();
    }

    class GeoTagHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvLatlng;
        CardView cvTag;

        GeoTagHolder(View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_tag_address);
            tvLatlng = itemView.findViewById(R.id.tv_tag_latlng);
            cvTag = itemView.findViewById(R.id.cv_tag);
        }
    }
}
