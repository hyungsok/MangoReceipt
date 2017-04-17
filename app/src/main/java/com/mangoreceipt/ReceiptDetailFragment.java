package com.mangoreceipt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mangoreceipt.protocol.Receipt;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by hyungsoklee on 2017. 4. 8..
 */

public class ReceiptDetailFragment extends BaseDialogFragment {
    @BindView(R.id.image)
    ImageView mImage;

    @BindView(R.id.date)
    EditText mDate;

    @BindView(R.id.week)
    EditText mWeek;

    @BindView(R.id.price)
    EditText mPrice;

    private Realm mRealm;
    private Receipt mReceipt;

    public static void show(FragmentManager fragmentManager, Receipt receipt) {
        ReceiptDetailFragment fragment = new ReceiptDetailFragment();
        fragment.setReceipt(receipt);
        fragment.show(fragmentManager);
    }

    public void setReceipt(Receipt receipt) {
        this.mReceipt = receipt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReceipt != null) {
            Glide.with(this).load(mReceipt.getReceiptImageUrl()).into(mImage);
            mDate.setText(mReceipt.getDate());
            mWeek.setText(String.valueOf(mReceipt.getWeekNumber()));
            mPrice.setText(String.valueOf(mReceipt.getPrice()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_receipt, container);
        ButterKnife.bind(this, view);
        mRealm = Realm.getDefaultInstance();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }

    @OnClick(R.id.save)
    void onClickSave() {
        mRealm.beginTransaction();
        Receipt receipt = mRealm.createObject(Receipt.class);
        receipt.setPrice(getInt(mPrice));
        receipt.setDate(mDate.getText().toString());
        receipt.setReceiptImageUrl(mReceipt.getReceiptImageUrl());
        receipt.setWeekNumber(getInt(mWeek));
        mRealm.commitTransaction();
        dismiss();
    }

    private int getInt(EditText text) {
        try {
            return Integer.parseInt(text.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }



}
