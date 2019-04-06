package com.example.mycommunity.function;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mycommunity.R;

import java.util.ArrayList;
import java.util.List;

public class FunctionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.function_layout, container, false);
        init(view);
        return view;
    }

    //测试
    private void init(View view) {
        Function[] functions = new Function[]{
                new Function(R.drawable.ic_user, "立即报修"),
                new Function(R.drawable.ic_user, "送水服务"),
                new Function(R.drawable.ic_user, "找书记")};
        List<Function> functionList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            functionList.add(functions[i]);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        RecyclerView recyclerView = view.findViewById(R.id.function_recycle_view);
        recyclerView.setLayoutManager(layoutManager);
        FunctionCardAdapter functionCardAdapter = new FunctionCardAdapter(functionList);
        recyclerView.setAdapter(functionCardAdapter);
    }
}