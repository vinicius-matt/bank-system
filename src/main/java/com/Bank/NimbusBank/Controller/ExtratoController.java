package com.Bank.NimbusBank.Controller;

import com.Bank.NimbusBank.Service.ExtratoExportService;
import com.Bank.NimbusBank.Service.ExtratoService;
import com.Bank.NimbusBank.dto.TransacaoResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta/{id}/extrato")
public class ExtratoController {

    private final ExtratoService extratoService;
    private final ExtratoExportService exportService;

    public ExtratoController(ExtratoService extratoService, ExtratoExportService exportService) {
        this.extratoService = extratoService;
        this.exportService = exportService;
    }

    @GetMapping
    public List<TransacaoResponseDTO> extrato(@PathVariable Long id) {
        return extratoService.extrato(id);
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> csv(@PathVariable Long id) {
        byte[] body = exportService.gerarCsv(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"extrato-" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] body = exportService.gerarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"extrato-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}
