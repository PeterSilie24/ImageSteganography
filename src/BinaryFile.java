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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BinaryFile
{
	private byte[] bytes;
	
	private String name;
	
	private byte[] nullTerminatedName;
	
	public BinaryFile(File file) throws IOException
	{
		this.bytes = Files.readAllBytes(file.toPath());
		
		this.name = file.getName();
		
		this.nullTerminatedName = (this.name + "\0").getBytes(StandardCharsets.UTF_8);
	}
	
	public BinaryFile(byte[] bytes, String name)
	{
		this.bytes = bytes;
		
		this.name = name;
		
		this.nullTerminatedName = (this.name + "\0").getBytes(StandardCharsets.UTF_8);
	}
	
	public BinaryFile(byte[] data)
	{
		this.bytes = null;
		
		this.name = "";
		
		if (data != null)
		{
			if (data.length > 0)
			{
				int fileNameSize = -1;
				
				for (int i = 0; i < data.length; i++)
				{
					if (data[i] == 0)
					{
						fileNameSize = i;
						
						break;
					}
				}
				
				if (fileNameSize > -1)
				{
					byte[] fileName = new byte[fileNameSize];
					
					this.bytes = new byte[data.length - fileNameSize - 1];
					
					System.arraycopy(data, 0, fileName, 0, fileName.length);

					System.arraycopy(data, fileName.length + 1, this.bytes, 0, this.bytes.length);
					
					this.name = new String(fileName);
				}
			}
		}
		
		this.nullTerminatedName = (this.name + "\0").getBytes(StandardCharsets.UTF_8);
	}
	
	public void save(File file) throws IOException
	{
		if (this.bytes != null)
		{
			Files.write(file.toPath(), this.bytes);
		}
	}
	
	public boolean isEmpty()
	{
		return this.bytes == null;
	}
	
	public byte[] getBytes()
	{
		return this.bytes;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public byte[] getNullTerminatedName()
	{
		return this.nullTerminatedName;
	}
	
	public int getRequiredSpace()
	{
		int requiredSpace = this.nullTerminatedName.length;
	
		if (this.bytes != null)
		{
			requiredSpace = requiredSpace + this.bytes.length;
		}
		
		return requiredSpace;
	}
	
	public byte[] getCombinedBytes()
	{
		int size = this.getRequiredSpace();
		
		byte[] data = new byte[size];
		
		System.arraycopy(this.nullTerminatedName, 0, data, 0, this.nullTerminatedName.length);

		if (this.bytes != null)
		{
			System.arraycopy(this.bytes, 0, data, this.nullTerminatedName.length, this.bytes.length);
		}
		
		return data;
	}
}
