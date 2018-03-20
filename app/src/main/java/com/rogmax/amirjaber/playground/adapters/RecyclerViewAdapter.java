package com.rogmax.amirjaber.playground.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rogmax.amirjaber.playground.R;
import com.rogmax.amirjaber.playground.UpdateActivity;
import com.rogmax.amirjaber.playground.models.LocationDto;

import java.util.List;

/**
 * Created by Amir Jaber on 2/13/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<LocationDto> results;

    public RecyclerViewAdapter(Context context, List<LocationDto> list) {
        this.context = context;
        this.results = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LocationDto locationDto = results.get(position);
        holder.id = locationDto.getId();
        holder.textViewName.setText(locationDto.getName());
        holder.textViewAddress.setText(locationDto.getAddress());

    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Long id;
        TextView textViewName;
        TextView textViewAddress;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textName);
            textViewAddress = itemView.findViewById(R.id.textAddress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Long id = getId();
            String name = textViewName.getText().toString();
            String address = textViewAddress.getText().toString();

            Intent i = new Intent(context, UpdateActivity.class);
            i.putExtra("id", id);
            i.putExtra("name", name);
            i.putExtra("address", address);
            context.startActivity(i);

        }
    }
}
