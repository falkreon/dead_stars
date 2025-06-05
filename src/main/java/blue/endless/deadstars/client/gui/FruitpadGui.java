package blue.endless.deadstars.client.gui;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.client.DeadStarsClient;
import blue.endless.deadstars.client.gui.widget.MarkdownWidget;
import blue.endless.deadstars.client.markdown.SoftNode;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.util.Identifier;

public class FruitpadGui extends LightweightGuiDescription {
	protected SoftNode root;
	protected int fruitpadColor;
	
	public FruitpadGui(Identifier logId, int fruitpadColor) {
		this.setTitleVisible(false);
		root = DeadStarsClient.FRUITPAD_ENTRIES.getEntry(logId);
		this.fruitpadColor = fruitpadColor;
		
		
		WPlainPanel rootPanel = new WPlainPanel();
		rootPanel.setInsets(Insets.NONE);
		rootPanel.setSize(427, 240);
		
		MarkdownWidget md = new MarkdownWidget();
		md.setDocument(root);
		rootPanel.add(md, 0, 0, 427, 240);
		md.setSize(427, 240);
		
		this.setRootPanel(rootPanel);
	}
	
	@Override
	public void addPainters() {
		BackgroundPainter painter = BackgroundPainter.createGuiSprite(DeadStarsMod.identifier("fruitpad_bg"));
		this.rootPanel.setBackgroundPainter(painter);
	}
	
}
