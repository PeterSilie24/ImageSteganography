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

public class FileEncoder
{
	private ByteEncoder byteEncoder;
	
	public FileEncoder(File file) throws IOException
	{
		byteEncoder = new ByteEncoder(file);
	}
	
	public BufferedImage getBufferedImage()
	{
		return this.byteEncoder.getBufferedImage();
	}
	
	public int getCapacity()
	{
		int capacity = this.byteEncoder.getCapacity();
		
		BinaryFile binaryFile = new BinaryFile(null, "");
		
		return Math.max(capacity - binaryFile.getRequiredSpace(), 0);
	}
	
	public boolean encode(BinaryFile binaryFile)
	{
		if (binaryFile.getRequiredSpace() > this.getCapacity())
		{
			return false;
		}
		
		return this.byteEncoder.encode(binaryFile.getCombinedBytes());
	}
	
	public BinaryFile decode()
	{
		byte[] data = this.byteEncoder.decode();
		
		BinaryFile binaryFile = new BinaryFile(data);
		
		return binaryFile;
	}
	
	public void save(File file) throws IOException
	{
		this.byteEncoder.save(file);
	}
}
