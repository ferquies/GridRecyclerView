package com.example.fernandoquilez.gridrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Q. Esquitino
 * Mail: fernando.quilez@fromthebenchgames.com
 * 30/05/17.
 */
class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.BaseViewHolder> {

  static final int ITEM = 0;
  static final int LONG_ITEM = 1;
  static final int HEADER_ITEM = 2;
  static final int FOOTER_ITEM = 3;
  boolean isOpen = false;
  private List<String> mData = new ArrayList<>();
  private List<String> mRemoveElements = new ArrayList<>();
  private LayoutInflater mInflater;
  private ItemClickListener mClickListener;
  private int lastPositionOpen = -1;
  private int lastPositionClicked = -1;

  MyRecyclerViewAdapter(Context context, List<String> data) {
    this.mInflater = LayoutInflater.from(context);
    this.mData.addAll(data);
    this.mRemoveElements.addAll(data);
    clearDataSet(0);
  }

  private void clearDataSet(int positionNotRemove) {
    for (int position = 0; position < mRemoveElements.size(); position++) {
      if ("".equals(mRemoveElements.get(position)) && position != positionNotRemove) {
        mRemoveElements.remove(position);
      }
    }
  }

  @Override public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    BaseViewHolder viewHolder;

    if (viewType == LONG_ITEM) {
      view = mInflater.inflate(R.layout.long_item, parent, false);
      viewHolder = new LongItemViewHolder(view);
    } else if (viewType == HEADER_ITEM) {
      view = mInflater.inflate(R.layout.header_item, parent, false);
      viewHolder = new HeaderItemViewHolder(view);
    } else if (viewType == FOOTER_ITEM) {
      view = mInflater.inflate(R.layout.footer_item, parent, false);
      viewHolder = new FooterItemViewHolder(view);
    } else {
      view = mInflater.inflate(R.layout.item, parent, false);
      viewHolder = new ItemViewHolder(view);
    }

    return viewHolder;
  }

  @Override public void onBindViewHolder(BaseViewHolder holder, int position) {
    if (holder instanceof ItemViewHolder) {
      ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
      itemViewHolder.textView.setText(mRemoveElements.get(position));

      if ("-1".equals(mRemoveElements.get(position))) {
        itemViewHolder.overlay.setVisibility(View.VISIBLE);
      } else {
        itemViewHolder.overlay.setVisibility(View.GONE);
      }
    } else if (holder instanceof HeaderItemViewHolder) {
      ((HeaderItemViewHolder) holder).headerTitle.setText(
          mRemoveElements.get(position).split("/")[1]);
    } else if (holder instanceof FooterItemViewHolder) {

    } else {
      ((LongItemViewHolder) holder).longTextView.setText("Hello world!");
    }
  }

  void toggleItemVisibility(int position) {
    if ("-1".equals(mRemoveElements.get(position))) {
      return;
    }

    if(isOpen && lastPositionOpen < position) {
      position--;
    }

    int origPosition = position - 1;
    int headersFootersCount = getHeadersAndFootersCount(position);
    position++;

    position = position - headersFootersCount;

    if (position % 7 != 0 && (lastPositionOpen >= position || lastPositionOpen == -1)) {
      position++;
    }

    while ((position < 2 || position % 7 != 0)) {
      position++;
    }

    position = position + headersFootersCount;

    if (position >= mRemoveElements.size()) {
      position = mRemoveElements.size() - 1;
    }

    if (lastPositionOpen >= mRemoveElements.size()) {
      lastPositionOpen = mRemoveElements.size() - 1;
    }

    if (isOpen && (origPosition == lastPositionClicked || position != lastPositionOpen)) {
      isOpen = false;
      mRemoveElements.remove(lastPositionOpen);
      notifyItemRangeRemoved(lastPositionOpen, 1);
    }

    if (!isOpen && position != lastPositionOpen) {
      mRemoveElements.add(position, "");
      lastPositionOpen = position;
      lastPositionClicked = origPosition;
      notifyItemRangeInserted(position, 1);
      isOpen = true;
    }

    if (!isOpen) {
      lastPositionOpen = -1;
    }
  }

  private int getHeadersAndFootersCount(int position) {
    int headersAndFootersCount = 0;

    for (int index = 0; index < position; index++) {
      if (getItemViewType(index) == HEADER_ITEM || getItemViewType(index) == FOOTER_ITEM) {
        headersAndFootersCount++;
      }
    }

    return headersAndFootersCount;
  }

  @Override public int getItemCount() {
    return mRemoveElements.size();
  }

  @Override public int getItemViewType(int position) {
    if ("".equals(mRemoveElements.get(position))) {
      return LONG_ITEM;
    } else if ("HEADER".equals(mRemoveElements.get(position).split("/")[0])) {
      return HEADER_ITEM;
    } else if ("FOOTER".equals(mRemoveElements.get(position))) {
      return FOOTER_ITEM;
    }

    return ITEM;
  }

  void setClickListener(ItemClickListener itemClickListener) {
    this.mClickListener = itemClickListener;
  }

  interface ItemClickListener {
    void onItemClick(View view, int position);
  }

  class BaseViewHolder extends RecyclerView.ViewHolder {
    BaseViewHolder(View itemView) {
      super(itemView);
    }
  }

  class HeaderItemViewHolder extends BaseViewHolder {
    TextView headerTitle;

    HeaderItemViewHolder(View headerView) {
      super(headerView);
      headerTitle = (TextView) headerView.findViewById(R.id.header_title);
    }
  }

  class FooterItemViewHolder extends BaseViewHolder {
    FooterItemViewHolder(View footerView) {
      super(footerView);
    }
  }

  class ItemViewHolder extends BaseViewHolder implements View.OnClickListener {
    TextView textView;
    View overlay;

    ItemViewHolder(View itemView) {
      super(itemView);
      textView = (TextView) itemView.findViewById(R.id.textView);
      overlay = itemView.findViewById(R.id.overlay);
      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }
  }

  class LongItemViewHolder extends BaseViewHolder {
    TextView longTextView;

    LongItemViewHolder(View itemView) {
      super(itemView);
      longTextView = (TextView) itemView.findViewById(R.id.textViewLong);
    }
  }
}
