package services;

import com.google.common.collect.Lists;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by markus on 26/01/14.
 */
public class PdfboxService {
    private static PDType1Font textFontBold = PDType1Font.TIMES_BOLD;
    private static int titleFontSize = 16;
    private static int afterTitleSpace = titleFontSize;
    private static PDType1Font textFont = PDType1Font.TIMES_ROMAN;
    private static int textFontSize = 12;
    private static int afterParagraphSpace = textFontSize;
    private static int afterLineSpace = textFontSize + textFontSize / 1;
    private static int X = 50;
    private static int Y = 800;
    //private static int dinaA4pageHeight = 842; // DIN A4 page height in pt.
    private static int paragraphWidth = 200;

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

    public static ByteArrayOutputStream appendEmailsToPDF(ByteArrayOutputStream mainPdf, List<Message> emails) {
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
        try {
            // Create a document
            PDDocument document = new PDDocument();
            PDPage page;
            PDPageContentStream contentStream;

            for(Message email : emails) {
                // Add new page for each email
                page = new PDPage(PDPage.PAGE_SIZE_A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                // Write email content
                String subject = email.getSubject();
                String body = email.getBody();
                Paragraph subjectParagraph = new PdfboxService.Paragraph(X,Y,subject);
                subjectParagraph.font = textFontBold;
                write(contentStream,subjectParagraph);
                Paragraph bodyParagraph = new PdfboxService.Paragraph(X,Y-40,body);
                write(contentStream,bodyParagraph);
                contentStream.close();
            }
            ByteArrayOutputStream emailBais = new ByteArrayOutputStream();
            save(document, emailBais);

            // Prepare merger
            PDFMergerUtility ut = new PDFMergerUtility();
            ByteArrayInputStream bais = new ByteArrayInputStream(mainPdf.toByteArray());
            ut.addSource(bais);
//            List<InputStream> inputStreams = new ArrayList<InputStream>();
//            for (JournweFile file : files) {
//                URL url = new URL(file.getUrl());
//                URLConnection urlConn = url.openConnection();
//                // Checking whether the URL contains a PDF
//                String fileType = url.toString().substring(url.toString().length() - 3);
//                if (urlConn.getContentType().equalsIgnoreCase("application/pdf") || fileType.equalsIgnoreCase("pdf")) {
//                    Logger.debug("Attach pdf file " + url);
//                    // Read the PDF from the URL
//                    InputStream pdfIS = url.openStream();
//                    inputStreams.add(pdfIS);
//                    ut.addSource(pdfIS);
//                }
//            }
            ut.addSource(new ByteArrayInputStream(emailBais.toByteArray()));
            Logger.debug("MERGE EMAILS 1");
            ut.setDestinationStream(toReturn);
            Logger.debug("MERGE EMAILS 2");
            ut.mergeDocuments();
            bais.close();
            emailBais.close();

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

    private static void write(PDPageContentStream out, Paragraph paragraph) throws IOException {
        out.beginText();
        out.appendRawCommands(paragraph.getFontHeight() + " TL\n");
        out.setFont(paragraph.getFont(), paragraph.getFontSize());
        out.moveTextPositionByAmount(paragraph.getX(), paragraph.getY());
        out.setStrokingColor(paragraph.getColor());

        List<String> lines = paragraph.getLines();
        for (Iterator<String> i = lines.iterator(); i.hasNext(); ) {
            out.drawString(i.next().trim());
            if (i.hasNext()) {
                out.appendRawCommands("T*\n");
            }
        }
        out.endText();

    }

    static private class Paragraph {

        /** position X */
        private float x;

        /** position Y */
        private float y;

        /** width of this paragraph */
        private int width = 500;

        /** text to write */
        private String text;

        /** font to use */
        private PDType1Font font = PDType1Font.TIMES_ROMAN;

        /** font size to use */
        private int fontSize = 12;

        private int color = 0;

        public Paragraph(float x, float y, String text) {
            this.x = x;
            this.y = y;
            this.text = text;
        }

        /**
         * Break the text in lines
         * @return
         */
        public List<String> getLines() throws IOException {
            List<String> result = Lists.newArrayList();

            String[] split = text.split("(?<=\\W)");
            int[] possibleWrapPoints = new int[split.length];
            possibleWrapPoints[0] = split[0].length();
            for ( int i = 1 ; i < split.length ; i++ ) {
                possibleWrapPoints[i] = possibleWrapPoints[i-1] + split[i].length();
            }

            int start = 0;
            int end = 0;
            for ( int i : possibleWrapPoints ) {
                float width = font.getStringWidth(text.substring(start,i)) / 1000 * fontSize;
                if ( start < end && width > this.width ) {
                    result.add(text.substring(start,end));
                    start = end;
                }
                end = i;
            }
            // Last piece of text
            result.add(text.substring(start));
            return result;
        }

        public float getFontHeight() {
            return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        }

        public Paragraph withWidth(int width) {
            this.width = width;
            return this;
        }

        public Paragraph withFont(PDType1Font font, int fontSize) {
            this.font = font;
            this.fontSize = fontSize;
            return this;
        }

        public Paragraph withColor(int color) {
            this.color = color;
            return this;
        }

        public int getColor() {
            return color;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public String getText() {
            return text;
        }

        public PDType1Font getFont() {
            return font;
        }

        public int getFontSize() {
            return fontSize;
        }

    }

}
