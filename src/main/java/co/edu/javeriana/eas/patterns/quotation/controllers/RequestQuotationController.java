package co.edu.javeriana.eas.patterns.quotation.controllers;

import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationWrapperDto;
import co.edu.javeriana.eas.patterns.common.dto.quotation.UpdateRequestQuotationStateDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindRequestQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestFilter;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;
import co.edu.javeriana.eas.patterns.quotation.services.IRequestQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request-quotation")
public class RequestQuotationController {

    private IRequestQuotationService requestQuotationService;

    @PostMapping("/{filter}")
    public ResponseEntity<Object> getRequestQuotationByFilter(@PathVariable ERequestFilter filter, @RequestBody FindRequestQuotationDto findRequestQuotationDto) {
        try {
            List<RequestQuotationWrapperDto> bodyResponse = requestQuotationService.findRequestQuotationByFilter(filter, findRequestQuotationDto);
            return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
        } catch (RequestQuotationException e) {
            return new ResponseEntity<>(e.getExceptionCode().getCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createRequestQuotation(@RequestBody RequestQuotationWrapperDto requestQuotationWrapperDto) {
        try {
            RequestQuotationWrapperDto bodyResponse = requestQuotationService.createRequestQuotation(requestQuotationWrapperDto);
            return new ResponseEntity<>(bodyResponse, HttpStatus.CREATED);
        } catch (RequestQuotationException e) {
            return new ResponseEntity<>(e.getExceptionCode().getCode(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateRequestQuotationStatus(@RequestBody UpdateRequestQuotationStateDto updateRequestQuotationStateDto) {
        try {
            requestQuotationService.updateRequestStatusQuotation(updateRequestQuotationStateDto.geteRequestStatus(), updateRequestQuotationStateDto.getRequestId());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Autowired
    public void setRequestQuotationService(IRequestQuotationService requestQuotationService) {
        this.requestQuotationService = requestQuotationService;
    }
}