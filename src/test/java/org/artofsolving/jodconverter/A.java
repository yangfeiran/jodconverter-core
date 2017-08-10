package org.artofsolving.jodconverter;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.cli.Convert;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class A {

	public static void main(String[] args) throws IOException {
		//convert();
		String inputFile="E:/test/说明书.doc";
		String outputFile="E:/test/说明书00.pdf";
		convert(inputFile, outputFile);
		//convertAll("E:/test");
	}

	private static void convert(String input, String output) {
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        
        officeManager.start();
        try {
        	File inputFile = new File(input);
			String inputExtension = FilenameUtils.getExtension(inputFile.getName());
            DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
            File outputFile = new File(output);
            outputFile.createNewFile();
            DocumentFormat outputFormat = formatRegistry.getFormatByExtension(
            		FilenameUtils.getExtension(outputFile.getName()));
			System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat .getExtension());
            long start = System.currentTimeMillis();
			converter.convert(inputFile, outputFile, outputFormat);
			long end = System.currentTimeMillis();
            System.out.printf("done.\n");
            float size = inputFile.length()/(1024*1024f);
            System.out.println("fileSize:"+size+"M,use:"+(end-start)/1000+"s");
            assertTrue(outputFile.isFile() && outputFile.length() > 0);
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
            officeManager.stop();
        }
	}
	
	private static void convertAll(String inputPath) {
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        
        officeManager.start();
        try {
        	File dir = new File(inputPath);
            File[] files = dir.listFiles();
            for (File inputFile : files) {
            	if(inputFile.isDirectory())
            		continue;
				String inputExtension = FilenameUtils.getExtension(inputFile.getName());
	            DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
	            File outputFile = new File(inputPath+"/pdf/"+inputFile.getName()+".pdf");
	            outputFile.createNewFile();
	            DocumentFormat outputFormat = formatRegistry.getFormatByExtension(
	            		FilenameUtils.getExtension(outputFile.getName()));
				System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat .getExtension());
	            long start = System.currentTimeMillis();
				converter.convert(inputFile, outputFile, outputFormat);
				long end = System.currentTimeMillis();
	            System.out.printf("done.\n");
	            long size = inputFile.length()/(1024*1024);
	            System.out.println("fileName:"+inputFile.getName()+",fileSize:"+size+"M,use:"+(end-start)/1000+"s");
	            assertTrue(outputFile.isFile() && outputFile.length() > 0);
            }
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
            officeManager.stop();
        }
	}

	private static void convert() throws IOException {
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        
        officeManager.start();
        try {
            File dir = new File("src/test/resources/documents");
            File[] files = dir.listFiles(new FilenameFilter() {
            	public boolean accept(File dir, String name) {
            		return !name.startsWith(".");
            	}
            });
			for (File inputFile : files) {
                String inputExtension = FilenameUtils.getExtension(inputFile.getName());
                DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
                assertNotNull(inputFormat, "unknown input format: " + inputExtension);
                Set<DocumentFormat> outputFormats = formatRegistry.getOutputFormats(inputFormat.getInputFamily());
                for (DocumentFormat outputFormat : outputFormats) {
                    // LibreOffice 4 fails natively on this one
                    if (inputFormat.getExtension().equals("odg") && outputFormat.getExtension().equals("svg")) {
                        System.out.println("-- skipping odg to svg test... ");
                        continue;
                    }
                    File outputFile = File.createTempFile("test", "." + outputFormat.getExtension());
                    outputFile.deleteOnExit();
                    System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat.getExtension());
                    converter.convert(inputFile, outputFile, outputFormat);
                    System.out.printf("done.\n");
                    assertTrue(outputFile.isFile() && outputFile.length() > 0);
                    //TODO use file detection to make sure outputFile is in the expected format
                }
            }
        } finally {
            officeManager.stop();
        }
	}

}
