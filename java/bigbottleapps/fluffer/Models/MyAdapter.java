package bigbottleapps.fluffer.Models;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import bigbottleapps.fluffer.R;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<RecyclerItem> listItems;
    private Context mContext;
    private static final String mServerUrl = "http://posovetu.vh100.hosterby.com/";
    private HttpURLConnection conn;
    private int res, ans;
    private String answer;
    private int id, user_id, pos;
    private ViewHolder holder_;

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
            public void onClick(View view) {
                id = Integer.parseInt(listItems.get(position).getId());
                user_id = Integer.parseInt(listItems.get(position).getUser());
                pos = position;
                holder_ = holder;
                if(user_id!=0)
                    new SELECT1().execute();
                else
                    Toast.makeText(MyAdapter.this.mContext, mContext.getString(R.string.only_registered_like_posts), Toast.LENGTH_SHORT).show();

            }
        });

        holder.txtDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = Integer.parseInt(listItems.get(position).getId());
                user_id = Integer.parseInt(listItems.get(position).getUser());
                pos = position;
                holder_ = holder;
                if(user_id!=0)
                    new SELECT2().execute();
                else{
                    Toast.makeText(MyAdapter.this.mContext, mContext.getString(R.string.only_registered_dslike_posts), Toast.LENGTH_SHORT).show();
                }
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
                URL url = new URL(mServerUrl + "valuation_service.php?action=insert&action_id="+id+"&user_id="+user_id+
                "&like_or_dislike=2");
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
                answer = stringBuilder.toString();
                answer = answer.substring(0, answer.indexOf("]") + 1);
                answer = answer.substring(0, answer.indexOf("]") + 1);
                inputStream.close();
                reader.close();
                JSONArray jsonArray = new JSONArray(answer);
                JSONObject jsonObject;
                jsonObject = jsonArray.getJSONObject(0);
                int l = Integer.parseInt(jsonObject.getString("likes"));
                int d = Integer.parseInt(jsonObject.getString("dislikes"));
                ans = Integer.parseInt(jsonObject.getString("this"));
                listItems.get(pos).setDislikes(d);
                listItems.get(pos).setLikes(l);
                listItems.get(pos).setProgress(l, d);
                holder_.progressBar.setProgress(listItems.get(pos).getProgress());
                switch(ans){
                    case 0:
                        new SetLB().execute();
                        new SetDBl().execute();
                        break;
                    case 1:
                        new SetLB().execute();
                        new SetDB().execute();
                        break;
                    case 2:
                        new SetLBl().execute();
                        new SetDB().execute();
                        break;
                }
                new SetL().execute();
                new SetD().execute();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }


    private class SetLB extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                holder_.txtLikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(pos).getUpBlack(), null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SetLBl extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                holder_.txtLikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(pos).getUpBlue(), null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SetDB extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                holder_.txtDislikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(pos).getDownBlack(), null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SetDBl extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                holder_.txtDislikes.setCompoundDrawablesWithIntrinsicBounds(null, listItems.get(pos).getDownBlue(), null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SetL extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                holder_.txtLikes.setText(listItems.get(pos).getLikes() + "");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SetD extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try{
                holder_.txtDislikes.setText(listItems.get(pos).getDislikes()+"");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SELECT2 extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(mServerUrl + "valuation_service.php?action=insert&action_id="+id+"&user_id="+user_id+
                        "&like_or_dislike=0");
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
                answer = stringBuilder.toString();
                answer = answer.substring(0, answer.indexOf("]") + 1);
                inputStream.close();
                reader.close();
                JSONArray jsonArray = new JSONArray(answer);
                JSONObject jsonObject;
                jsonObject = jsonArray.getJSONObject(0);
                int l = Integer.parseInt(jsonObject.getString("likes"));
                int d = Integer.parseInt(jsonObject.getString("dislikes"));
                ans = Integer.parseInt(jsonObject.getString("this"));
                listItems.get(pos).setDislikes(d);
                listItems.get(pos).setLikes(l);
                listItems.get(pos).setProgress(l, d);
                holder_.progressBar.setProgress(listItems.get(pos).getProgress());
                switch(ans){
                    case 0:
                        new SetLB().execute();
                        new SetDBl().execute();
                        break;
                    case 1:
                        new SetLB().execute();
                        new SetDB().execute();
                        break;
                    case 2:
                        new SetLBl().execute();
                        new SetDB().execute();
                        break;
                }
                new SetL().execute();
                new SetD().execute();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return res;
        }
    }
}
