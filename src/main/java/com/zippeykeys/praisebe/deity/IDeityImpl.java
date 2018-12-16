package com.zippeykeys.praisebe.deity;

import com.google.common.collect.ImmutableMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@AllArgsConstructor
@Builder(toBuilder = true)
public class IDeityImpl implements IDeity {
    @Getter(onMethod_ = @Override)
    private String name;

    @Getter(onMethod_ = @Override)
    private Type type;

    @Getter(onMethod_ = @Override)
    private Element element;

    @Getter(onMethod_ = @Override)
    private Alignment alignment;

    @Singular
    private ImmutableMap<String, Relationship> relationships;

    @Override
    @Contract(pure = true)
    public @NotNull String getUnlocalizedName() {
        return "deity." + name + ".name";
    }

    @Override
    @Contract(pure = true)
    public @NotNull String getUnlocalizedDescription() {
        return "deity." + name + ".desc";
    }

    @Override
    public @NotNull Relationship getRelationship(IDeity other) {
        return Relationship.NEUTRAL;
    }
}