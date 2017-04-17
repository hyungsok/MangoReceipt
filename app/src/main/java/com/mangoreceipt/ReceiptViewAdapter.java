package com.mangoreceipt;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mangoreceipt.protocol.Receipt;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ReceiptViewAdapter extends RecyclerView.Adapter<ReceiptViewAdapter.ViewHolder> {

    private RealmResults<Receipt> realmResults;
    private AppCompatActivity mActivity;

    public ReceiptViewAdapter(AppCompatActivity activity) {
        Realm realm = Realm.getDefaultInstance();
        updateRealmResults(realm);
        mActivity = activity;
    }

    private void updateRealmResults(Realm realm) {
        realmResults = realm.where(Receipt.class).findAll();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(realmResults.get(position));
        holder.mView.setOnClickListener(view -> {
        });
    }

    @Override
    public int getItemCount() {
        if (realmResults == null)
            return 0;
        return realmResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.icon)
        public ImageView mIcon;
        @BindView(R.id.date)
        public TextView mDate;
        @BindView(R.id.content)
        public TextView mContent;
        @BindView(R.id.edit)
        public View mEdit;
        @BindView(R.id.close)
        public View mClose;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void bind(Receipt receipt) {
            mDate.setText(receipt.getDate() + "\n#" + receipt.getWeekNumber() + "주차");
            mContent.setText(String.valueOf(receipt.getPrice()));
            mEdit.setOnClickListener(view -> {
                ReceiptDetailFragment.show(mActivity.getSupportFragmentManager(), receipt);
            });
            mClose.setOnClickListener(view -> {
                // Realm 인스턴스를 얻습니다
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                receipt.deleteFromRealm();
                updateRealmResults(realm);
                notifyDataSetChanged();
                realm.commitTransaction();
            });
        }
    }
}
