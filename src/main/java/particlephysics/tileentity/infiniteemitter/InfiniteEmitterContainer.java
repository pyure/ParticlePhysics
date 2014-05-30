package particlephysics.tileentity.infiniteemitter;

import particlephysics.tileentity.emitter.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InfiniteEmitterContainer extends EmitterContainer
{

    private EmitterTileEntity machine;

    public InfiniteEmitterContainer(InventoryPlayer invPlayer, InfiniteEmitterTileEntity machine)
    {
        super(invPlayer, machine);
        this.machine = machine;
        

        // Player inventory hotbar slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 64 + 130));
        }
        // Player non-hotbar inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 64 + 72 + y * 18));
            }
        }

        // Add active fuel slot
        addSlotToContainer(new EmitterFuelSlot(machine, 0, 8, 73));
        
    }

    public EmitterTileEntity getMachine()
    {
        return this.machine;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return machine.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();

            if (i >= 36)
            {
                if (!mergeItemStack(stack, 0, 36, false))
                {
                    return null;
                }
            } else if (machine.isValidFuel(stack.itemID) || !mergeItemStack(stack, 36, 36 + machine.getSizeInventory(), false))
            {
                return null;
            }

            if (stack.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

        }
        return null;
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);

        player.sendProgressBarUpdate(this, 0, machine.fuelStored);
        player.sendProgressBarUpdate(this, 1, machine.fuelType);
        player.sendProgressBarUpdate(this, 2, machine.fuelMeta);
        player.sendProgressBarUpdate(this, 3, machine.interval);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            machine.fuelStored = data;
        }
        if (id == 1)
        {
            machine.fuelType = data;
        }
        if (id == 2)
        {
            machine.fuelMeta = data;
        }
        if (id == 3)
        {
            machine.interval = data;
        }
    }

    private int oldFuelStored;
    private int oldFuelType;
    private int oldFuelMeta;

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (Object player : crafters)
        {

            if (oldFuelStored != machine.fuelStored)
            {
                ((ICrafting) player).sendProgressBarUpdate(this, 0, machine.fuelStored);
            }
            if (oldFuelType != machine.fuelType)
            {
                ((ICrafting) player).sendProgressBarUpdate(this, 1, machine.fuelType);
            }
            if (oldFuelMeta != machine.fuelMeta)
            {
                ((ICrafting) player).sendProgressBarUpdate(this, 2, machine.fuelMeta);
            }

        }
        this.oldFuelMeta = machine.fuelMeta;
        this.oldFuelStored = machine.fuelStored;
        this.oldFuelType = machine.fuelType;
    }

}