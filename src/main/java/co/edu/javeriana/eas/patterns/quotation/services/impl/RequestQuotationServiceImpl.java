package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.persistence.entities.*;
import co.edu.javeriana.eas.patterns.persistence.repositories.IRequestQuotationDetailRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IRequestQuotationHistoricalRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IRequestQuotationRepository;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindRequestQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.RequestQuotationDetailDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.RequestQuotationEntityWrapper;
import co.edu.javeriana.eas.patterns.quotation.dtos.RequestQuotationWrapperDto;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestFilter;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;
import co.edu.javeriana.eas.patterns.quotation.mappers.RequestQuotationMapper;
import co.edu.javeriana.eas.patterns.quotation.services.IRequestQuotationService;
import co.edu.javeriana.eas.patterns.quotation.utilities.InputQuotationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RequestQuotationServiceImpl implements IRequestQuotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestQuotationServiceImpl.class);

    private InputQuotationUtility inputQuotationUtility;
    private IRequestQuotationRepository requestQuotationRepository;
    private IRequestQuotationDetailRepository requestQuotationDetailRepository;
    private IRequestQuotationHistoricalRepository requestQuotationHistoricalRepository;

    @Override
    public List<RequestQuotationWrapperDto> findRequestQuotationByFilter(ERequestFilter filter, FindRequestQuotationDto findRequestQuotationDto)
            throws RequestQuotationException {
        LOGGER.info("INICIA CONSULTA DE SOLICITUDES DE COTIZACIONES POR FILTRO [{}] -> [{}]", filter, findRequestQuotationDto);
        List<RequestQuotationEntity> requestsQuotations = getRequestQuotation(filter, findRequestQuotationDto);
        inputQuotationUtility.validateHasElements(requestsQuotations);
        List<RequestQuotationEntityWrapper> requestQuotationEntityWrappers = new ArrayList<>();
        requestsQuotations.forEach(requestQuotationEntity -> {
            RequestQuotationEntityWrapper requestQuotationEntityW = new RequestQuotationEntityWrapper();
            requestQuotationEntityW.setRequestQuotationEntity(requestQuotationEntity);
            requestQuotationEntityWrappers.add(requestQuotationEntityW);
        });
        setDetails(requestQuotationEntityWrappers);
        List<RequestQuotationWrapperDto> response = createWrapperResponse(requestQuotationEntityWrappers);
        LOGGER.info("FINALIZA CONSULTA DE SOLICITUDES DE COTIZACIONES POR FILTRO [{}] -> [{}] RESULTADO -> [{}]", filter, findRequestQuotationDto, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = RequestQuotationException.class)
    public RequestQuotationWrapperDto createRequestQuotation(RequestQuotationWrapperDto requestQuotationWrapperDto) throws RequestQuotationException {
        LOGGER.info("INICIA CREACIÓN DE NUEVA SOLICITUD -> [{}]", requestQuotationWrapperDto);
        RequestQuotationEntity requestQuotationEntity = createRequestQuotationEntity(requestQuotationWrapperDto);
        createRequestQuotationDetail(requestQuotationWrapperDto.getDetails(), requestQuotationEntity);
        createRequestQuotationHistorical(requestQuotationEntity, ERequestStatus.REGISTERED);
        LOGGER.info("FINALIZA CREACIÓN DE NUEVA SOLICITUD -> [{}]", requestQuotationWrapperDto);
        return requestQuotationWrapperDto;
    }

    private List<RequestQuotationEntity> getRequestQuotation(ERequestFilter filter, FindRequestQuotationDto findRequestQuotationDto) throws RequestQuotationException {
        CategoryEntity categoryEntity;
        PersonEntity personEntity;
        RequestStatusEntity requestStatusEntity;
        List<RequestQuotationEntity> requestQuotationEntities = new ArrayList<>();
        switch (filter) {
            case CATEGORY:
                categoryEntity = inputQuotationUtility.getCategoryEntity(findRequestQuotationDto.getCategoryId());
                requestQuotationEntities = requestQuotationRepository.findByCategory(categoryEntity);
                break;
            case PERSON:
                personEntity = inputQuotationUtility.getPersonEntity(findRequestQuotationDto.getPersonId());
                requestQuotationEntities = requestQuotationRepository.findByPerson(personEntity);
                break;
            case PERSON_CATEGORY:
                categoryEntity = inputQuotationUtility.getCategoryEntity(findRequestQuotationDto.getCategoryId());
                personEntity = inputQuotationUtility.getPersonEntity(findRequestQuotationDto.getPersonId());
                requestQuotationEntities = requestQuotationRepository.findByPersonAndAndCategory(personEntity, categoryEntity);
                break;
            case PERSON_STATUS:
                personEntity = inputQuotationUtility.getPersonEntity(findRequestQuotationDto.getPersonId());
                requestStatusEntity = inputQuotationUtility.getRequestStatusEntity(findRequestQuotationDto.getStatusId());
                requestQuotationEntities = requestQuotationRepository.findByPersonAndStatus(personEntity, requestStatusEntity);
                break;
            default:
                Iterable<Integer> iter = Collections.singletonList(findRequestQuotationDto.getRequestQuotationId());
                requestQuotationRepository.findAllById(iter).forEach(requestQuotationEntities::add);
        }
        return requestQuotationEntities;
    }

    private void setDetails(List<RequestQuotationEntityWrapper> requestQuotationEntityWrappers) {
        requestQuotationEntityWrappers.forEach(requestQuotationEntityWrapper -> {
            List<RequestQuotationDetailEntity> details;
            details = requestQuotationDetailRepository.findByRequest(requestQuotationEntityWrapper.getRequestQuotationEntity());
            requestQuotationEntityWrapper.setRequestQuotationDetailEntity(details);
        });
    }

    private List<RequestQuotationWrapperDto> createWrapperResponse(List<RequestQuotationEntityWrapper> requestQuotationEntityWrappers) {
        List<RequestQuotationWrapperDto> response = new ArrayList<>();
        requestQuotationEntityWrappers.forEach(requestQuotationEntityWrapper -> {
            RequestQuotationWrapperDto requestQuotationWrapper;
            requestQuotationWrapper = RequestQuotationMapper.requestQuotationEntityMapperInRequestQuotationWrapperDto(requestQuotationEntityWrapper.getRequestQuotationEntity());
            List<RequestQuotationDetailDto> listDetails;
            listDetails = RequestQuotationMapper.requestQuotationDetailListMapperInRequestQuotationDetailList(requestQuotationEntityWrapper.getRequestQuotationDetailEntity());
            requestQuotationWrapper.setDetails(listDetails);
            response.add(requestQuotationWrapper);
        });
        return response;
    }

    private RequestQuotationEntity createRequestQuotationEntity(RequestQuotationWrapperDto requestQuotationWrapperDto) throws RequestQuotationException {
        CategoryEntity categoryEntity = inputQuotationUtility.getCategoryEntity(requestQuotationWrapperDto.getCategoryId());
        PersonEntity personEntity = inputQuotationUtility.getPersonEntity(requestQuotationWrapperDto.getPersonId());
        RequestStatusEntity requestStatusEntity = inputQuotationUtility.getRequestStatusEntity(ERequestStatus.REGISTERED);
        RequestQuotationEntity requestQuotationEntity = new RequestQuotationEntity();
        requestQuotationEntity.setCategory(categoryEntity);
        requestQuotationEntity.setPerson(personEntity);
        requestQuotationEntity.setStatus(requestStatusEntity);
        requestQuotationEntity.setAdditionalInfo(requestQuotationWrapperDto.getAdditionalInfo());
        requestQuotationRepository.save(requestQuotationEntity);
        requestQuotationWrapperDto.setRequestQuotationId(requestQuotationEntity.getId());
        requestQuotationWrapperDto.seteRequestStatus(ERequestStatus.REGISTERED);
        requestQuotationWrapperDto.setCategoryDescription(categoryEntity.getCategoryDescription());
        return requestQuotationEntity;
    }

    private void createRequestQuotationDetail(List<RequestQuotationDetailDto> details, RequestQuotationEntity requestQuotationEntity) throws RequestQuotationException {
        for (RequestQuotationDetailDto requestQuotationDetailDto : details) {
            ProductServiceEntity productServiceEntity = inputQuotationUtility.getProductServiceEntity(requestQuotationDetailDto.getProductId());
            RequestQuotationDetailEntity requestQuotationDetailEntity = new RequestQuotationDetailEntity();
            requestQuotationDetailEntity.setAdditionalInfo(requestQuotationDetailDto.getAdditionalInformation());
            requestQuotationDetailEntity.setQuantity(requestQuotationDetailDto.getQuantity());
            requestQuotationDetailEntity.setRequest(requestQuotationEntity);
            requestQuotationDetailEntity.setProductService(productServiceEntity);
            requestQuotationDetailRepository.save(requestQuotationDetailEntity);
        }
    }

    private void createRequestQuotationHistorical(RequestQuotationEntity requestQuotationEntity, ERequestStatus eRequestStatus) throws RequestQuotationException {
        RequestStatusEntity requestStatusEntity = inputQuotationUtility.getRequestStatusEntity(eRequestStatus);
        RequestQuotationHistoricalEntity requestQuotationHistoricalEntity = new RequestQuotationHistoricalEntity();
        requestQuotationHistoricalEntity.setRequest(requestQuotationEntity);
        requestQuotationHistoricalEntity.setStatus(requestStatusEntity);
        requestQuotationHistoricalRepository.save(requestQuotationHistoricalEntity);
    }

    @Autowired
    public void setInputQuotationUtility(InputQuotationUtility inputQuotationUtility) {
        this.inputQuotationUtility = inputQuotationUtility;
    }

    @Autowired
    public void setRequestQuotationRepository(IRequestQuotationRepository requestQuotationRepository) {
        this.requestQuotationRepository = requestQuotationRepository;
    }

    @Autowired
    public void setRequestQuotationDetailRepository(IRequestQuotationDetailRepository requestQuotationDetailRepository) {
        this.requestQuotationDetailRepository = requestQuotationDetailRepository;
    }

    @Autowired
    public void setRequestQuotationHistoricalRepository(IRequestQuotationHistoricalRepository requestQuotationHistoricalRepository) {
        this.requestQuotationHistoricalRepository = requestQuotationHistoricalRepository;
    }

}