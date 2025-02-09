package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICrafting
{
    void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList);

    void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack);

    void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue);

    void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_);
}
