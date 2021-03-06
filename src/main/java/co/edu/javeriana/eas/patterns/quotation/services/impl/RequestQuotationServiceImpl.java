package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationDetailDto;
import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationWrapperDto;
import co.edu.javeriana.eas.patterns.common.enums.EExceptionCode;
import co.edu.javeriana.eas.patterns.common.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.persistence.entities.*;
import co.edu.javeriana.eas.patterns.persistence.repositories.*;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindRequestQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.RequestQuotationEntityWrapper;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestFilter;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;
import co.edu.javeriana.eas.patterns.quotation.mappers.RequestQuotationMapper;
import co.edu.javeriana.eas.patterns.quotation.services.IRequestQuotationService;
import co.edu.javeriana.eas.patterns.quotation.utilities.InputQuotationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class RequestQuotationServiceImpl implements IRequestQuotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestQuotationServiceImpl.class);

    private InputQuotationUtility inputQuotationUtility;
    private IRequestQuotationRepository requestQuotationRepository;
    private IRequestQuotationDetailRepository requestQuotationDetailRepository;
    private IRequestQuotationHistoricalRepository requestQuotationHistoricalRepository;
    private IRequestProviderRepository requestProviderRepository;
    private IProviderRepository providerRepository;

    private RestTemplate restTemplate;

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
        CompletableFuture.runAsync(() -> {
            notificationToProviders(requestQuotationWrapperDto);
        });
        LOGGER.info("FINALIZA CREACIÓN DE NUEVA SOLICITUD -> [{}]", requestQuotationWrapperDto);
        return requestQuotationWrapperDto;
    }

    @Override
    public void createRequestQuotationHistorical(RequestQuotationEntity requestQuotationEntity, ERequestStatus eRequestStatus) throws RequestQuotationException {
        RequestStatusEntity requestStatusEntity = inputQuotationUtility.getRequestStatusEntity(eRequestStatus);
        RequestQuotationHistoricalEntity requestQuotationHistoricalEntity = new RequestQuotationHistoricalEntity();
        requestQuotationHistoricalEntity.setRequest(requestQuotationEntity);
        requestQuotationHistoricalEntity.setStatus(requestStatusEntity);
        requestQuotationHistoricalRepository.save(requestQuotationHistoricalEntity);
    }

    @Override
    @Transactional(rollbackFor = RequestQuotationException.class)
    public void updateRequestStatusQuotation(ERequestStatus eRequestStatus, int requestId) throws RequestQuotationException {
        LOGGER.info("INICIA ACTUALIZACIÓN DE ESTADO SOLICITUD Y CREACIÓN DE HISTORICO -> [{}]", requestId);
        inputQuotationUtility.updateRequestQuotationStatus(eRequestStatus.getStatus(), requestId);
        RequestQuotationEntity requestQuotationEntity = requestQuotationRepository.findById(requestId).
                orElseThrow(() -> new RequestQuotationException(EExceptionCode.PERSON_NOT_FOUND, "Id cotizacion no existente."));
        createRequestQuotationHistorical(requestQuotationEntity, eRequestStatus);
        LOGGER.info("FINALIZA ACTUALIZACIÓN DE ESTADO SOLICITUD Y CREACIÓN DE HISTORICO -> [{}]", requestId);
    }

    private List<RequestQuotationEntity> getRequestQuotation(ERequestFilter filter, FindRequestQuotationDto findRequestQuotationDto) throws RequestQuotationException {
        CategoryEntity categoryEntity;
        PersonEntity personEntity;
        RequestStatusEntity requestStatusEntity;
        ProviderEntity providerEntity;
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
            case PROVIDER:
                providerEntity = providerRepository.findById(findRequestQuotationDto.getProviderId())
                        .orElseThrow(() -> new RequestQuotationException(EExceptionCode.PROVIDER_NOT_FOUND, "Provedor no existente."));
                List<RequestProviderEntity> pendingRequestByProvider = requestProviderRepository.findByProviderAndNotified(providerEntity, 0);
                List<RequestQuotationEntity> finalRequestQuotationEntities = requestQuotationEntities;
                pendingRequestByProvider.forEach(requestProviderEntity -> finalRequestQuotationEntities.add(requestProviderEntity.getRequest()));
                requestQuotationEntities = finalRequestQuotationEntities;
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

    private void notificationToProviders(RequestQuotationWrapperDto requestQuotationWrapperDto) {
        LOGGER.info("inicia proceso de notificacion al proveedor por categoria [{}].", requestQuotationWrapperDto.getCategoryId());
        try {
            restTemplate.postForEntity("http://localhost:7074/advisor/notification-provider/" + requestQuotationWrapperDto.getCategoryId(), requestQuotationWrapperDto, Void.class);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error en notificacion a proveedor externo: ", e);
        }
        LOGGER.info("finaliza proceso de notificacion al proveedor [{}].", requestQuotationWrapperDto.getCategoryId());
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    @Autowired
    public void setRequestProviderRepository(IRequestProviderRepository requestProviderRepository) {
        this.requestProviderRepository = requestProviderRepository;
    }

    @Autowired
    public void setProviderRepository(IProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }
}