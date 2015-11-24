package com.example.administrator.cyut_molosport.Member.Chart;

import android.content.Context;
import android.widget.TextView;

import com.example.administrator.cyut_molosport.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2015/10/27.
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource){
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    //callbacks everytime the MarkView is redrawn, can be used to update the
    //content(user-interface)

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry){

            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        }else {
            tvContent.setText("" + Utils.formatNumber(e.getVal(),0 , true));
        }
    }

    @Override
    public int getXOffset() {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        return -getHeight();
    }
}
