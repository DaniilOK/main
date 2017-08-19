package bigbottleapps.fluffer.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;
import bigbottleapps.fluffer.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private List<RecyclerItem> listItems;
    private Context mContext;
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private HttpURLConnection conn;
    private int id, user_id, pos, res;
    private ViewHolder holder_;
    private boolean lOrD;

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
        if(itemList.getLikes()<10000)
            holder.txtLikes.setText(itemList.getLikes()+"");
        if((itemList.getLikes()>=10000)&&(itemList.getLikes()<1000000))
            holder.txtLikes.setText(itemList.getLikes()/1000+"k");
        if(itemList.getLikes()>=1000000)
            holder.txtLikes.setText(itemList.getLikes()/1000000+"M");

        if(itemList.getDislikes()<10000)
            holder.txtDislikes.setText(itemList.getDislikes()+"");
        if((itemList.getDislikes()>=10000)&&(itemList.getDislikes()<1000000))
            holder.txtDislikes.setText(itemList.getDislikes()/1000+"k");
        if(itemList.getDislikes()>=1000000)
            holder.txtDislikes.setText(itemList.getDislikes()/1000000+"M");

        holder.progressBar.setProgress(itemList.getProgress());
        setImage(holder.imgPhoto, itemList.getImage());//Запускает асинхронную загрузку изображения

        switch (listItems.get(position).getCurrent()){
            case 0:
                holder.txtDislikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(position).getDownBlue(), null, null);
                break;
            case 1:

                break;
            case 2:
                holder.txtLikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(position).getUpBlue(), null, null);
                break;
        }

        holder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesOrDislikesClick(true, position, holder, mContext.getString(R.string.only_registered_like_posts));
            }
        });

        holder.txtDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesOrDislikesClick(false, position, holder, mContext.getString(R.string.only_registered_like_posts));
            }
        });

        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mContext.getString(R.string.show_descr), Toast.LENGTH_LONG).show();
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

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private void likesOrDislikesClick(boolean flag, int position, ViewHolder holder, String text){
        id = Integer.parseInt(listItems.get(position).getId());
        user_id = Integer.parseInt(listItems.get(position).getUser());
        pos = position;
        holder_ = holder;
        if(user_id!=0) {
            lOrD = flag;
            new SELECT1().execute();
        } else
            setDialog(text);
    }

    private void setDialog(String text){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.registration));
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getString(R.string.registration), new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(mContext, RegisterOrLogInActivity.class));
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setImage(ImageView iw, String res){
        new DownloadImageTask(iw).execute(res);
    }

    private void setLikesAndDislikes(int ans){
        setLikesAmount(listItems.get(pos).getLikes());
        setDislikesAmount(listItems.get(pos).getDislikes());
        switch(ans){
            case 0:
                setLikeImage(listItems.get(pos).getUpBlack());
                setDislikeImage(listItems.get(pos).getDownBlue());
                break;
            case 1:
                setLikeImage(listItems.get(pos).getUpBlack());
                setDislikeImage(listItems.get(pos).getDownBlack());
                break;
            case 2:
                setLikeImage(listItems.get(pos).getUpBlue());
                setDislikeImage(listItems.get(pos).getDownBlack());
                break;
        }
    }

    private void setLikeImage(Drawable res){
        try {
            holder_.txtLikes.setCompoundDrawablesWithIntrinsicBounds(null, res, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setDislikeImage(Drawable res){
        try {
            holder_.txtDislikes.setCompoundDrawablesWithIntrinsicBounds(null, res, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setLikesAmount(int amount){
        try{
            holder_.txtLikes.setText(amount+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setDislikesAmount(int amount){
        try{
            holder_.txtDislikes.setText(amount+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle;
        TextView txtLikes;
        TextView txtDislikes;
        ImageView imgPhoto;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtLikes = (TextView) itemView.findViewById(R.id.text_likes);
            txtDislikes = (TextView) itemView.findViewById(R.id.text_dislikes);
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

    private class SELECT1 extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                String likeOrDislike;
                if (lOrD)
                    likeOrDislike = "2";
                else
                    likeOrDislike = "0";
                URL url = new URL(mServerUrl + "valuation_service.php?action=insert&action_id="+id+"&user_id="+user_id+
                        "&like_or_dislike="+likeOrDislike);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setDoInput(true);
                conn.connect();
                res = conn.getResponseCode();
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedString;
                while ((bufferedString = reader.readLine()) != null)
                    stringBuilder.append(bufferedString);
                String answer = stringBuilder.toString();
                answer = answer.substring(0, answer.indexOf("]") + 1);
                inputStream.close();
                reader.close();
                JSONArray jsonArray = new JSONArray(answer);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                int l = Integer.parseInt(jsonObject.getString("likes"));
                int d = Integer.parseInt(jsonObject.getString("dislikes"));
                int ans = Integer.parseInt(jsonObject.getString("this"));
                listItems.get(pos).setDislikes(d);
                listItems.get(pos).setLikes(l);
                listItems.get(pos).setProgress(l, d);
                holder_.progressBar.setProgress(listItems.get(pos).getProgress());
                setLikesAndDislikes(ans);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }
}