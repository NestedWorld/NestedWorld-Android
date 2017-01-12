package com.nestedworld.nestedworld.ui.view.connected.friend;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import butterknife.BindView;

public class FriendCatchFragment extends BaseFragment {
    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.item_piechart_total)
    View viewPieChartTotal;
    @BindView(R.id.item_piechart_pvp)
    View viewPieChartPvp;
    @BindView(R.id.item_piechart_pve)
    View viewPieChartPve;
    @BindView(R.id.item_piechart_portals)
    View viewPieChartPortal;

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_friend_stats;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        setupCharts();
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setupPieChar(@NonNull final View pieChartItem,
                              @NonNull final String title,
                              @Nullable final PieData pieData) {
        //Retrieve widget
        final TextView textViewTitle = (TextView) pieChartItem.findViewById(R.id.textview_piechart_friend_title);
        final PieChart pieChart = (PieChart) pieChartItem.findViewById(R.id.piechart_friend);

        //Populate widget
        textViewTitle.setText(title);
        pieChart.setData(pieData);
    }

    private void setupCharts() {
        final PieDataSet pieDataSet = new PieDataSet(null, "");
        pieDataSet.setColor(Color.RED);

        final PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        setupPieChar(viewPieChartTotal, "Total", pieData);
        setupPieChar(viewPieChartPvp, "Pvp", pieData);
        setupPieChar(viewPieChartPve, "Pve", pieData);
        setupPieChar(viewPieChartPortal, "Portal", pieData);
    }
}
