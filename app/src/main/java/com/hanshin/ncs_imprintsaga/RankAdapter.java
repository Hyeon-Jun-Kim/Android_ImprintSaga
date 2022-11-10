package com.hanshin.ncs_imprintsaga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RankAdapter extends BaseAdapter {


    //랭킹기록정보
    ArrayList<String> enrollEmail = new ArrayList<String>();
    ArrayList<String> enrolllevel = new ArrayList<String>();
    ArrayList<String> enrollPoint = new ArrayList<String>();
    ArrayList<String> enrollStage = new ArrayList<String>();

    //파이어베이스 선언 변수
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;


    public RankAdapter(Context c, ArrayList<String> enrollEmail, ArrayList<String> enrolllevel, ArrayList<String> enrollPoint, ArrayList<String> enrollStage) {
        context = c;
        this.enrollEmail = enrollEmail;
        this.enrolllevel = enrolllevel;
        this.enrollPoint = enrollPoint;
        this.enrollStage = enrollStage;
    }

    @Override
    public int getCount() {
        return enrollEmail.size();
    }

    @Override
    public Object getItem(int position) {
        return enrollEmail.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rank_list_item, parent, false);
        }
        TextView rankEmail = convertView.findViewById(R.id.rankEmail);
        TextView rankLevel = convertView.findViewById(R.id.rankLevel);
        TextView rankPoint = convertView.findViewById(R.id.rankPoint);
        TextView rankStage = convertView.findViewById(R.id.rankStage);

        rankEmail.setText(enrollEmail.get(position));
        rankLevel.setText(enrolllevel.get(position));
        rankPoint.setText(enrollPoint.get(position));
        rankStage.setText(enrollStage.get(position));

        return convertView;
    }



}
