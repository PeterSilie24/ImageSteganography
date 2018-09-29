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
import java.util.Optional;

import javafx.application.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;

public class Main extends Application
{
	private FileEncoder fileEncoder;
	
	private Button buttonOpenImage;

	private Button buttonSaveImage;

	private Button buttonEncodeFile;

	private Button buttonDecodeFile;
	
	private Label labelSpace;
	
	private ImageView imageView;
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		stage.setTitle("Image Steganography");
		
		this.buttonOpenImage = new Button("Open Image");

		this.buttonOpenImage.setOnAction(event -> 
		{
			FileChooser fileChooser = new FileChooser();
	        
	        fileChooser.setTitle("Choose image");
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.jpe", "*.jfif", "*.png", "*.bmp", "*.gif"));
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("JPG", "*.jpg", "*.jpeg", "*.jpe", "*.jfif"));
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG", "*.png"));
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("Bitmap", "*.bmp"));
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("GIF", "*.gif"));
	        
	        File file = fileChooser.showOpenDialog(stage);
	        
	        if (file != null)
	        {
	        	try
	        	{
					this.fileEncoder = new FileEncoder(file);
				}
	        	catch (IOException e)
	        	{
	        		e.printStackTrace();
					
					Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open image \"" + file.getPath() + "\".");
					
					alert.setHeaderText("");
					
					alert.showAndWait();
				}
	        	
	        	Image image = new Image(file.toURI().toString());
	        	
	        	if (image != null)
	        	{
	        		this.imageView.setImage(image);
	        		
	        		this.buttonSaveImage.setDisable(false);
	        		
	        		this.buttonEncodeFile.setDisable(false);
	        		
	        		this.buttonDecodeFile.setDisable(false);
	        		
	        		this.labelSpace.setDisable(false);
	        		
	        		this.updateImage();
	        	}
	        }
		});
		
		this.buttonSaveImage = new Button("Save Image");
		
		this.buttonSaveImage.setDisable(true);
		
		this.buttonSaveImage.setOnAction(event -> 
		{
			if (this.fileEncoder != null)
			{
				FileChooser fileChooser = new FileChooser();
		        
		        fileChooser.setTitle("Save image as");
		        
		        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG", "*.png"));
		        
		        fileChooser.getExtensionFilters().add(new ExtensionFilter("Bitmap", "*.bmp"));
		        
		        File file = fileChooser.showSaveDialog(stage);
		        
		        if (file != null)
		        {
		        	try
		        	{
						this.fileEncoder.save(file);
					}
		        	catch (IOException e)
		        	{
		        		e.printStackTrace();
						
						Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save image as \"" + file.getPath() + "\".");
						
						alert.setHeaderText("");
						
						alert.showAndWait();
					}
		        }
			}
		});
		
		Separator separator1 = new Separator();

		this.buttonEncodeFile = new Button("Encode File");
		
		this.buttonEncodeFile.setDisable(true);
		
		this.buttonEncodeFile.setOnAction(event -> 
		{
			this.encodeFile(stage);
			
			this.updateImage();
		});

		this.buttonDecodeFile = new Button("Decode File");
		
		this.buttonDecodeFile.setDisable(true);
		
		this.buttonDecodeFile.setOnAction(event -> 
		{
			this.decodeFile(stage);
		});
		
		Separator separator2 = new Separator();
		
		this.labelSpace = new Label("0 bytes");
		
		this.labelSpace.setDisable(true);
		
		ToolBar toolBar = new ToolBar(this.buttonOpenImage, this.buttonSaveImage, separator1, this.buttonEncodeFile, this.buttonDecodeFile, separator2, this.labelSpace);
		
		this.imageView = new ImageView();
		this.imageView.setSmooth(true);
		this.imageView.setCache(true);
		this.imageView.setPreserveRatio(true);
		
		VBox layout = new VBox(toolBar, this.imageView);
		
		Scene scene = new Scene(layout, 512, 384);
		
		stage.setScene(scene);
		
		stage.sizeToScene();
		
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/ImageSteganography16x16.png")));
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/ImageSteganography24x24.png")));
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/ImageSteganography32x32.png")));
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/ImageSteganography.png")));
		
		stage.show();
	}
	
	public boolean encodeFile(Stage stage)
	{
		if (this.fileEncoder != null)
		{
			FileChooser fileChooser = new FileChooser();
			
	        fileChooser.setTitle("Choose file to encode");
	        
	        fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
	        
	        File file = fileChooser.showOpenDialog(stage);
	        
	        if (file != null)
	        {
	        	try
	        	{
	        		BinaryFile binaryFile = new BinaryFile(file);
	        		
	        		BinaryFile binaryFileWithoutFileName = new BinaryFile(binaryFile.getBytes(), "");
	        		
					if (binaryFileWithoutFileName.getRequiredSpace() > this.fileEncoder.getCapacity())
					{
						Alert alert = new Alert(Alert.AlertType.ERROR, "The file \"" + file.getPath() + "\" is to big to be hidden in the image.");
						
						alert.setHeaderText("");
						
						alert.showAndWait();
					}
					else
					{
						if (binaryFile.getRequiredSpace() > this.fileEncoder.getCapacity())
						{
							Alert alert = new Alert(Alert.AlertType.WARNING, "The file \"" + file.getPath() + "\" is to big to be hidden in the image along with the filename.\nDo you want to hide it without the filename?", ButtonType.YES, ButtonType.NO);
							
							alert.setHeaderText("");
							
							Optional<ButtonType> buttonType = alert.showAndWait();
							
							if (buttonType.isPresent() && buttonType.get() == ButtonType.YES)
							{
								binaryFile = binaryFileWithoutFileName;
							}
						}
						
						if (binaryFile.getRequiredSpace() <= this.fileEncoder.getCapacity())
						{
							this.fileEncoder.encode(binaryFile);
							
							return true;
						}
					}
				}
	        	catch (IOException e)
	        	{
	        		e.printStackTrace();
					
					Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open file \"" + file.getPath() + "\".");
					
					alert.setHeaderText("");
					
					alert.showAndWait();
				}
	        }
		}
        
        return false;
	}
	
	public void decodeFile(Stage stage)
	{
		if (this.fileEncoder != null)
		{
			BinaryFile binaryFile = this.fileEncoder.decode();
			
			if (!binaryFile.isEmpty())
			{
				FileChooser fileChooser = new FileChooser();
				
		        fileChooser.setTitle("Save decoded file as");
		        
		        fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
		        
		        fileChooser.setInitialFileName(new String(binaryFile.getName()));
		        
		        File file = fileChooser.showSaveDialog(stage);
		        
		        if (file != null)
		        {
		        	try
		        	{
		        		binaryFile.save(file);
					}
		        	catch (IOException e)
		        	{
						e.printStackTrace();
						
						Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save file \"" + file.getPath() + "\".");
						
						alert.setHeaderText("");
						
						alert.showAndWait();
					}
		        }
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nothing was hidden in the image.");
				
				alert.setHeaderText("");
				
				alert.showAndWait();
			}
		}
	}
	
	public void updateImage()
	{
		BufferedImage bufferedImage = this.fileEncoder.getBufferedImage();
		
		Image image = SwingFXUtils.toFXImage(bufferedImage, null);
		
		this.imageView.setImage(image);
		
		int size = this.fileEncoder.getCapacity();
		
		String capacity = size + " byte" + (size == 1 ? "" : "s");
		
		this.labelSpace.setText(capacity);
	}
}
