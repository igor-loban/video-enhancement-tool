package by.bsu.fpmi.vet.report;

import by.bsu.fpmi.vet.exception.ReportGenerationException;
import by.bsu.fpmi.vet.util.MessageUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public final class ReportGenerator {
    private final static Logger LOGGER = getLogger(ReportGenerator.class);

    private final List<Snapshot> snapshots = new ArrayList<>();

    private WordprocessingMLPackage wordMLPackage;
    private ObjectFactory docxObjectFactory;

    public ReportGenerator() {
    }

    public void addSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void generate() {
        try {
            String commentTitle = MessageUtils.getMessage("report.label.comment");

            wordMLPackage = WordprocessingMLPackage.createPackage();
            docxObjectFactory = Context.getWmlObjectFactory();
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            Document pdfDocument = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(pdfDocument, new FileOutputStream("report.pdf"));
            pdfDocument.open();

            Iterator<Snapshot> iterator = snapshots.iterator();
            while (iterator.hasNext()) {
                Snapshot snapshot = iterator.next();
                byte[] imageAsBytes = convertImageToByteArray(snapshot.getImage());

                mainDocumentPart.addParagraphOfText(commentTitle);
                mainDocumentPart.addParagraphOfText(snapshot.getComment());
                P imageParagraph = addInlineImageToParagraph(createInlineImage(imageAsBytes));
                mainDocumentPart.addObject(imageParagraph);

                pdfDocument.add(new Paragraph(commentTitle));
                pdfDocument.add(new Paragraph(snapshot.getComment()));
                pdfDocument.add(Image.getInstance(imageAsBytes));

                if (iterator.hasNext()) {
                    addPageBreak();
                    pdfDocument.newPage();
                }
            }

            wordMLPackage.save(new File("report.docx"));
//            saveAsPDF();

            pdfDocument.close();
            docxObjectFactory = null;
            wordMLPackage = null;
        } catch (IOException | DocumentException | Docx4JException e) {
            LOGGER.debug("report generate error", e);
        }
    }

    private void saveAsPDF() {
        try {
            Mapper fontMapper = new IdentityPlusMapper();
            wordMLPackage.setFontMapper(fontMapper);
            PhysicalFont font = PhysicalFonts.getPhysicalFonts().get("Arial Unicode MS");
            fontMapper.getFontMappings().put("Times New Roman", font);

            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(wordMLPackage);
            OutputStream os = new FileOutputStream("report.pdf");
            Docx4J.toFO(foSettings, os, Docx4J.FLAG_NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPageBreak() {
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        Br breakObj = new Br();
        breakObj.setType(STBrType.PAGE);

        P paragraph = docxObjectFactory.createP();
        paragraph.getContent().add(breakObj);
        documentPart.getJaxbElement().getBody().getContent().add(paragraph);
    }

    private P addInlineImageToParagraph(Inline inline) {
        P paragraph = docxObjectFactory.createP();
        R run = docxObjectFactory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = docxObjectFactory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    private Inline createInlineImage(byte[] bytes) {
        try {
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

            int docPrId = 1;
            int cNvPrId = 2;

            return imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);
        } catch (Exception e) {
            throw new ReportGenerationException(e);
        }
    }

    private byte[] convertImageToByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (IOException e) {
            throw new ReportGenerationException(e);
        }
    }
}
