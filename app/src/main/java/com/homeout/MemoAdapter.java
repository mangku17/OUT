package com.homeout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.CustomViewHolder> {

    private ArrayList<Memo> arrayList;

    public MemoAdapter(ArrayList<Memo> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MemoAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.CustomViewHolder holder, int position) {
        //추가되는 생명주기
        holder.tvDate.setText(arrayList.get(position).getDate());
        holder.tvContext.setText(arrayList.get(position).getContext());

        //리스트가 클릭이 되었을때
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curDate = holder.tvDate.getText().toString();
                String curContent = holder.tvContext.getText().toString();
                Toast.makeText(v.getContext(), ""+curDate+", "+curContent, Toast.LENGTH_SHORT).show();

            }
        });
        //리스트를 길게 눌렀을때 삭제
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeMemo(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList?arrayList.size() : 0);
    }

    public void removeMemo(int position){
        try{
            arrayList.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvDate, tvContext;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //액티비티형식이 아니기 때문에 위젯을 찾기위해서, itemView.을 사용
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvContext = (TextView) itemView.findViewById(R.id.tvContext);
        }
    }
}
