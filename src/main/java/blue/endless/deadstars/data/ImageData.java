package blue.endless.deadstars.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface ImageData {
	
	public int width();
	public int height();
	public int argb(int x, int y);
	public int intensity(int x, int y);
	public void setArgb(int x, int y, int argb);
	
	
	public record OpaqueGray(int width, int height, byte[] data) implements ImageData {
		
		public OpaqueGray(int width, int height, byte[] data) {
			this.width = width; this.height = height;
			if (data.length == width * height) {
				this.data = data;
			} else {
				this.data = Arrays.copyOf(data, width * height);
			}
		}
		
		public OpaqueGray(int width, int height) {
			this(width, height, new byte[width * height]);
		}

		@Override
		public int argb(int x, int y) {
			return intensityToRgb(intensity(x, y));
		}
		
		public void setIntensity(int x, int y, int intensity) {
			if (x<0 || y<0 || x>=width || y>=height) return;
			int ofs = (y * width) + x;
			if (ofs >= data.length) return;
			data[ofs] = (byte) (intensity & 0xFF);
		}
		
		@Override
		public int intensity(int x, int y) {
			if (x<0 || y<0 || x>=width || y>=height) return 0;
			int ofs = (y * width) + x;
			if (ofs >= data.length) return 0;
			return ((int) data[ofs]) & 0xFF;
		}
		
		public void setArgb(int x, int y, int color) {
			setIntensity(x, y, rgbToIntensity(color));
		}
	}
	
	public record Argb(int width, int height, int[] data) implements ImageData {
		
		public Argb(int width, int height, int[] data) {
			this.width = width; this.height = height;
			if (data.length == width * height) {
				this.data = data;
			} else {
				this.data = Arrays.copyOf(data, width * height);
			}
		}
		
		public Argb(int width, int height) {
			this(width, height, new int[width * height]);
		}
		
		@Override
		public int argb(int x, int y) {
			if (x<0 || y<0 || x>=width || y>=height) return 0;
			int ofs = y * width + x;
			if (ofs >= data.length) return 0;
			return data[ofs];
		}
		
		@Override
		public int intensity(int x, int y) {
			return rgbToIntensity(argb(x, y));
		}
		
		@Override
		public void setArgb(int x, int y, int argb) {
			if (x<0 || y<0 || x>=width || y>=height) return;
			int ofs = y * width + x;
			if (ofs >= data.length) return;
			data[ofs] = argb;
		}
	}
	
	static ImageData loadBmp(InputStream inputStream) throws IOException {
		DataWrapper in = new DataWrapper(inputStream);
		
		int magic = in.i16le();
		if (magic != 0x4d42) throw new IOException("Not a BMP file.");
		
		int fileSize = in.i32le(); // Total file length, including headers
		
		// Vendor specific fields; GNU Imp zeroes these; ignore.
		in.i16le();
		in.i16le();
		
		int dataOffset = in.i32le();
		
		System.out.println("Bitmap file with image data starting at "+dataOffset+" / "+fileSize+" bytes. Data size should be "+(fileSize-dataOffset)+" bytes.");
		
		int dibLength = in.i32le();
		
		/*
		 * There is a 12-byte BITMAPCOREHEADER type that we want to exclude for its 16-bit unsigned width/height
		 */
		if (dibLength >=16) {
			int width = in.i32le();
			int height = in.i32le();
			@SuppressWarnings("unused")
			int colorPlanes = in.i16le();
			int bitsPerPixel = in.i16le();
			
			if (width < 0 || height < 0) throw new IOException("Invalid size: "+width+" x "+height);
			
			if (dibLength > 16) {
				int compression = in.i32le();
				if (compression != 0) throw new IOException("No compression formats are supported.");
			}
			
			int samples = (int) Math.ceil(bitsPerPixel / 8.0);
			
			//Jump to imageData
			in.seekTo(dataOffset);
			
			if (samples == 1) {
				OpaqueGray result = new OpaqueGray(width, height);
				
				for(int y=0; y<height; y++) {
					for(int x=0; x<width; x++) {
						result.setIntensity(x, y, in.i8());
					}
				}
				
				return result;
			} else {
				Argb result = new Argb(width, height);
				
				for(int y=0; y<height; y++) {
					for(int x=0; x<width; x++) {
						int pixel = 0;
						for(int i=0; i<samples; i++) {
							pixel <<= 8;
							pixel |= in.i8();
						}
						
						if (samples < 4) pixel |= 0xFF_000000;
						
						result.setArgb(x, y, pixel);
					}
				}
				
				return result;
			}
		} else {
			throw new IOException("Cannot discode this kind of DIB header");
		}
	}
	
	private static int rgbToIntensity(final int argb) {
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		
		return (r + g + b) / 3;
	}
	
	private static int intensityToRgb(int intensity) {
		intensity &= 0xFF;
		return 0xFF_000000 | (intensity << 16) | (intensity << 8) | intensity;
	}
	
	
}