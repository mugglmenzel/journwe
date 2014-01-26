package services;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by markus on 26/01/14.
 */
public class PdfboxService {

    public static PDDocument create(final String adventureName) {
        PDDocument document = null;
        try {
            // Create a document
            document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage( page );
            // Create a new font object selecting one of the PDF base fonts
            PDFont font = PDType1Font.HELVETICA_BOLD;
            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
            contentStream.beginText();
            contentStream.setFont( font, 16 );
            contentStream.moveTextPositionByAmount( 100, 700 );
            contentStream.drawString( adventureName );
            contentStream.endText();
            // Make sure that the content stream is closed:
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
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




}
