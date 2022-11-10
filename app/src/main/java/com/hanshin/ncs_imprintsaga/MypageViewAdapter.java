package com.hanshin.ncs_imprintsaga;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;



public class MypageViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> haveItem;

    //그리드뷰 이미지 타이틀
    String[] shopListTitle=  {
            "cap", "newspaper", "sneakers",
            "coffee ", "book", "magnifier",
            "hambuger", "phone", "sunglass"
    };
    //그리드뷰 이미지 저장위치
    Integer[] shopListImage ={
            R.drawable.item1, R.drawable.item2, R.drawable.item3,
            R.drawable.item4, R.drawable.item5, R.drawable.item6,
            R.drawable.item7, R.drawable.item8, R.drawable.item9
    };

    //그리드뷰 이미지 가격
    Integer[] shopListPrice ={
            200,  300,  500,
            800, 1000, 1500,
            1500, 2000, 3000
    };


    //그리드뷰 대화상자 아이템능력
    final String[] shopListAbility = {
            //기본 HP = 100, 기본 공격력 = 10, 기본 방어력 = 0, 능력 = x
            "방어 10 증가", "공격 10 증가 ", "힌트 1회 제공",
            "방어 20 증가", "공격 20 증가", "힌트 2회 제공",
            "방어 30 증가", "공격 30 증가",  "힌트 3회 제공"
    };



    public MypageViewAdapter(Context c, ArrayList<String> haveItem) {
        context = c;
        this.haveItem = haveItem;
    }



    @Override
    public int getCount() {
        return shopListImage.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mypage_list_image, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.mypage_ListImage);
        TextView text = convertView.findViewById(R.id.mypage_ListTV);



//        int h = Integer.parseInt(haveItem.get(position));
//        //아이템을 갖고 있을 경우
//        if(h==1){
//            image.setImageResource(shopListImage[position]);
//            image.setScaleType(ImageView.ScaleType.FIT_XY);
//            image.setPadding(20,20,20,20);
//        }

            image.setImageResource(shopListImage[position]);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setPadding(20,20,20,20);

        //4번아이템부터 9번아이템 흑백으로 표시
        if(position ==0 || position==1 || position==2){
        }else{
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            image.setColorFilter(filter);
        }

        return  convertView;
    }
    //갖고 있는 아이템 리스트 설정
    public void set(ArrayList<String> haveItem) {
        this.haveItem = haveItem;
    }


}
