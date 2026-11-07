package dev.nekotune.spearfishing.shared.registry.content;

import java.util.function.Supplier;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.shared.definition.recipe.TransmuteShapelessRecipe;
import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipeSerializers {

    private static final IRegistrationProvider<RecipeSerializer<?>> RECIPE_SERIALIZERS = IRegistrationProvider
            .get(Registries.RECIPE_SERIALIZER, SpearFishing.MOD_ID);

    public static final Supplier<TransmuteShapelessRecipe.Serializer> TRANSMUTE_SHAPELESS = RECIPE_SERIALIZERS
            .register("crafting_transmute_shapeless", TransmuteShapelessRecipe.Serializer::new);

    public static void init() {
    }

    private ModRecipeSerializers() {
    }
}
