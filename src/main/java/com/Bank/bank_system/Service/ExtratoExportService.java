package com.Bank.bank_system.Service;

import com.Bank.bank_system.Entity.Conta;
import com.Bank.bank_system.Entity.Transacao;
import com.Bank.bank_system.Repository.TransacaoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ExtratoExportService {

    private final ContaService contaService;
    private final TransacaoRepository transacaoRepository;

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color BRAND = new Color(124, 92, 255);

    public ExtratoExportService(ContaService contaService, TransacaoRepository transacaoRepository) {
        this.contaService = contaService;
        this.transacaoRepository = transacaoRepository;
    }

    private String moeda(BigDecimal v) {
        return NumberFormat.getCurrencyInstance(BR).format(v == null ? BigDecimal.ZERO : v);
    }

    private String dt(LocalDateTime d) {
        return d == null ? "-" : d.format(DTF);
    }

    public byte[] gerarCsv(Long contaId) {
        Conta conta = contaService.obterContaComAcesso(contaId);
        List<Transacao> txs = transacaoRepository.findByContaIdOrderByDataDesc(contaId);

        StringBuilder sb = new StringBuilder();
        sb.append("Conta;").append(conta.getNumero()).append('\n');
        sb.append("Saldo atual;").append(conta.getSaldo()).append('\n');
        sb.append('\n');
        sb.append("Data;Tipo;Descricao;Valor\n");
        for (Transacao t : txs) {
            sb.append(dt(t.getData())).append(';')
              .append(t.getTipo()).append(';')
              .append(t.getDescricao() == null ? "" : t.getDescricao().replace(';', ',')).append(';')
              .append(t.getValor()).append('\n');
        }
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(body, 0, out, bom.length, body.length);
        return out;
    }

    public byte[] gerarPdf(Long contaId) {
        Conta conta = contaService.obterContaComAcesso(contaId);
        List<Transacao> txs = transacaoRepository.findByContaIdOrderByDataDesc(contaId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 48, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BRAND);
            Font sub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

            Paragraph header = new Paragraph("Nimbus Bank — Extrato", titulo);
            doc.add(header);
            doc.add(new Paragraph("Conta " + conta.getNumero()
                    + "  ·  Saldo atual: " + moeda(conta.getSaldo())
                    + "  ·  Emitido em " + dt(LocalDateTime.now()), sub));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{3.2f, 2.4f, 5f, 2.6f});
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            for (String h : new String[]{"Data", "Tipo", "Descrição", "Valor"}) {
                PdfPCell c = new PdfPCell(new Phrase(h,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
                c.setBackgroundColor(BRAND);
                c.setPadding(7);
                c.setBorderColor(Color.WHITE);
                table.addCell(c);
            }

            boolean zebra = false;
            Color soft = new Color(244, 243, 252);
            for (Transacao t : txs) {
                Color bg = zebra ? soft : Color.WHITE;
                zebra = !zebra;
                table.addCell(cell(dt(t.getData()), bg, Element.ALIGN_LEFT));
                table.addCell(cell(String.valueOf(t.getTipo()), bg, Element.ALIGN_LEFT));
                table.addCell(cell(t.getDescricao() == null ? "" : t.getDescricao(), bg, Element.ALIGN_LEFT));
                table.addCell(cell(moeda(t.getValor()), bg, Element.ALIGN_RIGHT));
            }

            if (txs.isEmpty()) {
                PdfPCell vazio = new PdfPCell(new Phrase("Nenhuma movimentação registrada.", sub));
                vazio.setColspan(4);
                vazio.setPadding(12);
                vazio.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(vazio);
            }

            doc.add(table);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Documento gerado automaticamente. Não possui valor fiscal.", sub));
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao gerar PDF do extrato");
        }
    }

    private PdfPCell cell(String text, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK)));
        c.setBackgroundColor(bg);
        c.setPadding(6);
        c.setBorderColor(new Color(230, 230, 235));
        c.setHorizontalAlignment(align);
        return c;
    }
}
