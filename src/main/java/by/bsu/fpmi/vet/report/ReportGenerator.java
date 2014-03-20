package by.bsu.fpmi.vet.report;

import by.bsu.fpmi.vet.exception.ReportGenerationException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.docx4j.dml.wordprocessingDrawing.Inline;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;
import static org.slf4j.LoggerFactory.getLogger;

public final class ReportGenerator {
    private final static Logger LOGGER = getLogger(ReportGenerator.class);

    private final String notesTitle = getMessage("report.label.notes");
    private final List<Snapshot> snapshots = new ArrayList<>();

    private WordprocessingMLPackage wordMLPackage;
    private ObjectFactory docxObjectFactory;

    public ReportGenerator() {
    }

    public List<Snapshot> getSnapshots() {
        return new ArrayList<>(snapshots);
    }

    public Snapshot getLastSnapshot() {
        return snapshots.isEmpty() ? null : snapshots.get(snapshots.size() - 1);
    }

    public void addSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void remove(Snapshot snapshot) {
        snapshots.remove(snapshot);
    }

    public void removeAllSnapshots() {
        snapshots.clear();
    }

    public void generate() {
        Thread docxReportGeneratorThread = new Thread(new DocxReportGenerator());
        Thread pdfReportGeneratorThread = new Thread(new PdfReportGenerator());
//        docxReportGeneratorThread.start();
        pdfReportGeneratorThread.start();
    }

    private final class DocxReportGenerator implements Runnable {
        @Override public void run() {
            try {
                wordMLPackage = WordprocessingMLPackage.createPackage();
                docxObjectFactory = Context.getWmlObjectFactory();
                MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

                int frameGrabNumber = 1;
                Iterator<Snapshot> iterator = snapshots.iterator();
                while (iterator.hasNext()) {
                    Snapshot snapshot = iterator.next();

                    mainDocumentPart.addParagraphOfText("Frame Grab: " + frameGrabNumber++);
                    mainDocumentPart.addParagraphOfText("Time Index: " + snapshot.getTime());
                    mainDocumentPart.addParagraphOfText(notesTitle);
                    mainDocumentPart.addParagraphOfText(snapshot.getNotes());
                    byte[] imageAsBytes = convertImageToByteArray(snapshot.getImage());
                    Inline inlineImage = createInlineImage(imageAsBytes);
                    P imageParagraph = addInlineImageToParagraph(inlineImage);
                    mainDocumentPart.addObject(imageParagraph);

                    if (iterator.hasNext()) {
                        addPageBreak();
                    }
                }

                wordMLPackage.save(new File("report_" + DateTime.now().toString("yyyy-MM-dd_HH-mm-ss") + ".docx"));
                docxObjectFactory = null;
                wordMLPackage = null;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        JOptionPane.showMessageDialog(null, "Reports generated.", "Report",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            } catch (Docx4JException e) {
                LOGGER.debug("report generate error", e);
                throw new ReportGenerationException(e);
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
                Random random = new Random();
                return imagePart.createImageInline("", "", random.nextInt(), random.nextInt(), false);
            } catch (Exception e) {
                throw new ReportGenerationException(e);
            }
        }
    }

    private final class PdfReportGenerator implements Runnable {
        @Override public void run() {
            try {
                if (snapshots.isEmpty()) {
                    return;
                }

                Document pdfDocument = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(pdfDocument,
                        new FileOutputStream("report_" + DateTime.now().toString("yyyy-MM-dd_HH-mm-ss") + ".pdf"));
                pdfDocument.open();

                int frameGrabNumber = 1;
                Iterator<Snapshot> iterator = snapshots.iterator();
                while (iterator.hasNext()) {
                    Snapshot snapshot = iterator.next();
                    byte[] imageAsBytes = convertImageToByteArray(snapshot.getImage());

                    pdfDocument.add(new Paragraph("Frame Grab: " + frameGrabNumber++));
                    pdfDocument.add(new Paragraph("Time Index: " + snapshot.getTime()));
                    pdfDocument.add(new Paragraph(notesTitle));
                    pdfDocument.add(new Paragraph(snapshot.getNotes()));
                    pdfDocument.add(Image.getInstance(imageAsBytes));

                    if (iterator.hasNext()) {
                        pdfDocument.newPage();
                    }
                }

                pdfDocument.close();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        JOptionPane.showMessageDialog(null, "PDF report generated.", "Report",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            } catch (IOException | DocumentException e) {
                LOGGER.debug("report generate error", e);
                throw new ReportGenerationException(e);
            }
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
