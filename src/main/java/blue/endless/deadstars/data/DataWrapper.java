package blue.endless.deadstars.data;

import java.io.IOException;
import java.io.InputStream;

public class DataWrapper {
	private InputStream in;
	private boolean eof = false;
	private long position = 0;
	
	public DataWrapper(InputStream in) {
		this.in = in;
	}
	
	public long position() {
		return position;
	}
	
	public int i8() throws IOException {
		if (eof) return 0x00;
		
		int res = in.read();
		position++;
		
		if (res == -1) {
			eof = true;
			return 0x00;
		}
		
		return res;
	}
	
	public int i16le() throws IOException {
		return i8() | (i8() << 8);
	}
	
	public int i32le() throws IOException {
		return
				 i8() |
				(i8() <<  8) |
				(i8() << 16) |
				(i8() << 24);
	}
	
	public int i16be() throws IOException {
		return (i8() << 8) | i8();
	}
	
	public int i32be() throws IOException {
		return
				(i8() << 24) |
				(i8() << 16) |
				(i8() <<  8) |
				 i8();
	}
	
	public void seekTo(long position) throws IOException {
		if (position < this.position) throw new IOException("Cannot seek backwards in an InputStream.");
		long toSkip = position - this.position;
		if (toSkip == 0) return;
		
		System.out.println("Skipping "+toSkip+" bytes...");
		in.skipNBytes(toSkip);
		this.position = position;
	}
	
	public boolean eof() {
		return eof;
	}
}
