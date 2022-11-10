package com.hanshin.ncs_imprintsaga;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Training_FinishDialog {
    private Context context;

    public Training_FinishDialog(Context context) {
        this.context = context;
    }

    public void callFunction(int totalNum) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.training_end_info);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        final TextView resultTv = (TextView) dlg.findViewById(R.id.resultTv_onTraining);
        resultTv.setText("Finish!");

        final TextView rateTv = (TextView) dlg.findViewById(R.id.rateTv_onTraining);
        rateTv.setText(rateTv.getText() + String.valueOf(totalNum));

        final Button endBtn = (Button) dlg.findViewById(R.id.endBtn_onTraining);
        final TrainingActivity Ta = (TrainingActivity)TrainingActivity.TrainingPageActivity;
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ta.finish();
            }
        });

        final Button retryBtn = (Button) dlg.findViewById(R.id.retryBtn_onTraining);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ta.finish();
                Intent intent=new Intent();
                intent.setClass(Ta, Ta.getClass());
                Ta.startActivity(intent);
            }
        });
    }

}
