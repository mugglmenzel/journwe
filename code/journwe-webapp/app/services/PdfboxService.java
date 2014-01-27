package services;

import models.adventure.email.Message;
import models.adventure.file.JournweFile;
import models.adventure.place.PlaceOption;
import models.adventure.time.TimeOption;
import models.adventure.todo.EStatus;
import models.adventure.todo.Todo;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.util.PDFMergerUtility;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markus on 26/01/14.
 */
public class PdfboxService {
    private static PDFont textFontBold = PDType1Font.TIMES_BOLD;
    private static int titleFontSize = 16;
    private static int afterTitleSpace = titleFontSize;
    private static PDFont textFont = PDType1Font.TIMES_ROMAN;
    private static int textFontSize = 12;
    private static int afterParagraphSpace = textFontSize;
    private static int afterLineSpace = textFontSize + textFontSize / 1;
    private static int X = 50;
    private static int Y = 800;
    //private static int dinaA4pageHeight = 842; // DIN A4 page height in pt.

    public static PDDocument create(final String adventureName, PlaceOption po, TimeOption to, List<Todo> todos) {
        PDDocument document = null;
        try {
            // Create a document
            document = new PDDocument();
            PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
            document.addPage(page);
            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            int y = Y;
            // ADD TITLE
            contentStream.beginText();
            contentStream.setFont(textFontBold, titleFontSize);
            contentStream.moveTextPositionByAmount(X, y);
            contentStream.drawString(adventureName);
            contentStream.endText();
            y -= titleFontSize + afterTitleSpace;
            // ADD TIME
            if (to != null) {
                contentStream.beginText();
                contentStream.setFont(textFont, textFontSize);
                contentStream.moveTextPositionByAmount(X, y);
                contentStream.drawString("Zeit: " + to.getStartDate() + " - " + to.getEndDate());
                contentStream.endText();
                y -= textFontSize + afterParagraphSpace;
            }
            // ADD PLACE
            if (po != null) {
                contentStream.beginText();
                contentStream.setFont(textFont, textFontSize);
                contentStream.moveTextPositionByAmount(X, y);
                contentStream.drawString("Ort: " + po.getAddress());
                contentStream.endText();
                y -= textFontSize + afterParagraphSpace;
                // Add Map
                Double lon = po.getLongitude();
                Double lat = po.getLatitude();
                int zoom = 15;
                int width = 500;
                int height = 500;
                String imageUrl = "http://ojw.dev.openstreetmap.org/StaticMap/?mode=Export&show=1&lat="+lat+"&lon="+lon+"&z="+zoom+"&w="+width+"&h="+height+"&mico1=32140&mlat1="+lat+"&mlon1="+lon;
                PDPixelMap pngImage = getPngImage(document, imageUrl);
                //int imageHeightInPt = 375; // height * 0.75
                contentStream.drawImage(pngImage, X, y-height);
                y -= height + 2* afterParagraphSpace;
            }
            // ADD MY TODOLIST
            if (todos != null && !todos.isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(textFontBold, textFontSize);
                contentStream.moveTextPositionByAmount(X, y);
                contentStream.drawString("TODO LIST:");
                contentStream.endText();
                y -= textFontSize + afterParagraphSpace;
                for (Todo todo : todos) {
                    contentStream.beginText();
                    contentStream.setFont(textFont, textFontSize);
                    contentStream.moveTextPositionByAmount(X, y);
                    if (todo.getStatus().equals(EStatus.NEW)) {
                        contentStream.drawString(todo.getTitle() + " [ ]");
                    } else if (todo.getStatus().equals(EStatus.COMPLETE)) {
                        contentStream.setFont(textFont, textFontSize);
                        contentStream.drawString(todo.getTitle() + " [X]");
                    }
                    y -= afterLineSpace;
                    contentStream.endText();
                    if (newPage(y)) {
                        // Make sure that the content stream is closed:
                        contentStream.close();
                        PDPage newPage = new PDPage(PDPage.PAGE_SIZE_A4);
                        document.addPage(newPage);
                        contentStream = new PDPageContentStream(document, newPage);
                        y = Y;
                    }
                }
            }
            // Make sure that the content stream is closed:
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static PDDocument addFilesTest2(PDDocument document, List<JournweFile> files) {
        try {
            PDDocument toReturn = new PDDocument();
            // Prepare merger
            PDFMergerUtility ut = new PDFMergerUtility();
            List<InputStream> inputStreams = new ArrayList<InputStream>();
            for (JournweFile file : files) {
                URL url = new URL(file.getUrl());
                URLConnection urlConn = url.openConnection();
                // Checking whether the URL contains a PDF
                String fileType = url.toString().substring(url.toString().length() - 3);
                if (urlConn.getContentType().equalsIgnoreCase("application/pdf") || fileType.equalsIgnoreCase("pdf")) {
                    Logger.debug("Attach pdf file " + url);
                    // Read the PDF from the URL
                    InputStream pdfIS = url.openStream();
                    inputStreams.add(pdfIS);
                    ut.addSource(pdfIS);
                }
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Logger.debug("MERGE NOW! 2");
            ut.setDestinationStream(output);
            Logger.debug("MERGE NOW! 3");
            ut.mergeDocuments();
            Logger.debug("MERGE NOW!");
            toReturn.save(output);
            for(InputStream is : inputStreams)
                is.close();
            document.close();
            return toReturn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteArrayOutputStream addFilesTest(ByteArrayOutputStream mainPdf) {
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
        try {
            // Prepare merger
            PDFMergerUtility ut = new PDFMergerUtility();
            ByteArrayInputStream bais = new ByteArrayInputStream(mainPdf.toByteArray());
            ut.addSource(bais);
            ut.addSource("Cockcroft.pdf");
            ut.addSource("JournWe.pdf");
            Logger.debug("MERGE NOW! 1");
            ut.setDestinationStream(toReturn);
            Logger.debug("MERGE NOW! 2");
            ut.mergeDocuments();
            bais.close();
            Logger.debug("MERGED!");
            return toReturn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static ByteArrayOutputStream appendFilesToPDF(ByteArrayOutputStream mainPdf, List<JournweFile> files) {
        ByteArrayOutputStream toReturn = mainPdf;
        if (files != null && !files.isEmpty()) {
        try {
            // Prepare merger
            PDFMergerUtility ut = new PDFMergerUtility();
            ByteArrayInputStream bais = new ByteArrayInputStream(mainPdf.toByteArray());
            ut.addSource(bais);
            List<InputStream> inputStreams = new ArrayList<InputStream>();
            for (JournweFile file : files) {
                URL url = new URL(file.getUrl());
                URLConnection urlConn = url.openConnection();
                // Checking whether the URL contains a PDF
                String fileType = url.toString().substring(url.toString().length() - 3);
                if (urlConn.getContentType().equalsIgnoreCase("application/pdf") || fileType.equalsIgnoreCase("pdf")) {
                    Logger.debug("Attach pdf file " + url);
                    // Read the PDF from the URL
                    InputStream pdfIS = url.openStream();
                    inputStreams.add(pdfIS);
                    ut.addSource(pdfIS);
                }
            }
            Logger.debug("MERGE NOW! 1");
            ut.setDestinationStream(toReturn);
            Logger.debug("MERGE NOW! 2");
            ut.mergeDocuments();
            bais.close();
            Logger.debug("MERGED!");
            return toReturn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        }
        return toReturn;
    }

    public static void save(PDDocument document, ByteArrayOutputStream output) {
        try {
            // Save the results and ensure that the document is properly closed:
            document.save(output);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }

    private static boolean newPage(final int y) throws IOException {
        if (y < 100)
            return true;
        return false;
    }

    private static PDPixelMap getPngImage(PDDocument document, String imageUrl) throws IOException {
        InputStream is = getImageIS(imageUrl);
        BufferedImage image = ImageIO.read(is);
        PDPixelMap png = new PDPixelMap(document, image);
        is.close();
        return png;
    }

    /**
     * Helper.
     *
     * @param imageUrl
     * @return
     * @throws IOException
     */
    private static InputStream getImageIS(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        return is;
    }

}
