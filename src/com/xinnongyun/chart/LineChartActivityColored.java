package com.xinnongyun.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.xinnongyun.utils.StringUtil;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class LineChartActivityColored {

	private LineChartView chart;
	private LineChartData data;
	private int numberOfLines = 1;
	private int maxNumberOfLines = 4;
	private int numberOfPoints = 0;

	float[][] randomNumbersTab;

	private boolean hasAxes = true;
	//设置是否有xy标题
	private boolean hasAxesNames = false;
	private boolean hasLines = true;
	private boolean hasPoints = false;
	private ValueShape shape = ValueShape.CIRCLE;
	private boolean isFilled = false;
	private boolean hasLabels = true;
	//false 折线   true曲线
	private boolean isCubic = false;
	private boolean hasLabelForSelected = false;
	List<AxisValue> axisValues = new ArrayList<AxisValue>();
	private Axis axisX = new Axis();
	private Axis axisY = new Axis().setHasLines(true);
	public View onCreateView(Context context,List<double[]> chartNums,List<Date[]> dateList,int minY,int maxY) {
		chart = new LineChartView(context);
		maxNumberOfLines = chartNums.size();
		Date[] dates = dateList.get(0);
		numberOfPoints = chartNums.get(0).length;
		randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
		for (int i = 0; i < numberOfLines; ++i) {
			
			for (int j = 0; j < numberOfPoints; ++j) {
				randomNumbersTab[i][j] = (float)chartNums.get(i)[j];
			}
		}
		
		generateData(minY,maxY,dates);
		return chart;
	}

	
	private void generateData(int minY,int maxY,Date[] dates) {

		List<Line> lines = new ArrayList<Line>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date da = StringUtil.stringConverToDate(formatter.format(new Date(System.currentTimeMillis()))+" 00:00:00");
		List<PointValue> valuesTwo = new ArrayList<PointValue>();
		valuesTwo.add(new PointValue(da.getTime(), minY));
		valuesTwo.add(new PointValue(da.getTime() + 24 * 60 * 60 * 1000, maxY));
		Line lineTwo = new Line(valuesTwo);
		lineTwo.setColor(Color.TRANSPARENT);
		lines.add(lineTwo);
		for (int i = 0; i < numberOfLines; ++i) {

			List<PointValue> values = new ArrayList<PointValue>();
			for (int j = 0; j < numberOfPoints; ++j) {
				if(dates!=null && dates[j]!=null && !dates[j].toString().equals("null"))
				values.add(new PointValue(dates[j].getTime(), randomNumbersTab[i][j]));
			}

			Line line = new Line(values);
			line.setColor(0xff0292db);
			line.setShape(shape);
			line.setCubic(isCubic);
			line.setFilled(isFilled);
			line.setHasLabels(hasLabels);
			line.setHasLabelsOnlyForSelected(hasLabelForSelected);
			line.setHasLines(hasLines);
			line.setHasPoints(hasPoints);
			
			lines.add(line);
		}
		
		data = new LineChartData(lines);
		
		if (hasAxes) {
			if (hasAxesNames) {
				axisX.setName("Axis X");
				axisY.setName("Axis Y");
			}
			try {
				
				for(int k = 0;k < 9; k++)
					{
					    float lableX = da.getTime() + k * 3 * 60 * 60 *1000; 
						axisValues.add(new AxisValue(lableX,(k * 3 + ":00").toCharArray()));
						
					}
			} catch (Exception e) {
			}
			data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
			axisY.setMaxLabelChars((maxY+"").length()+2);
			data.setAxisYLeft(axisY);
		} else {
			data.setAxisXBottom(null);
			data.setAxisYLeft(null);
		}

		data.setBaseValue(Float.NEGATIVE_INFINITY);
		
		chart.setLineChartData(data);

		
	}
}
