package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CreativeCrafting implements ICrafting
{
    private final Minecraft mc;

    public CreativeCrafting(Minecraft mc)
    {
        this.mc = mc;
    }

    public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList)
    {
    }

    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        this.mc.playerController.sendSlotPacket(stack, slotInd);
    }

    public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue)
    {
    }

    public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_)
    {
    }
}
