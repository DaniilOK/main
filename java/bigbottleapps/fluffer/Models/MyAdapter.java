package bigbottleapps.fluffer.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import bigbottleapps.fluffer.Controllers.ActionActivity;
import bigbottleapps.fluffer.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private List<RecyclerItem> listItems;
    private Context mContext;
    public MyAdapter(List<RecyclerItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)  {

        final RecyclerItem itemList = listItems.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.date.setText(itemList.getDate());
        String typelist[] = mContext.getResources().getStringArray(R.array.typelist);
        int type = itemList.getType();
        holder.txtType.setText(typelist[type].toUpperCase());
        switch (type){
            case 1:
                holder.txtType.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_local_dining_black_24dp), null ,null, null);
                break;
            case 2:
                holder.txtType.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_attach_money_black_24dp), null ,null, null);
                break;
            case 3:
                holder.txtType.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_filter_vintage_black_24dp), null ,null, null);
                break;


        }

        if(itemList.getProgress()==-1){
            holder.progressBar.setProgress(0);
            holder.percent.setText("-%");
        }else if(itemList.getProgress()==0){
            holder.progressBar.setProgress(0);
            holder.percent.setText("0%");
        }else{
            holder.progressBar.setProgress(itemList.getProgress());
            holder.percent.setText(itemList.getProgress() + "%");
            holder.star.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_black_24dp));
        }
       //Запускает асинхронную загрузку изображения

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActionActivity.class);
                intent.putExtra("id", listItems.get(position).getId());
                mContext.startActivity(intent);
            }
        };

        holder.txtTitle.setOnClickListener(clickListener);
        holder.percent.setOnClickListener(clickListener);
        holder.imgPhoto.setOnClickListener(clickListener);
        holder.progressBar.setOnClickListener(clickListener);


        new AsyncTask<ViewHolder, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(ViewHolder... params) {
                setImage(holder.imgPhoto, itemList.getImage());
                return null;
            }
        }.execute(holder);

    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        TextView txtType;
        TextView percent;
        TextView date;
        CircleImageView imgPhoto;
        ImageView star;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            txtType = (TextView) itemView.findViewById(R.id.type);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            imgPhoto = (CircleImageView) itemView.findViewById(R.id.list_item_photo);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            percent = (TextView)itemView.findViewById(R.id.percent);
            date = (TextView)itemView.findViewById(R.id.date);
            star = (ImageView)itemView.findViewById(R.id.star);
        }
    }

    private void setImage(CircleImageView iw, String res){
        new DownloadImageTask(iw).execute(res);
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView iw;

        DownloadImageTask(CircleImageView iw){
            this.iw = iw;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(urls[0]).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            this.iw.setImageBitmap(result);
        }
    }
}