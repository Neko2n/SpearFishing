package dev.nekotune.spearfishing.shared.definition.recipe;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.nekotune.spearfishing.shared.registry.content.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Shapeless recipe that preserves data components
 */
public class TransmuteShapelessRecipe extends ShapelessRecipe {

    private static final int MAX_INGREDIENTS = 9;

    private final Ingredient source;
    private final List<Ingredient> extraIngredients;
    private final ItemStack result;

    public TransmuteShapelessRecipe(final String group, final CraftingBookCategory category,
            final Ingredient source, final List<Ingredient> extraIngredients, final ItemStack result) {
        super(group, category, result, allIngredients(source, extraIngredients));
        this.source = source;
        this.extraIngredients = List.copyOf(extraIngredients);
        this.result = result;
    }

    private static NonNullList<Ingredient> allIngredients(final Ingredient source,
            final List<Ingredient> extraIngredients) {
        final NonNullList<Ingredient> all = NonNullList.create();
        all.add(source);
        all.addAll(extraIngredients);
        return all;
    }

    @Override
    public ItemStack assemble(final CraftingInput input, final HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            final ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && source.test(stack)) {
                final ItemStack output = stack.transmuteCopy(result.getItem(), result.getCount());
                output.applyComponents(result.getComponentsPatch());
                return output;
            }
        }
        return super.assemble(input, registries);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.TRANSMUTE_SHAPELESS.get();
    }

    public Ingredient getSource() {
        return source;
    }

    public List<Ingredient> getExtraIngredients() {
        return extraIngredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<TransmuteShapelessRecipe> {

        private static final MapCodec<TransmuteShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(ShapelessRecipe::category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("source")
                                .forGetter(TransmuteShapelessRecipe::getSource),
                        Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("ingredients", List.of())
                                .validate(list -> list.size() < MAX_INGREDIENTS
                                        ? DataResult.success(list)
                                        : DataResult.error(() -> "Too many ingredients for transmute recipe. "
                                                + "The maximum is: " + (MAX_INGREDIENTS - 1)))
                                .forGetter(TransmuteShapelessRecipe::getExtraIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TransmuteShapelessRecipe::getResult))
                        .apply(instance, TransmuteShapelessRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TransmuteShapelessRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<TransmuteShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TransmuteShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TransmuteShapelessRecipe fromNetwork(final RegistryFriendlyByteBuf buffer) {
            final String group = buffer.readUtf();
            final CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            final Ingredient source = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            final int count = buffer.readVarInt();
            final List<Ingredient> extras = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                extras.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            final ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new TransmuteShapelessRecipe(group, category, source, extras, result);
        }

        private static void toNetwork(final RegistryFriendlyByteBuf buffer, final TransmuteShapelessRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getSource());
            buffer.writeVarInt(recipe.getExtraIngredients().size());
            for (final Ingredient ingredient : recipe.getExtraIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
        }
    }
}
