package com.webivoire.babyissweetest.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.webivoire.babyissweetest.R;
import com.webivoire.babyissweetest.base.BaseApplication;
import com.webivoire.babyissweetest.model.Item;
import com.webivoire.babyissweetest.tools.Tools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fabia on 16/03/2017.
 */

public class ListFragment extends android.support.v4.app.Fragment { // Il ya plusieur type de Fragment

    @BindView(R.id.listItem) ListView listView;

    private static final  int COUNT_PER_PAGE = 10;


    private DataAdapter dataAdapter;
    private RealmResults<Item> dataItems;
    private ArrayList<Item> maListe = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_item_activity,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        dataAdapter = new DataAdapter(getContext(), maListe);
        listView.setAdapter(dataAdapter);

        loadFromRealm();
        fetchLieuList();



    }



    private void loadFromRealm() {
        dataItems = BaseApplication.getRealmInstance()
                .where(Item.class)
                .findAll();
        loadData();
        dataItems.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(RealmResults<Item> element) {
                loadData();
            }
        });
    }
    private void loadData(){
        maListe.clear();
        maListe.addAll(dataItems.subList(0, dataItems.size() < COUNT_PER_PAGE ? dataItems.size() : COUNT_PER_PAGE));
        dataAdapter.notifyDataSetChanged();
    }
    private void fetchLieuList(){

        Tools.getServiceAPI().getItemFromDB().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement element = response.body();
                JsonArray array = element.getAsJsonArray();

                BaseApplication.getRealmInstance().beginTransaction();
                for (JsonElement obj : array){

                    Item lieu = Item.createFormJson(obj);
                    BaseApplication.getRealmInstance().copyToRealmOrUpdate(lieu);
                   // maListe.clear();
                   // maListe.addAll(dataItems.subList(0, dataItems.size() < COUNT_PER_PAGE ? dataItems.size() : COUNT_PER_PAGE));
                    //dataAdapter.notifyDataSetChanged();
                }

                BaseApplication.getRealmInstance().commitTransaction();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "la requet a echoué", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public class DataAdapter extends ArrayAdapter<Item> {

        public DataAdapter(Context context, List<Item> item) {
            super(context,R.layout.list_item, item);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View v = convertView;
            ViewHolder vh;
            Item item = getItem(position); // obligatoire
            // si convertView est null on lui donne les items
            if(v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item, parent, false);
                //create viewHolder
                vh = new ViewHolder(v);
                // ne pas oublier setter la tag avec la vh
                v.setTag(vh);
            }else{
                vh = (ViewHolder)v.getTag();
            }
            // si le vh n'est pas null

            vh.type.setText(item.getType());
            vh.nom.setText(item.getNom());
            vh.img.setTag(item);



            Picasso.with(getContext()).load(item.getPhotos()).into(vh.img);

            return v;
        }
        class ViewHolder{


            @BindView(R.id.reserver)
            Button reserver;
            @BindView(R.id.img)
            ImageView img;
            @BindView(R.id.nom)
            TextView nom;
            @BindView(R.id.type) TextView type;








            public ViewHolder(View v) {
                ButterKnife.bind(this, v);
            }

            @OnClick(R.id.img)
            public void onImageClick(){
                Item item = (Item) img.getTag();


                View v = LayoutInflater.from(getContext())
                            .inflate(R.layout.dialog_photos_info, null, false);



                Picasso.with(getContext()).load(item.getPhotos())
                        .into((ImageView)v.findViewById(R.id.dialog_photos));


                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setView(v)
                                .create();

                alertDialog.show();


            }
        }
    }





}
