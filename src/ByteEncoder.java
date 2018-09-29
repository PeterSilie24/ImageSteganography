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

import java.io.File;
import java.io.IOException;

public class ByteEncoder extends RawByteEncoder
{
	public ByteEncoder(File file) throws IOException
	{
		super(file);
	}

	@Override
	public int getCapacity()
	{
		int capacity = super.getCapacity();
		
		return Math.max(capacity - 4, 0);
	}
	
	@Override
	public boolean encode(byte[] bytes)
	{
		if (bytes != null)
		{
			int size = bytes.length;
			
			if (size > this.getCapacity())
			{
				return false;
			}
			
			byte b0 = (byte)((size >> 0) & 0xFF);
			byte b1 = (byte)((size >> 8) & 0xFF);
			byte b2 = (byte)((size >> 16) & 0xFF);
			byte b3 = (byte)((size >> 24) & 0xFF);
			
			byte[] header = { b0, b1, b2, b3 };
			
			byte[] data = new byte[header.length + size];
			
			System.arraycopy(header, 0, data, 0, header.length);
			
			System.arraycopy(bytes, 0, data, header.length, size);
			
			return super.encode(data);
		}
		
		return true;
	}
	
	@Override
	public byte[] decode()
	{
		byte[] data = super.decode();
		
		byte[] bytes = null;
		
		if (data != null)
		{
			if (data.length >= 4)
			{
				byte[] header = new byte[4];
				
				System.arraycopy(data, 0, header, 0, header.length);
				
				int b0 = (((int)(header[0]) + 256) % 256) << 0;
				int b1 = (((int)(header[1]) + 256) % 256) << 8;
				int b2 = (((int)(header[2]) + 256) % 256) << 16;
				int b3 = (((int)(header[3]) + 256) % 256) << 24;
				
				
				int size = b0 | b1 | b2 | b3;
				
				if (size > 0 && size <= (data.length - header.length))
				{
					bytes = new byte[size];
					
					System.arraycopy(data, header.length, bytes, 0, bytes.length);
				}
			}
		}
		
		return bytes;
	}
}
