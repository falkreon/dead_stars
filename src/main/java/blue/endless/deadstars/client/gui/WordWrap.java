package blue.endless.deadstars.client.gui;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class WordWrap {
	private BreakIterator breaks;
	private String locale;
	
	@Environment(EnvType.CLIENT)
	public WordWrap() {
		this(MinecraftClient.getInstance().getLanguageManager().getLanguage());
	}
	
	public WordWrap(String locale) {
		this.locale = locale;
		// Try to find a locale-sensitive object
		for(Locale l : BreakIterator.getAvailableLocales()) {
			String lCode = l.toString().toLowerCase();
			if (lCode.equals(locale)) {
				this.breaks = BreakIterator.getLineInstance(l);
				if (this.breaks != null) return;
			}
		}
		// But ANY object is better than none.
		this.breaks = BreakIterator.getLineInstance();
	}
	
	public String getLocale() { return locale; }
	
	@Environment(EnvType.CLIENT)
	public String getFirstLine(TextRenderer font, int width, String str) {
		int totalWidth = font.getWidth(str);
		if (totalWidth <= width) return str;
		
		//Scan back to find the first breakable character
		String firstLine = str;
		breaks.setText(str);
		int pos = breaks.last();
		if (pos == BreakIterator.DONE) return hardWrap(font, width, firstLine);
		firstLine = firstLine.substring(0, pos);
		
		while(font.getWidth(firstLine) > width) {
			pos = breaks.previous();
			if (pos == BreakIterator.DONE) return hardWrap(font, width, firstLine); //Failed! Cut along non-breaking lines
			firstLine = firstLine.substring(0, pos);
		}
		return firstLine;
	}
	
	@Environment(EnvType.CLIENT)
	public String hardWrap(TextRenderer font, int width, String str) {
		while(font.getWidth(str) > width) {
			str = str.substring(0, str.length()-1);
			if (str.length() <= 1) return str;
		}
		return str;
	}
	
	@Environment(EnvType.CLIENT)
	public List<String> wrap(TextRenderer font, int width, String str) {
		ArrayList<String> lines = new ArrayList<>();
		
		while(!str.isEmpty()) {
			String curLine = getFirstLine(font, width, str);
			lines.add(curLine);
			if (str.length() == curLine.length()) return lines;
			str = str.substring(curLine.length());
		}
		
		return lines;
	}
}
