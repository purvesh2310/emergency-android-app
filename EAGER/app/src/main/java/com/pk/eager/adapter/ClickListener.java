package com.pk.eager.adapter;

import android.view.View;

/**
 * Created by kimpham on 7/23/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}