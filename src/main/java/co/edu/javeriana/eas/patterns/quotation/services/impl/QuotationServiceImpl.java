package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.common.dto.quotation.QuotationDetailDto;
import co.edu.javeriana.eas.patterns.common.dto.quotation.QuotationWrapperDto;
import co.edu.javeriana.eas.patterns.common.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.common.exceptions.QuotationCoreException;
import co.edu.javeriana.eas.patterns.persistence.entities.*;
import co.edu.javeriana.eas.patterns.persistence.repositories.*;
import co.edu.javeriana.eas.patterns.quotation.dtos.*;
import co.edu.javeriana.eas.patterns.quotation.enums.EQuotationFilter;
import co.edu.javeriana.eas.patterns.quotation.exceptions.QuotationException;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;
import co.edu.javeriana.eas.patterns.quotation.mappers.QuotationMapper;
import co.edu.javeriana.eas.patterns.quotation.services.INotificationService;
import co.edu.javeriana.eas.patterns.quotation.services.IQuotationService;
import co.edu.javeriana.eas.patterns.quotation.services.IRequestQuotationService;
import co.edu.javeriana.eas.patterns.quotation.utilities.InputQuotationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class QuotationServiceImpl implements IQuotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotationServiceImpl.class);

    private INotificationService notificationService;
    private IRequestQuotationService requestQuotationService;

    private InputQuotationUtility inputQuotationUtility;
    private IQuotationRepository quotationRepository;
    private IQuotationDetailRepository quotationDetailRepository;

    @Override
    public List<QuotationWrapperDto> findQuotationByFilter(EQuotationFilter filter, FindQuotationDto findQuotationDto)
            throws QuotationCoreException {
        LOGGER.info("INICIA CONSULTA DE COTIZACIONES POR FILTRO [{}] -> [{}]", filter, findQuotationDto);
        List<QuotationEntity> quotations = getQuotation(filter, findQuotationDto);
        inputQuotationUtility.validateHasElements(quotations);
        List<QuotationEntityWrapper> quotationEntityWrappers = new ArrayList<>();
        quotations.forEach(quotationEntity -> {
            QuotationEntityWrapper quotationEntityW = new QuotationEntityWrapper();
            quotationEntityW.setQuotationEntity(quotationEntity);
            quotationEntityWrappers.add(quotationEntityW);
        });
        setDetails(quotationEntityWrappers);
        List<QuotationWrapperDto> response = createWrapperResponse(quotationEntityWrappers);
        LOGGER.info("FINALIZA CONSULTA DE COTIZACIONES POR FILTRO [{}] -> [{}] RESULTADO -> [{}]", filter, findQuotationDto, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = QuotationException.class)
    public QuotationWrapperDto createQuotation(QuotationWrapperDto quotationWrapperDto) throws QuotationCoreException {
        LOGGER.info("INICIA CREACIÓN DE NUEVA COTIZACION -> [{}]", quotationWrapperDto);
        QuotationEntity quotationEntity = createQuotationEntity(quotationWrapperDto);
        List<QuotationDetailEntity> productServiceList = createQuotationDetail(quotationWrapperDto.getDetails(), quotationEntity);
        LOGGER.info("FINALIZA CREACIÓN DE NUEVA COTIZACION -> [{}]", quotationWrapperDto);
        CompletableFuture.runAsync(() -> {
            try {
                notificationService.sendNotification(quotationEntity, productServiceList);
                requestQuotationService.createRequestQuotationHistorical(quotationEntity.getRequest(), ERequestStatus.IN_QUOTATION);
                requestQuotationService.updateRequestStatusQuotation(ERequestStatus.IN_QUOTATION, quotationEntity.getRequest().getId());
            } catch (IOException | MessagingException | RequestQuotationException e) {
                LOGGER.error("Error en notificación: ", e);
            }
        });
        return quotationWrapperDto;
    }

    private List<QuotationEntity> getQuotation(EQuotationFilter filter, FindQuotationDto findQuotationDto) throws RequestQuotationException {
        CategoryEntity categoryEntity;
        PersonEntity personEntity;
        ProviderEntity providerEntity;
        RequestQuotationEntity requestQuotationEntity;
        List<QuotationEntity> quotationEntities = new ArrayList<>();

        switch (filter) {
            case CATEGORY:
                categoryEntity = inputQuotationUtility.getCategoryEntity(findQuotationDto.getCategoryId());
                quotationEntities = quotationRepository.findByCategory(categoryEntity);
                break;
            case PERSON:
                personEntity = inputQuotationUtility.getPersonEntity(findQuotationDto.getPersonId());
                quotationEntities = quotationRepository.findByPerson(personEntity);
                break;
            case PERSON_CATEGORY:
                categoryEntity = inputQuotationUtility.getCategoryEntity(findQuotationDto.getCategoryId());
                personEntity = inputQuotationUtility.getPersonEntity(findQuotationDto.getPersonId());
                quotationEntities = quotationRepository.findByPersonAndAndCategory(personEntity, categoryEntity);
                break;
            case PROVIDER:
                providerEntity = inputQuotationUtility.getProviderEntity(findQuotationDto.getProviderId());
                quotationEntities = quotationRepository.findByProvider(providerEntity);
                break;
            case REQUEST:
                requestQuotationEntity = inputQuotationUtility.getRequestQuotationEntity(findQuotationDto.getRequestId());
                quotationEntities = quotationRepository.findByRequest(requestQuotationEntity);
                break;
            default:
                Iterable<Integer> iter = Collections.singletonList(findQuotationDto.getQuotationId());
                quotationRepository.findAllById(iter).forEach(quotationEntities::add);
        }
        return quotationEntities;
    }

    private void setDetails(List<QuotationEntityWrapper> quotationEntityWrappers) {
        quotationEntityWrappers.forEach(quotationEntityWrapper -> {
            List<QuotationDetailEntity> details;
            details = quotationDetailRepository.findByQuotation(quotationEntityWrapper.getQuotationEntity());
            quotationEntityWrapper.setQuotationDetailEntity(details);
        });
    }

    private List<QuotationWrapperDto> createWrapperResponse(List<QuotationEntityWrapper> quotationEntityWrappers) {
        List<QuotationWrapperDto> response = new ArrayList<>();
        quotationEntityWrappers.forEach(quotationEntityWrapper -> {
            QuotationWrapperDto quotationWrapper;
            quotationWrapper = QuotationMapper.quotationEntityMapperInQuotationWrapperDto(quotationEntityWrapper.getQuotationEntity());
            List<QuotationDetailDto> listDetails;
            listDetails = QuotationMapper.quotationDetailListMapperInQuotationDetailList(quotationEntityWrapper.getQuotationDetailEntity());
            quotationWrapper.setDetails(listDetails);
            response.add(quotationWrapper);
        });
        return response;
    }

    private QuotationEntity createQuotationEntity(QuotationWrapperDto quotationWrapperDto) throws QuotationCoreException {
        RequestQuotationEntity requestQuotationEntity = inputQuotationUtility.getRequestQuotationEntity(quotationWrapperDto.getRequestId());
        ProviderEntity providerEntity = inputQuotationUtility.getProviderEntity(quotationWrapperDto.getProviderId());
        QuotationEntity quotationEntity = new QuotationEntity();
        quotationEntity.setRequest(requestQuotationEntity);
        quotationEntity.setProvider(providerEntity);
        quotationEntity.setAmount(quotationWrapperDto.getAmountTotal());
        quotationEntity.setNotified(0);
        quotationRepository.save(quotationEntity);
        quotationWrapperDto.setQuotationId(quotationEntity.getId());
        quotationWrapperDto.setCategoryId(quotationEntity.getRequest().getCategory().getId());
        quotationWrapperDto.setCategoryDescription(quotationEntity.getRequest().getCategory().getCategoryDescription());
        quotationWrapperDto.setPersonId(quotationEntity.getRequest().getPerson().getId());
        quotationWrapperDto.setProviderBusinessName(quotationEntity.getProvider().getBusinessName());
        return quotationEntity;
    }

    private List<QuotationDetailEntity> createQuotationDetail(List<QuotationDetailDto> details, QuotationEntity requestQuotationEntity) throws RequestQuotationException {
        List<QuotationDetailEntity> productServiceList = new ArrayList<>();
        for (QuotationDetailDto quotationDetailDto : details) {
            ProductServiceEntity productServiceEntity = inputQuotationUtility.getProductServiceEntity(quotationDetailDto.getProductId());
            QuotationDetailEntity quotationDetailEntity = new QuotationDetailEntity();
            quotationDetailEntity.setQuotation(requestQuotationEntity);
            quotationDetailEntity.setProductService(productServiceEntity);
            quotationDetailEntity.setQuantity(quotationDetailDto.getQuantity());
            quotationDetailEntity.setAmount(quotationDetailDto.getAmount());
            quotationDetailEntity.setDiscount(quotationDetailDto.getDiscount());
            quotationDetailEntity.setDescription(quotationDetailDto.getDescription());
            quotationDetailRepository.save(quotationDetailEntity);
            productServiceList.add(quotationDetailEntity);
        }
        return productServiceList;
    }

    @Autowired
    public void setNotificationService(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setRequestQuotationService(IRequestQuotationService requestQuotationService) {
        this.requestQuotationService = requestQuotationService;
    }

    @Autowired
    public void setInputQuotationUtility(InputQuotationUtility inputQuotationUtility) {
        this.inputQuotationUtility = inputQuotationUtility;
    }

    @Autowired
    public void setQuotationRepository(IQuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    @Autowired
    public void setQuotationDetailRepository(IQuotationDetailRepository quotationDetailRepository) {
        this.quotationDetailRepository = quotationDetailRepository;
    }

}