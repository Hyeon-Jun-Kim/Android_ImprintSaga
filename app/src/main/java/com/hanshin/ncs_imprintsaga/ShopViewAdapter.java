package com.hanshin.ncs_imprintsaga;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopViewAdapter extends BaseAdapter {
    Context context;

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
    String[] shopListAbility = {
            //기본 HP = 100, 기본 공격력 = 10, 기본 방어력 = 0, 능력 = x
            "방어 10 증가", "공격 10 증가 ", "힌트 1회 제공",
            "방어 20 증가", "공격 20 증가", "힌트 2회 제공",
            "방어 30 증가", "공격 30 증가",  "힌트 3회 제공"
    };

    //그리드뷰 이미지 타이틀
    String[] shopListTitle=  {
            "cap", "newspaper", "sneakers",
            "coffee ", "book", "magnifier",
            "hambuger", "phone", "sunglass"
    };
    public ShopViewAdapter(Context c) {
        context = c;
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
            convertView = inflater.inflate(R.layout.shop_list_image, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.shopListImage);
        TextView textView  = convertView.findViewById(R.id.shopListTV);

        image.setImageResource(shopListImage[position]);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setPadding(20,20,20,20);
        textView.setText(shopListPrice[position].toString());

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
}
