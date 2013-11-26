package mods.architecttable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SingleItemSlot extends Slot {
	public SingleItemSlot(IInventory inv, int id, int x, int y) {
		super(inv, id, x, y);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
