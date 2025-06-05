package blue.endless.deadstars.client.markdown;

import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import net.minecraft.text.OrderedText;

public record SoftNode(SoftNode.Type type, String text, String value, List<SoftNode> children) {
	public SoftNode(SoftNode.Type type, List<SoftNode> children) {
		this(type, "", "", children);
	}
	
	public SoftNode(SoftNode.Type type, String text, List<SoftNode> children) {
		this(type, text, "", children);
	}
	
	public OrderedText asText() {
		List<OrderedText> nodes = new ArrayList<>();
		if (text != null && !text.isEmpty()) {
			nodes.add(net.minecraft.text.Text.literal(text).asOrderedText());
		}
		
		for(SoftNode node : children) {
			nodes.add(node.asText());
		}
		return OrderedText.concat(nodes);
	}
	
	public void appendAsString(StringBuilder builder) {
		if (text != null && !text.isEmpty()) {
			builder.append(text);
		}
		
		for(SoftNode node : children) {
			node.appendAsString(builder);
		}
	}
	
	public String asString() {
		StringBuilder builder = new StringBuilder();
		appendAsString(builder);
		return builder.toString();
	}
	
	public static SoftNode of(Node node) {
		return switch(node) {
			case Text text -> new SoftNode(Type.TEXT, text.getLiteral(), normalizeChildren(node));
			case Code code -> new SoftNode(Type.CODE, code.getLiteral(), normalizeChildren(node));
			case FencedCodeBlock fencedBlock -> new SoftNode(Type.FENCED_CODE_BLOCK, fencedBlock.getLiteral(), fencedBlock.getInfo(), normalizeChildren(node));
			case HardLineBreak hbr -> new SoftNode(Type.HARD_LINE_BREAK, "\n", normalizeChildren(node));
			case Heading heading -> new SoftNode(Type.HEADING, "", ""+heading.getLevel(), normalizeChildren(node));
			case HtmlBlock htmlBlock -> new SoftNode(Type.HTML_BLOCK, htmlBlock.getLiteral(), normalizeChildren(node));
			case HtmlInline htmlInline -> new SoftNode(Type.HTML_INLINE, htmlInline.getLiteral(), normalizeChildren(node));
			case Image image -> new SoftNode(Type.IMAGE, image.getTitle(), image.getDestination(), normalizeChildren(node));
			case IndentedCodeBlock indentedBlock -> new SoftNode(Type.INDENTED_CODE_BLOCK, indentedBlock.getLiteral(), normalizeChildren(node));
			case Link link -> new SoftNode(Type.LINK, link.getTitle(), link.getDestination(), normalizeChildren(node));
			// We'll generally want to skip these, but at least make sure the text is empty. This typically gets "rendered" as an html anchor.
			case LinkReferenceDefinition ref -> new SoftNode(Type.LINK_REFERENCE_DEFINITION, "", ref.getLabel() + ":" + ref.getDestination() + ":" + ref.getTitle(), normalizeChildren(node));
			case OrderedList ordered -> new SoftNode(Type.ORDERED_LIST, "", computeIfAbsent(ordered.getMarkerStartNumber(), 1).toString(), normalizeChildren(node));
			case SoftLineBreak soft -> new SoftNode(Type.SOFT_LINE_BREAK, " ", normalizeChildren(node));
			
			default -> new SoftNode(Type.forClass(node.getClass()), normalizeChildren(node));
		};
	}
	
	public static List<SoftNode> normalizeChildren(Node root) {
		Node node = root.getFirstChild();
		List<SoftNode> result = new ArrayList<>();
		while (node != null) {
			Node next = node.getNext();
			result.add(of(node));
			node = next;
		}
		return List.copyOf(result);
	}
	
	private static <T> T computeIfAbsent(T t, T defaultValue) {
		return (t == null) ? defaultValue : t;
	}
	
	public static enum Type {
		BLOCK_QUOTE(BlockQuote.class, 1, true, 2),
		BULLET_LIST(BulletList.class, 1, true, 4),
		CODE(Code.class, 0, true, 2),
		CUSTOM_BLOCK(CustomBlock.class, 0, true, 2),
		CUSTOM_NODE(CustomNode.class, 0, false, 0),
		DOCUMENT(Document.class, 0, true, 0),
		EMPHASIS(Emphasis.class, 0, false, 0),
		FENCED_CODE_BLOCK(FencedCodeBlock.class, 0, true, 2),
		HARD_LINE_BREAK(HardLineBreak.class, 0, false, 0),
		HEADING(Heading.class, 0, true, 4),
		HTML_BLOCK(HtmlBlock.class, 1, true, 2),
		HTML_INLINE(HtmlInline.class, 0, false, 0),
		IMAGE(Image.class, 0, false, 0),
		INDENTED_CODE_BLOCK(IndentedCodeBlock.class, 1, true, 2),
		LINK(Link.class, 0, false, 0),
		LINK_REFERENCE_DEFINITION(LinkReferenceDefinition.class, 0, false, 0),
		LIST_ITEM(ListItem.class, 0, false, 2),
		ORDERED_LIST(OrderedList.class, 1, true, 4),
		PARAGRAPH(Paragraph.class, 0, true, 4),
		SOFT_LINE_BREAK(SoftLineBreak.class, 0, false, 0),
		STRONG_EMPHASIS(StrongEmphasis.class, 0, false, 0),
		TEXT(Text.class, 0, false, 0),
		THEMATIC_BREAK(ThematicBreak.class, 0, true, 4);
		
		private final Class<? extends Node> nodeClass;
		private final int indentValue;
		private final boolean isBlock;
		private final int marginBottom;
		
		Type(Class<? extends Node> nodeClass, int indentValue, boolean isBlock, int marginBottom) {
			this.nodeClass = nodeClass;
			this.indentValue = indentValue;
			this.isBlock = isBlock;
			this.marginBottom = marginBottom;
		}
		
		public static SoftNode.Type forClass(Class<? extends Node> clazz) {
			for(SoftNode.Type value : values()) {
				if (value.nodeClass == clazz) return value;
			}
			
			return Type.CUSTOM_NODE;
		}
		
		public Class<? extends Node> nodeClass() { return nodeClass; }
		public int indentValue() { return indentValue; }
		public boolean isBlockElement() { return isBlock; }
		public boolean isInline() { return !isBlock; }
		public int marginBottom() { return marginBottom; }
	}
}