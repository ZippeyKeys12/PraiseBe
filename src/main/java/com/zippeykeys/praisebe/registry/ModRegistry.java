package com.zippeykeys.praisebe.registry;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.zippeykeys.praisebe.block.ModBlocks;
import com.zippeykeys.praisebe.block.base.PBBlock;
import com.zippeykeys.praisebe.deity.Deity;
import com.zippeykeys.praisebe.deity.ModDeities;
import com.zippeykeys.praisebe.util.ClassUtil;
import com.zippeykeys.praisebe.util.Reference;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

@UtilityClass
@EventBusSubscriber(modid = Reference.MOD_ID)
public class ModRegistry {
    public static final Set<PBBlock> BLOCKS;

    public static final Set<Deity> DEITIES;

    static {
        val blocks = ImmutableSet.<PBBlock>builder();
        Arrays.stream(ModBlocks.class.getDeclaredFields()) //
                .filter(AccessibleObject::isAccessible) //
                .map(x -> (PBBlock) ClassUtil.getFieldValue(x)) //
                .filter(Objects::nonNull) //
                .forEach(blocks::add);
        BLOCKS = blocks.build();

        val deities = ImmutableSet.<Deity>builder();
        Arrays.stream(ClassUtil.getDeclaredFields(ModDeities.class.getDeclaredClasses())) //
                .filter(AccessibleObject::isAccessible) //
                .map(x -> (Deity) ClassUtil.getFieldValue(x)) //
                .filter(Objects::nonNull) //
                .forEach(deities::add);
        DEITIES = deities.build();
    }

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
        register(event, BLOCKS);
        BLOCKS.stream() //
                .map(PBBlock::getTileEntity) //
                .filter(Objects::nonNull) //
                .forEach(tileEntity -> GameRegistry.registerTileEntity(tileEntity,
                        (ResourceLocation) Objects.requireNonNull(ClassUtil.callDeclaredMethod(tileEntity,
                                "getResource", ClassUtil.newInstance(tileEntity)))));
    }

    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        register(event, PBBlock::getItem, BLOCKS);
    }

    @SubscribeEvent
    public static void registerDeities(Register<Deity> event) {
        register(event, DEITIES);
    }

    public static <T extends IForgeRegistryEntry<T>, R extends IForgeRegistryEntry<R>, K extends T> void register(
            Register<R> e, Function<K, R> mapper, Collection<K> values) {
        register(e, mapper, values.stream());
    }

    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>, R extends IForgeRegistryEntry<R>, K extends T> void register(
            Register<R> e, Function<K, R> mapper, K... values) {
        register(e, mapper, Arrays.stream(values));
    }

    public static <T extends IForgeRegistryEntry<T>, R extends IForgeRegistryEntry<R>, K extends T> void register(
            Register<R> e, Function<K, R> mapper, Stream<K> values) {
        register(e, values.map(mapper));
    }

    public static <T extends IForgeRegistryEntry<T>, K extends T> void register(Register<T> e, Stream<K> values) {
        val r = e.getRegistry();
        values.forEach(r::register);
    }

    public static <T extends IForgeRegistryEntry<T>> void register(Register<T> e, Collection<? extends T> values) {
        val r = e.getRegistry();
        values.forEach(r::register);
    }

    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> void register(Register<T> e, T... values) {
        e.getRegistry().registerAll(values);
    }

}