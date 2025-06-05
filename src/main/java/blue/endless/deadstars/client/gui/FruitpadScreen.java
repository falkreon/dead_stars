package blue.endless.deadstars.client.gui;

import blue.endless.deadstars.DeadStarsMod;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.util.Identifier;

public class FruitpadScreen extends CottonClientScreen {

	public FruitpadScreen() {
		super(new FruitpadGui(DeadStarsMod.identifier("not_found"), 0xFF_FFFFFF));
	}
	
	public FruitpadScreen(Identifier logId, int fruitpadColor) {
		super(new FruitpadGui(logId, fruitpadColor));
	}
	
}
