package bigbottleapps.fluffer1.Models;

import android.content.Context;
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
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import bigbottleapps.fluffer1.R;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public List<RecyclerItem> listItems;
    public Context mContext;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RecyclerItem itemList = listItems.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtLikes.setText(itemList.getLikes()+"");
        holder.txtDislikes.setText(itemList.getDislikes()+"");
        holder.progressBar.setProgress(itemList.getProgress());
        setImage(holder.imgPhoto, itemList.getImage());//Запускает асинхронную загрузку изображения

        holder.imgLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer likes = listItems.get(position).getLikes()+1;
                listItems.get(position).setLikes(likes);
                listItems.get(position).setProgress(listItems.get(position).getLikes(), listItems.get(position).getDislikes());
                holder.progressBar.setProgress(listItems.get(position).getProgress());
                holder.txtLikes.setText(String.valueOf(likes));
            }
        });

        holder.imgDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer dislikes = listItems.get(position).getDislikes()+1;
                listItems.get(position).setDislikes(dislikes);
                listItems.get(position).setProgress(listItems.get(position).getLikes(), listItems.get(position).getDislikes());
                holder.progressBar.setProgress(listItems.get(position).getProgress());
                holder.txtDislikes.setText(String.valueOf(dislikes));
            }
        });

        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Показать описание", Toast.LENGTH_LONG).show();
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, listItems.get(position).getDescription(), Toast.LENGTH_LONG).show();
            }
        };

        holder.txtTitle.setOnClickListener(clickListener);
    }
    private void setImage(ImageView iw, String res){
        new DownloadImageTask(iw).execute(res);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle;
        TextView txtLikes;
        TextView txtDislikes;
        ImageView imgLikes;
        ImageView imgDislikes;
        ImageView imgPhoto;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtLikes = (TextView) itemView.findViewById(R.id.text_likes);
            txtDislikes = (TextView) itemView.findViewById(R.id.text_dislikes);
            imgLikes = (ImageView) itemView.findViewById(R.id.image_likes);
            imgDislikes = (ImageView) itemView.findViewById(R.id.image_dislikes);
            imgPhoto = (ImageView) itemView.findViewById(R.id.list_item_photo);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView iw;

        DownloadImageTask(ImageView iw){
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
