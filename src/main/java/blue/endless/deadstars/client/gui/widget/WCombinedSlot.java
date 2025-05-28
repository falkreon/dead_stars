package blue.endless.deadstars.client.gui.widget;

import java.util.function.Predicate;

import com.google.common.base.Predicates;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.inventory.Inventory;

/**
 * Represents a gui zone that is both a fluid tank and a bank of item slots. Items render on top of fluids.
 * 
 * If this is an input slot, fluid containers on the item foreground will be emptied into the fluid background, and if
 * this is an output slot, placed fluid containers will be filled from the fluid in the background.
 * 
 * These combined slots are because machines can be fed with peristalsic vines, which carry both items and fluids.
 */
//TODO: Sync fluids
public class WCombinedSlot extends WItemSlot {
	
	protected int tankIndex;
	protected Predicate<FluidVariant> fluidInputFilter = Predicates.alwaysTrue();
	protected Predicate<FluidVariant> fluidOutputFilter = Predicates.alwaysTrue();
	protected ItemFluidBehavior itemFluidBehavior = ItemFluidBehavior.NONE;
	
	public WCombinedSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big, int tankIndex) {
		super(inventory, startIndex, slotsWide, slotsHigh, big);
		this.tankIndex = tankIndex;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters() {
		super.addPainters();
		
		//TODO: Add the fluid renderer here
	}
	
	public void setFluidInputFilter(Predicate<FluidVariant> pred) {
		this.fluidInputFilter = pred;
	}
	
	public void setFluidOutputFilter(Predicate<FluidVariant> pred) {
		this.fluidOutputFilter = pred;
	}
	
	public int getTankIndex() {
		return tankIndex;
	}
	
	public Predicate<FluidVariant> fluidInputFilter() { return fluidInputFilter; }
	public Predicate<FluidVariant> fluidOutputFilter() { return fluidOutputFilter; }
	
	public static enum ItemFluidBehavior {
		NONE,
		FILL_TANK,
		DRAIN_TANK;
	}
}
