package com.futuretech.test;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class TreeNode<T> {
    private T data;
    private List<TreeNode<T>> children = new ArrayList<>();

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }
}
