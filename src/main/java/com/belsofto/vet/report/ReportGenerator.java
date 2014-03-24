package com.belsofto.vet.report;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.exception.ReportGenerationException;
import com.belsofto.vet.ui.dialog.DialogUtils;
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
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.belsofto.vet.util.MessageUtils.format;
import static com.belsofto.vet.util.MessageUtils.getMessage;
import static org.slf4j.LoggerFactory.getLogger;

public final class ReportGenerator {
    private final static Logger LOGGER = getLogger(ReportGenerator.class);

    private static final String DATE_TIME_FORMAT = getMessage("format.dateTime.reportFileName");

    private final String notesTitle = getMessage("report.label.notes");
    private final List<Snapshot> snapshots = new ArrayList<>();

    private final AtomicBoolean docxGenerated = new AtomicBoolean();
    private final AtomicBoolean pdfGenerated = new AtomicBoolean();

    private ReportOptions options = new ReportOptions();
    private List<byte[]> imagesAsBytes = Collections.synchronizedList(new ArrayList<byte[]>());

    public ReportGenerator() {
    }

    public void setOptions(ReportOptions options) {
        this.options = options;
    }

    public ReportOptions getOptions() {
        return options;
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
        if (snapshots.isEmpty()) {
            return;
        }

        imagesAsBytes.clear();
        for (Snapshot snapshot : snapshots) {
            imagesAsBytes.add(convertImageToByteArray(snapshot.getImage()));
        }

        docxGenerated.set(true);
        pdfGenerated.set(true);

        if (options.isDocxPresent()) {
            docxGenerated.set(false);
            Thread docxReportGeneratorThread = new Thread(new DocxReportGenerator());
            docxReportGeneratorThread.start();
        }

        if (options.isPdfPresent()) {
            pdfGenerated.set(false);
            Thread pdfReportGeneratorThread = new Thread(new PdfReportGenerator());
            pdfReportGeneratorThread.start();
        }
    }

    private final class DocxReportGenerator implements Runnable {
        private WordprocessingMLPackage wordMLPackage;
        private ObjectFactory docxObjectFactory;

        @Override public void run() {
            try {
                wordMLPackage = WordprocessingMLPackage.createPackage();
                docxObjectFactory = Context.getWmlObjectFactory();
                MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

                int frameGrabNumber = 1;
                Iterator<Snapshot> iterator = snapshots.iterator();
                while (iterator.hasNext()) {
                    Snapshot snapshot = iterator.next();
                    byte[] imageAsBytes = imagesAsBytes.get(frameGrabNumber - 1);

                    mainDocumentPart.addParagraphOfText("Frame Grab: " + frameGrabNumber++);
                    mainDocumentPart.addParagraphOfText("Time Index: " + snapshot.getTimeAsString());
                    mainDocumentPart.addParagraphOfText(notesTitle);
                    mainDocumentPart.addParagraphOfText(snapshot.getNotes());
                    P imageParagraph = addInlineImageToParagraph(createInlineImage(imageAsBytes));
                    mainDocumentPart.addObject(imageParagraph);

                    if (iterator.hasNext()) {
                        addPageBreak();
                    }
                }

                File reportDirectory = new File(getReportDirectory());
                reportDirectory.mkdirs();

                wordMLPackage.save(new File(getReportDirectory() + getFileName("docx")));
                docxObjectFactory = null;
                wordMLPackage = null;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        DialogUtils.showInfoMessage("reportsGenerated");
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
                return imagePart.createImageInline("", "", 1, 2, false);
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

                File reportDirectory = new File(getReportDirectory());
                reportDirectory.mkdirs();

                Document pdfDocument = new Document(PageSize.A4.rotate());
                File reportFile = new File(getReportDirectory() + getFileName("pdf"));
                PdfWriter.getInstance(pdfDocument, new FileOutputStream(reportFile));
                pdfDocument.open();

                int frameGrabNumber = 1;
                Iterator<Snapshot> iterator = snapshots.iterator();
                while (iterator.hasNext()) {
                    Snapshot snapshot = iterator.next();
                    byte[] imageAsBytes = imagesAsBytes.get(frameGrabNumber - 1);

                    pdfDocument.add(new Paragraph("Frame Grab: " + frameGrabNumber++));
                    pdfDocument.add(new Paragraph("Time Index: " + snapshot.getTimeAsString()));
                    pdfDocument.add(new Paragraph(notesTitle));
                    pdfDocument.add(new Paragraph(snapshot.getNotes()));
                    pdfDocument.add(Image.getInstance(imageAsBytes));

                    if (iterator.hasNext()) {
                        pdfDocument.newPage();
                    }
                }

                pdfDocument.close();

                if (!options.isDocxPresent()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            DialogUtils.showInfoMessage("reportsGenerated");
                        }
                    });
                }
            } catch (IOException | DocumentException e) {
                LOGGER.debug("report generate error", e);
                throw new ReportGenerationException(e);
            }
        }
    }

    private String getReportDirectory() {
        return ApplicationContext.getInstance().getUserDirectory() + "/reports/";
    }

    private String getFileName(String extension) {
        return format("format.file.report", ApplicationContext.getInstance().getVideoName(),
                DateTime.now().toString(DATE_TIME_FORMAT), extension);
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