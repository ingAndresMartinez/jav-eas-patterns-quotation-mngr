package co.edu.javeriana.eas.patterns.quotation.controllers;

import co.edu.javeriana.eas.patterns.common.exceptions.QuotationCoreException;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.QuotationWrapperDto;
import co.edu.javeriana.eas.patterns.quotation.enums.EQuotationFilter;
import co.edu.javeriana.eas.patterns.quotation.services.IQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotation")
public class QuotationController {

    private IQuotationService quotationService;

    @GetMapping("/{filter}")
    public ResponseEntity<Object> getQuotationByFilter(@PathVariable EQuotationFilter filter, @RequestBody FindQuotationDto findQuotationDto) {
        try {
            List<QuotationWrapperDto> bodyResponse = quotationService.findQuotationByFilter(filter, findQuotationDto);
            return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
        } catch (QuotationCoreException e) {
            return new ResponseEntity<>(e.getExceptionCode().getCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createQuotation(@RequestBody QuotationWrapperDto quotationWrapperDto) {
        try {
            QuotationWrapperDto bodyResponse = quotationService.createQuotation(quotationWrapperDto);
            return new ResponseEntity<>(bodyResponse, HttpStatus.CREATED);
        } catch (QuotationCoreException e) {
            return new ResponseEntity<>(e.getExceptionCode().getCode(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Autowired
    public void setQuotationService(IQuotationService quotationService) {
        this.quotationService = quotationService;
    }

}