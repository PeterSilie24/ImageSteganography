/*
 * MIT License
 * 
 * Copyright (c) 2018 Phil Badura
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import javax.imageio.ImageIO;

public class RawByteEncoder
{
	private BufferedImage bufferedImage;
	
	private BufferedImage cloneBufferedImage(BufferedImage bufferedImageSource)
	{
	    return this.cloneBufferedImage(bufferedImageSource, false);
	}
	
	private BufferedImage cloneBufferedImage(BufferedImage bufferedImageSource, boolean removeAlpha)
	{
		int width = bufferedImageSource.getWidth();
		
		int height = bufferedImageSource.getHeight();
		
	    BufferedImage bufferedImage = new BufferedImage(width, height, removeAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
	    
	    for (int x = 0; x < width; x++)
	    {
	    	for (int y = 0; y < height; y++)
	    	{
	    		int argb = bufferedImageSource.getRGB(x, y);
	    		
	    		int b = (argb >> 0) & 0x000000FF;
	    		int g = (argb >> 8) & 0x000000FF;
	    		int r = (argb >> 16) & 0x000000FF;
	    		int a = (argb >> 24) & 0x000000FF;
	    		
	    		if (removeAlpha)
	    		{
	    			r = (0b11111110 & (r * a / 0xFF) & 0x000000FF) | (0b00000001 & r);
	    			g = (0b11111110 & (g * a / 0xFF) & 0x000000FF) | (0b00000001 & g);
	    			b = (0b11111110 & (b * a / 0xFF) & 0x000000FF) | (0b00000001 & b);
	    			a = 0xFF;
	    		}
	    		
	    		argb = (b << 0) | (g << 8) | (r << 16) | (a << 24);
	    		
	    		bufferedImage.setRGB(x, y, argb);
	    	}
	    }
	    
	    return bufferedImage;
	}
	
	public RawByteEncoder(File file) throws IOException
	{
		BufferedImage bufferedImage = ImageIO.read(file);
		
		this.bufferedImage = this.cloneBufferedImage(bufferedImage);
	}
	
	public BufferedImage getBufferedImage()
	{
		return this.cloneBufferedImage(this.bufferedImage);
	}
	
	private int getRawCapacity()
	{
		int width = this.bufferedImage.getWidth();
		
		int height = this.bufferedImage.getHeight();
		
		int size = width * height * 3;
		
		int maxBytes = size / 8;
		
		return maxBytes;
	}
	
	public int getCapacity()
	{
		return this.getRawCapacity();
	}
	
	public boolean encode(byte[] bytes)
	{
		if (bytes != null)
		{
			BitSet bits = BitSet.valueOf(bytes);
			
			if (bits.size() > this.getRawCapacity() * 8)
			{
				return false;
			}
			
			int width = this.bufferedImage.getWidth();
			
			for (int i = 0; i < bits.size(); i++)
			{
				int x = i / 3 % width;
				
				int y = i / 3 / width;
				
				int c = i % 3;
				
				int color = this.bufferedImage.getRGB(x, y);
				
				int value = (color >> (8 * c)) & 0x000000FF;
				
				color = color & ~(0x000000FF << (8 * c));
				
				value = (value & 0b11111110) | (bits.get(i) ? 0b00000001 : 0b00000000);
				
				value = ((value & 0x000000FF) << (8 * c));
				
				color = color | value;
				
				this.bufferedImage.setRGB(x, y, color);
			}
		}
		
		return true;
	}
	
	public byte[] decode()
	{
		BitSet bits = new BitSet(this.getRawCapacity() * 8);
		
		int size = bits.size();
		
		int width = this.bufferedImage.getWidth();
		
		for (int i = 0; i < size; i++)
		{
			int x = i / 3 % width;
			
			int y = i / 3 / width;
			
			int c = i % 3;
			
			int color = this.bufferedImage.getRGB(x, y);
			
			int value = (color >> (8 * c)) & 0x000000FF;
			
			if ((value & 0b00000001) != 0)
			{
				bits.set(i);
			}
		}
		
		byte[] bytes = bits.toByteArray();
		
		return bytes;
	}
	
	public void save(File file) throws IOException
	{
		String fileExtension = "";
		
		String fileName = file.getName();
		
		if (fileName.contains(".") && fileName.lastIndexOf(".") != 0)
		{
			fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		}
		else
		{
			fileExtension = "png";
			
			file = new File(file.getPath() + ".png");
		}
		
		BufferedImage bufferedImage;
		
		if (fileExtension.equals("bmp"))
		{
			bufferedImage = this.cloneBufferedImage(this.bufferedImage, true);
		}
		else
		{
			bufferedImage = this.bufferedImage;
		}
		
		ImageIO.write(bufferedImage, fileExtension, file);
	}
}
