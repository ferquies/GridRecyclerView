package com.example.fernandoquilez.gridrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
    implements MyRecyclerViewAdapter.ItemClickListener {

  private static final Map<Integer, Integer> DAYS_WEEK = new HashMap<Integer, Integer>() {
    {
      put(Calendar.MONDAY, 0);
      put(Calendar.TUESDAY, 1);
      put(Calendar.WEDNESDAY, 2);
      put(Calendar.THURSDAY, 3);
      put(Calendar.FRIDAY, 4);
      put(Calendar.SATURDAY, 5);
      put(Calendar.SUNDAY, 6);
    }
  };
  MyRecyclerViewAdapter adapter;
  Calendar calendar;
  List<String> data;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    data = new ArrayList<>();
    calendar = Calendar.getInstance();

    calendar.set(Calendar.DAY_OF_MONTH, 1);

    for (int month = 0; month < 12; month++) {
      calendar.set(Calendar.MONTH, month);
      calculateCalendar();
    }

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        switch (adapter.getItemViewType(position)) {
          case MyRecyclerViewAdapter.ITEM:
            return 1;
          case MyRecyclerViewAdapter.LONG_ITEM:
            return 7;
          case MyRecyclerViewAdapter.HEADER_ITEM:
            return 7;
          case MyRecyclerViewAdapter.FOOTER_ITEM:
            return 7;
          default:
            return -1;
        }
      }
    });

    recyclerView.setLayoutManager(layoutManager);
    adapter = new MyRecyclerViewAdapter(this, data);
    adapter.setClickListener(this);
    recyclerView.setAdapter(adapter);
  }

  private void calculateCalendar() {
    int daysPreviousMonth = 0;
    Calendar c = (Calendar) calendar.clone();
    List<String> days = new ArrayList<>();
    days.add(
        "HEADER/" + new SimpleDateFormat("MMMM YYYY", Locale.getDefault()).format(c.getTime()));

    if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
      daysPreviousMonth =
          DAYS_WEEK.get(Calendar.MONDAY) - DAYS_WEEK.get(calendar.get(Calendar.DAY_OF_WEEK));
    }

    c.add(Calendar.DAY_OF_YEAR, daysPreviousMonth - 1);

    for (int day = 1; day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - daysPreviousMonth;
        day++) {
      c.add(Calendar.DAY_OF_YEAR, 1);

      if (c.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)
          || c.get(Calendar.YEAR) < calendar.get(Calendar.YEAR)) {
        days.add("-1");
      } else {
        days.add(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
      }
    }

    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      c.add(Calendar.DAY_OF_YEAR, 1);

      if (c.get(Calendar.MONTH) > calendar.get(Calendar.MONTH)
          || c.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)) {
        days.add("-1");
      } else {
        days.add(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
      }
    }

    days.add("FOOTER");

    data.addAll(days);
  }

  @Override public void onItemClick(View view, int position) {
    adapter.toggleItemVisibility(position);
  }
}
