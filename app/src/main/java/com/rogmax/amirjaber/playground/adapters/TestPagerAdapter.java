package com.rogmax.amirjaber.playground.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.rogmax.amirjaber.playground.R;
import com.rogmax.amirjaber.playground.models.PagerDto;

import java.util.List;

/**
 * Created by Amir Jaber on 3/6/2018.
 */

public class TestPagerAdapter extends PagerAdapter {

    private Context context;
    private List<PagerDto> pagerDtos;
    private LayoutInflater inflater;

    public TestPagerAdapter(Context context, List<PagerDto> pagerDtos) {
        this.context = context;
        this.pagerDtos = pagerDtos;

        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return pagerDtos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_list_item, container, false);

        TextView title = view.findViewById(R.id.title);
        WebView webView = view.findViewById(R.id.code_manifest);
        view.setTag(position);
        container.addView(view);
        PagerDto pagerDto = pagerDtos.get(position);
        title.setText(pagerDto.getTitle());

        switch (position) {
            case 0:
                webView.loadUrl("file:///android_asset/code_manifest.html");
                break;
            case 1:
                webView.loadUrl("file:///android_asset/code_gradle.html");
                break;
            case 2:
                webView.loadUrl("file:///android_asset/code_adapters.html");
                break;
            case 3:
                webView.loadUrl("file:///android_asset/code_models.html");
                break;
            case 4:
                webView.loadUrl("file:///android_asset/code_retrofit.html");
                break;
            case 5:
                webView.loadUrl("file:///android_asset/code_activities.html");
                break;
            case 6:
                webView.loadUrl("file:///android_asset/code_resources.html");
                break;
        }

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
