package co.edu.javeriana.eas.patterns.quotation.utilities;

import co.edu.javeriana.eas.patterns.common.enums.EExceptionCode;
import co.edu.javeriana.eas.patterns.persistence.entities.CategoryEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.PersonEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.ProductServiceEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestStatusEntity;
import co.edu.javeriana.eas.patterns.persistence.repositories.ICategoryRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IPersonRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IProductServiceRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IRequestStatusRepository;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InputQuotationUtility {

    private ICategoryRepository categoryRepository;
    private IPersonRepository personRepository;
    private IProductServiceRepository productServiceRepository;
    private IRequestStatusRepository requestStatusRepository;


    public CategoryEntity getCategoryEntity(int categoryId) throws RequestQuotationException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RequestQuotationException(EExceptionCode.CATEGORY_NOT_FOUND, "Categoria no existente."));
    }

    public PersonEntity getPersonEntity(int personId) throws RequestQuotationException {
        return personRepository.findById(personId)
                .orElseThrow(() -> new RequestQuotationException(EExceptionCode.PERSON_NOT_FOUND, "Persona no existente."));
    }

    public ProductServiceEntity getProductServiceEntity(int productServiceId) throws RequestQuotationException {
        return productServiceRepository.findById(productServiceId)
                .orElseThrow(() -> new RequestQuotationException(EExceptionCode.PRODUCT_NOT_FOUND, "Producto - Servicio no existente."));
    }

    public RequestStatusEntity getRequestStatusEntity(ERequestStatus eRequestStatus) throws RequestQuotationException {
        return requestStatusRepository.findById(eRequestStatus.getStatus())
                .orElseThrow(() -> new RequestQuotationException(EExceptionCode.REQUEST_STATUS_NOT_FOUND, "Estado no existente."));
    }

    public void validateHasElements(List<?> list) throws RequestQuotationException {
        if (list.isEmpty()) {
            throw new RequestQuotationException(EExceptionCode.PRODUCT_NOT_FOUND, "No se encuentran productos asociados.");
        }
    }

    @Autowired
    public void setCategoryRepository(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setProductServiceRepository(IProductServiceRepository productServiceRepository) {
        this.productServiceRepository = productServiceRepository;
    }

    @Autowired
    public void setPersonRepository(IPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setRequestStatusRepository(IRequestStatusRepository requestStatusRepository) {
        this.requestStatusRepository = requestStatusRepository;
    }
}