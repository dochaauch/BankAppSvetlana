package com.example.bankapplication.service.impl;

import com.example.bankapplication.dto.*;
import com.example.bankapplication.entity.enums.AgreementStatus;
import com.example.bankapplication.mapper.AgreementMapper;
import com.example.bankapplication.repository.AccountRepository;
import com.example.bankapplication.repository.AgreementRepository;
import com.example.bankapplication.repository.ProductRepository;
import com.example.bankapplication.service.AgreementService;
import com.example.bankapplication.service.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * The `AgreementServiceImpl` class is an implementation of the `AgreementService` interface.
 * It provides methods for performing various operations related to agreements.
 *
 * @Service: This annotation is used to indicate that this class is a service component in the Spring framework.
 * @RequiredArgsConstructor: This annotation is from the Lombok library and generates a constructor with required arguments
 * for the final fields. It allows us to inject dependencies using constructor injection.
 * @Slf4j: This annotation is from the Lombok library and generates a logger field for logging.
 * @Transactional: This annotation is used in Spring to define transactional boundaries for methods or classes.
 * When applied to a method or class, it indicates that a transaction should be created for the annotated method
 * or all methods within the annotated class.
 * Transactional boundaries ensure that a group of operations are executed as a single atomic unit.
 * If an exception occurs during the execution of the annotated method or any method within the annotated class,
 * the transaction will be rolled back, and any changes made within the transaction will be undone.
 * By using the `@Transactional` annotation, we can manage transactions declaratively without having
 * to write explicit transaction management code. Spring takes care of creating, committing,
 * or rolling back transactions based on the annotated method's execution.
 * It is important to note that the `@Transactional` annotation should be applied to methods that modify data
 * or perform multiple database operations to ensure data integrity and consistency.
 * <p>
 * AgreementMapper agreementMapper: This field is used to map agreement entities to DTOs and vice versa.
 * <p>
 * AgreementRepository agreementRepository: This field is used to access the agreement data in the database.
 * <p>
 * ProductRepository productRepository: This field is used to access product data in the database.
 * <p>
 * AccountRepository accountRepository: This field is used to access account data in the database.
 * <p>
 * getAll(): This method retrieves all agreements.
 * <p>
 * getAgreementById(UUID id): This method retrieves an agreement by its unique identifier (`id`).
 * It throws an `AgreementNotFoundException` if no agreement with the specified `id` is found.
 * <p>
 * createAgreement(CreateAgreementDTO dto): This method creates a new agreement based on the provided DTO.
 * It throws a `ProductNotFoundException` if the product specified in the DTO is not found,
 * and an `AccountNotFoundException` if the account specified in the DTO is not found.
 * <p>
 * editAgreementById(UUID id, CreateAgreementDTO dto): This method updates an agreement with the specified `id`
 * using the information provided in the DTO. It throws an `AgreementNotFoundException` if no agreement with the specified `id` is found,
 * and a `ProductNotFoundException` if the product specified in the DTO is not found,
 * and an `AccountNotFoundException` if the account specified in the DTO is not found.
 * <p>
 * deleteAgreementById(UUID id): This method deletes an agreement by its unique identifier (`id`).
 * It throws an `AgreementNotFoundException` if no agreement with the specified `id` is found.
 * <p>
 * The `AgreementServiceImpl` class implements the `AgreementService` interface,
 * which defines the contract for performing operations on agreements.
 * By implementing this interface, the class provides the necessary business logic for agreement-related operations.
 * <p>
 * With the `AgreementServiceImpl` class, we can retrieve, create, update, and delete agreements,
 * as well as get all agreements. It uses the `AgreementRepository`, `ProductRepository`, and `AccountRepository` interfaces for data access,
 * and the `AgreementMapper` interface for entity-DTO mapping.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgreementServiceImpl implements AgreementService {
    private final AgreementMapper agreementMapper;
    private final AgreementRepository agreementRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AgreementListDTO getAll() {
        log.info("Get all agreements");
        return new AgreementListDTO(agreementMapper.agreementsToAgreementsDTO(agreementRepository.findAll()));
    }

    @Override
    @Transactional
    public AgreementDTO getAgreementById(UUID id) {
        log.info("Get agreement by id {}", id);
        return agreementMapper.toDTO(agreementRepository.findAgreementById(id).orElseThrow(
                () -> new AgreementNotFoundException(ErrorMessage.AGREEMENT_NOT_FOUND)
        ));
    }

    @Override
    @Transactional
    public AgreementDTO createAgreement(CreateAgreementDTO dto) {
        log.info("Create new agreement");
        if(Double.parseDouble(dto.getSum()) < 0){
            throw new NegativeDataException(ErrorMessage.NEGATIVE_DATA);
        }

        if(Double.parseDouble(dto.getInterestRate()) < 0){
            throw new NegativeDataException(ErrorMessage.NEGATIVE_DATA);
        }

        var productId = dto.getProductId();
        log.info("Product id : " + dto.getProductId());
        var product = productRepository.findProductById(productId).orElseThrow(
                () -> new ProductNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND)
        );
        log.info("Product name: " + product.getName());

        var accountId = dto.getAccountId();
        log.info("Account id : " + dto.getAccountId());
        var account = accountRepository.findAccountById(accountId).orElseThrow(
                () -> new AccountNotFoundException(ErrorMessage.ACCOUNT_NOT_FOUND)
        );
        log.info("Account name: " + account.getName());

        var agreement = agreementMapper.createToEntity(dto);
        agreement.setProduct(product);
        agreement.setAccount(account);
        var result = agreementRepository.save(agreement);
        return agreementMapper.toDTO(result);
    }

    @Override
    @Transactional
    public AgreementDTO editAgreementById(UUID id, CreateAgreementDTO dto) {
        log.info("Edit agreement by id {}", id);

        if(Double.parseDouble(dto.getSum()) < 0){
            throw new NegativeDataException(ErrorMessage.NEGATIVE_DATA);
        }

        if(Double.parseDouble(dto.getInterestRate()) < 0){
            throw new NegativeDataException(ErrorMessage.NEGATIVE_DATA);
        }

        var agreement = agreementRepository.findAgreementById(id).orElseThrow(
                () -> new AgreementNotFoundException(ErrorMessage.AGREEMENT_NOT_FOUND)
        );

        var accountId = dto.getAccountId();
        var account = accountRepository.findAccountById(accountId).orElseThrow(
                () -> new AccountNotFoundException(ErrorMessage.ACCOUNT_NOT_FOUND)
        );

        var productId = dto.getProductId();
        var product = productRepository.findProductById(productId).orElseThrow(
                () -> new ProductNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND)
        );

        agreement.setInterestRate(Double.parseDouble(dto.getInterestRate()));
        agreement.setStatus(AgreementStatus.valueOf(dto.getStatus()));
        agreement.setSum(Double.parseDouble(dto.getSum()));
        agreement.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        agreement.setAccount(account);
        agreement.setProduct(product);

        var result = agreementRepository.save(agreement);
        return agreementMapper.toDTO(result);
    }

    @Override
    @Transactional
    public void deleteAgreementById(UUID id) {
        log.info("Deleting agreement {}", id);
        var agreement = agreementRepository.findAgreementById(id);
        if (agreement.isPresent())
            agreementRepository.deleteById(id);
        else throw new AgreementNotFoundException(ErrorMessage.AGREEMENT_NOT_FOUND);
    }

    @Override
    public List<AgreementIdDTO> findAgreementsByManagerId(UUID managerId) {
        log.info("Find agreement`s ids by manager id {}", managerId);
        List<AgreementIdDTO> agreementIdDTOList = agreementRepository.findAgreementsByManagerId(managerId);
        if (agreementIdDTOList.isEmpty())
            throw new NullPointerException("The list of Id from agreement find by manager id is EMPTY!");
        return agreementIdDTOList;
    }

    @Override
    public List<AgreementIdDTO> findAgreementByClientId(UUID clientId) {
        log.info("Find agreement`s ids by client id {}", clientId);
        List<AgreementIdDTO> agreementIdDTOList = agreementRepository.findAgreementsByClientId(clientId);
        if (agreementIdDTOList.isEmpty())
            throw new NullPointerException("The list of Id from agreement find by client id is EMPTY!");
        return agreementIdDTOList;
    }
}