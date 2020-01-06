package kr.co.koscom.omp.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kr.co.koscom.omp.R
import java.util.ArrayList

class MyLineChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LineChart(context, attrs, defStyleAttr) {

    init {
        setViewPortOffsets(0f, 0f, 0f, 0f)
        setBackgroundColor(Color.parseColor("#00FFFFFF"))
        getDescription().setEnabled(false)
        setDrawGridBackground(false)

        setDrawBorders(false)

        getAxisLeft().setEnabled(false)
        getAxisRight().setDrawAxisLine(false)
        getAxisRight().setDrawGridLines(false)
        getXAxis().setDrawAxisLine(false)
        getXAxis().setDrawGridLines(false)


        getLegend().setEnabled(false)

    }

    fun setData(count: Int, range: Float){

        val values = ArrayList<Entry>()

        for (i in 0 until count) {

            val `val` = (Math.random() * range).toFloat() - 30
            values.add(Entry(i.toFloat(), `val`))
        }

        setData(values)
    }

    fun setData(values: ArrayList<Entry>){

        val set1: LineDataSet

        // create a dataset and give it a type
        set1 = LineDataSet(values, "dataSet1")

        set1.setDrawHighlightIndicators(false)
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.setDrawVerticalHighlightIndicator(false)
        set1.setDrawValues(false)
        set1.setDrawCircleHole(false)
        set1.setDrawCircles(false)
        set1.setDrawIcons(false)

        // black lines and points
        set1.color = Color.WHITE

        // line thickness and point size
        set1.lineWidth = 1f

        // set the filled area
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> getAxisLeft().getAxisMinimum() }

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(this.context, R.drawable.fade_white)
            set1.fillDrawable = drawable
        } else {
            set1.fillColor = Color.BLACK
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the data sets

        // create a data object with the data sets
        val data = LineData(dataSets)

        // set data
        setData(data)
    }
}