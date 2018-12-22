package com.zippeykeys.praisebe.block;

import com.zippeykeys.praisebe.pattern.ILocalize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EBuildingBlock implements ILocalize {
    MARBLE("marble"), BASALT("basalt");

    @Getter(onMethod_ = @Override)
    private String name;

    @Override
    public String getPrefix() {
        return "building";
    }
}