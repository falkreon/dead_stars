package blue.endless.deadstars.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class FruitpadScreen extends CottonClientScreen {

	protected FruitpadScreen(Text title) {
		super(new FruitpadGui());
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		System.out.println("Size: "+this.width+" x "+this.height);
	}
	
}
