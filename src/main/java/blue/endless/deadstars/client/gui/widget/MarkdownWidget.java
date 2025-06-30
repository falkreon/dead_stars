package blue.endless.deadstars.client.gui.widget;

import java.util.ArrayDeque;
import java.util.List;

import blue.endless.deadstars.client.gui.WordWrap;
import blue.endless.deadstars.client.markdown.SoftNode;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MarkdownWidget extends WWidget {
	protected SoftNode root;
	
	@Environment(EnvType.CLIENT)
	protected WordWrap wordWrap;
	
	@Environment(EnvType.CLIENT)
	protected TextRenderer font;
	
	public MarkdownWidget() {
		
	}
	
	public void setDocument(SoftNode root) {
		this.root = root;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (font == null) font = MinecraftClient.getInstance().textRenderer;
		if (wordWrap == null) wordWrap = new WordWrap();
		
		/*
		 * Peeking this stack will give you the "block context" for this block.
		 * A block context gives you your limits: how far up and left you can go, and how much width you have left.
		 */
		ArrayDeque<BlockContext> contextStack = new ArrayDeque<>();
		contextStack.push(new BlockContext(x + 8, y + 8, this.getWidth() - 24));
		paint(root, context, x + 8, y + 8, contextStack, Style.EMPTY);
		
		//context.enableScissor(x, y, x + width, y + height);
		//Vec2i pos = new Vec2i(x + 8, y + 8);
		//for(SoftNode child : root.children()) {
		//	pos = paint(child, context, pos.x(), pos.y(), contextStack);
		//}
		
		//context.disableScissor();
	}
	
	@Environment(EnvType.CLIENT)
	private Vec2i paint(SoftNode node, DrawContext context, int x, int y, ArrayDeque<BlockContext> contextStack, Style externalStyle) {
		BlockContext blockContext = contextStack.peek();
		if (blockContext == null) return new Vec2i(x, y);
		
		if (node.type().isBlockElement()) {
			// Try and figure out where we sit relative to our outer context
			if (x == blockContext.x()) {
				// We're at the start of the line, regardless of where we are vertically. Accept this location.
			} else {
				// We're somewhere in the middle of the enclosing block. Don't accept this location.
				// Break out of whatever line context we're in.
				y += 16;
				x = blockContext.x();
			}
			
			int blockIndent = node.type().indentValue() * 8;
			int blockLeft = blockContext.x() + blockIndent;
			int blockTop = y;
			int blockWidth = blockContext.width() - blockIndent;
			
			blockContext = new BlockContext(blockLeft, blockTop, blockWidth);
			contextStack.push(blockContext);
			
			Vec2i pos2 = new Vec2i(blockLeft, blockTop);
			
			Style imposedStyle = switch(node.type()) {
				case SoftNode.Type.HEADING -> {
					if (node.value().equals("1")) yield externalStyle.withBold(true).withColor(Formatting.AQUA);
					if (node.value().equals("2")) yield externalStyle.withBold(false).withColor(Formatting.LIGHT_PURPLE);
					yield externalStyle.withBold(false).withColor(Formatting.BLUE);
				}
				default -> externalStyle;
			};
			
			for(SoftNode child : node.children()) {
				pos2 = paint(child, context, pos2.x(), pos2.y(), contextStack, imposedStyle);
			}
			
			contextStack.pop();
			BlockContext outer = contextStack.peek();
			if (outer == null) outer = blockContext;
			Vec2i baseResult = (pos2.x() == blockContext.x()) ?
				new Vec2i(outer.x(), pos2.y() + node.type().marginBottom())
				:
				new Vec2i(outer.x(), pos2.y() + font.fontHeight + node.type().marginBottom());
			
			//Vec2i baseResult = new Vec2i(blockContext.x(), pos2.y() + font.fontHeight + node.type().marginBottom());
			
			return baseResult;
		} else {
			// We're doing a word wrap
			
			int presumedIndent = x - blockContext.x();
			int firstLineWidth = blockContext.width() - presumedIndent;
			
			String nodeString = node.asString();
			if (node.type() == SoftNode.Type.LIST_ITEM) nodeString = "\u2022 " + nodeString;
			
			String firstLine = wordWrap.getFirstLine(font, firstLineWidth, nodeString);
			String remaining = nodeString.substring(firstLine.length());
			List<String> lines = wordWrap.wrap(font, blockContext.width(), remaining);
			
			Style style = switch(node.type()) {
				case SoftNode.Type.EMPHASIS -> {
					//TODO: Switch on '_' vs '*'?
					yield externalStyle.withItalic(true);
				}
				case SoftNode.Type.STRONG_EMPHASIS -> {
					if (node.value().startsWith("_")) {
						yield externalStyle.withUnderline(true);
					} else {
						yield externalStyle.withBold(true);
					}
				}
				default -> externalStyle;
			};
			int textColor = 0xFF_FFFFFF;
			//int textColor = switch(node.type()) {
			//	case SoftNode.Type.HEADING -> Formatting.AQUA.getColorValue();
			//	default -> 0xFF_FFFFFF;
			//};
			
			boolean breakAfter = switch(node.type()) {
				case SoftNode.Type.LIST_ITEM -> true;
				default -> false;
			};
			
			OrderedText firstLineText = Text.literal(firstLine).setStyle(style).asOrderedText();
			//int textColor = (style.getColor() == null) ? -1 : style.getColor().getRgb();
			context.drawText(font, firstLineText, x, y, textColor, style.getShadowColor() != null);
			
			//ScreenDrawing.drawString(context, firstLineText, x, y, 0xFF_FFFFFF);
			
			if (lines.isEmpty()) {
				if (breakAfter) {
					return new Vec2i(blockContext.x(), y + font.fontHeight + node.type().marginBottom());
				} else {
					return new Vec2i(x + font.getWidth(firstLineText), y);
				}
			} else {
				Vec2i pos2 = new Vec2i(blockContext.x(), y + font.fontHeight);
				for(int i=0; i<lines.size(); i++) {
					String line = lines.get(i);
					OrderedText lineText = Text.literal(line).setStyle(style).asOrderedText();
					ScreenDrawing.drawString(context, Text.literal(line).setStyle(style).asOrderedText(), pos2.x(), pos2.y() + (i * font.fontHeight), 0xFF_FFFFFF);
					if (i == lines.size() - 1) {
						// Put us at the end of the last line.
						if (breakAfter) {
							pos2 = new Vec2i(blockContext.x(), pos2.y() + (font.fontHeight * lines.size()) + node.type().marginBottom());
						} else {
							int newX = pos2.x() + font.getWidth(lineText);
							pos2 = new Vec2i(newX, pos2.y() + (i * font.fontHeight));
						}
					}
				}
				
				
				return pos2;
			}
			/*
			switch(node.type()) {
				case SoftNode.Type.TEXT -> {
					//font.wrapLines(node.asText(), )
					ScreenDrawing.drawString(context, node.asText(), x, y, 0xFF_FFFFFF);
					return new Vec2i(x, y+font.fontHeight);
				}
				
				case SoftNode.Type.HEADING -> {
					ScreenDrawing.drawString(context, node.asText(), x, y, 0xFF_CCFFFF);
					return new Vec2i(x, y+font.fontHeight);
				}
				
				case SoftNode.Type.EMPHASIS -> {
					ScreenDrawing.drawString(context, node.asText(), x, y, 0xFF_FFCCFF);
					return new Vec2i(x, y+font.fontHeight);
				}
				
				case SoftNode.Type.LIST_ITEM -> {
					// TODO: Check somehow if this is bulleted or numbered
					OrderedText bulletText = OrderedText.concat(Text.literal("\u2022 ").asOrderedText(), node.asText());
					ScreenDrawing.drawString(context, bulletText, x, y, 0xFF_FFFFFF);
					return new Vec2i(x, y+font.fontHeight);
				}
				
				default -> {
					ScreenDrawing.drawString(context, node.asText(), x, y, 0xFF_FFFFCC);
					return new Vec2i(x, y+font.fontHeight);
				}
				
			}*/
		}
	}
	
	private static void text(DrawContext context, Text text, int x, int y) {
		
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	public static record BlockContext(int x, int y, int width) {};
}
