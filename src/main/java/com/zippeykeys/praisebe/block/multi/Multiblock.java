package com.zippeykeys.praisebe.block.multi;

import java.util.List;
import java.util.Optional;

import com.zippeykeys.praisebe.util.Localize;

import org.apache.logging.log4j.util.TriConsumer;
import org.immutables.builder.Builder.Factory;
import org.immutables.builder.Builder.Parameter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Singular;
import lombok.var;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@AllArgsConstructor
public class Multiblock implements Localize {
    @Getter(onMethod_ = @Override)
    private final String name;

    @Getter
    private final EMBActivator activator;

    @Getter
    private final IBlockState trigger;

    @Getter
    @Singular
    public final List<MultiblockPart> parts;

    private final TriConsumer<Multiblock, World, BlockPos> structureBuilder;

    @Factory
    @Contract("_, _, _, _, _ -> new")
    public static Multiblock multiblock(String name, Optional<EMBActivator> activator, IBlockState trigger,
            List<MultiblockPart> parts, @Parameter TriConsumer<Multiblock, World, BlockPos> structureBuilder) {
        return new Multiblock(name, activator.orElse(EMBActivator.values()[0]), trigger, parts, structureBuilder);
    }

    public boolean isValid(World world, BlockPos pos, EntityPlayer player) {
        for (var i = 0; i < 4; i++) {
            if (checkStructure(world, pos, player)) {
                return true;
            }
            getParts().forEach(MultiblockPart::rotate);
        }
        return false;
    }

    public @Nullable MultiblockPart getPartAt(BlockPos pos) {
        return getParts() //
                .stream() //
                .filter(x -> x.getPos() == pos) //
                .findAny() //
                .orElse(null);
    }

    public boolean checkStructure(World world, BlockPos pos, EntityPlayer player) {
        return getParts() //
                .stream() //
                .allMatch(x -> world.getBlockState(pos.add(x.getPos())).equals(x.getBlockState()));
    }

    public void buildStructure(World world, BlockPos blockPos) {
        structureBuilder.accept(this, world, blockPos);
    }

    @Override
    public String getPrefix() {
        return "multiblock";
    }
}