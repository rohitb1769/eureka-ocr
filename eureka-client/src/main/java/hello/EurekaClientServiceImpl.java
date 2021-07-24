package hello;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;

@Service
public class EurekaClientServiceImpl {

	public List<String> uploadAndScanFile(MultipartFile file) {
		List<String> scanResult = new ArrayList<String>();
		String fileName = file.getOriginalFilename();
		try {
			Path targetPath = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + fileName)
					.toAbsolutePath().normalize();
			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			scanResult = scanFile(targetPath.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scanResult;
	}

	public List<String> scanFile(String file) {
		List<String> result = new ArrayList<String>();
		String fileExt = FilenameUtils.getExtension(file);
		switch (fileExt) {
		case "jpg":
		case "jpeg":
		case "png":
			String resultData = scanImageFile(file);
			result.add(resultData);
			break;
		case "pdf":
			result = scanPdfFile(file);
			break;
		}
		return result;
	}

	private List<String> scanPdfFile(String file) {
		List<String> pdfImages = getPDFImages(file);
		if(pdfImages.size()>100) {
			throw new RuntimeException("pdf file size is greater than 100 pages");
		}
		List<String> resultData = new ArrayList<String>();
		for(String imgFilePath : pdfImages) {
			resultData.add(scanImageFile(imgFilePath));
		}
		return resultData;
	}
	
	private static List<String> getPDFImages(String pdfFile) {
		List<String> images = new ArrayList<String>();
		try {
			File pdf = new File(pdfFile);
			String dest = System.getProperty("java.io.tmpdir") + pdf.getName() + "/imagefiles/";
			File f = new File(dest);
			if(f.exists()) {
				f.delete();
			}
			f.mkdirs();
			PDDocument pdfDocument = PDDocument.load(pdf);
			PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
			for(int i=0;i<pdfDocument.getNumberOfPages();i++) {
				String destImageFile = dest + pdf.getName() + "" + i + ".png";
				BufferedImage image = pdfRenderer.renderImage(i,1.5f, ImageType.RGB);
				ImageIOUtil.writeImage(image, destImageFile, 300);
				images.add(destImageFile);
			}
			pdfDocument.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return images;
	}

	public String scanImageFile(String file) {
		String result = "";
		try 
		{
			Tesseract tesseract = new Tesseract();
			tesseract.setLanguage("eng");
			tesseract.setOcrEngineMode(1);

			Path dataDirectory = Paths.get("F:\\tesseract\\tessdata");
			tesseract.setDatapath(dataDirectory.toString());

			BufferedImage image;

			image = ImageIO.read(new FileInputStream(new File(file)));
			result = tesseract.doOCR(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
