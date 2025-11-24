package com.campify.campifybackend.campifyBase.services.impl;

import com.campify.campifybackend.campifyBase.apiDTOs.SearchResponse;
import com.campify.campifybackend.campifyBase.config.TenantContext;
import com.campify.campifybackend.campifyBase.dtos.BaseDto;
import com.campify.campifybackend.campifyBase.entities.BaseEntity;
import com.campify.campifybackend.campifyBase.models.SearchData;
import com.campify.campifybackend.campifyBase.models.SearchDetails;
import com.campify.campifybackend.campifyBase.repos.BaseRepository;
import com.campify.campifybackend.campifyBase.services.BaseService;
import com.campify.campifybackend.campifyBase.services.utils.SearchSpecificationBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unchecked")
public abstract class BaseServiceImpl<E extends BaseEntity, INDTO extends BaseDto<E>, OUTDTO extends BaseDto<E>> implements BaseService<E, INDTO, OUTDTO> {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_RECORDS_PER_DOCUMENT = 1000;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
    public static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    protected final BaseRepository<E> repository;
    protected final ModelMapper modelMapper;
    private final ConcurrentMap<Object, Object> lMap = new ConcurrentHashMap<>();
    protected Class<E> entityClass;
    protected Class<INDTO> inDTOClass;
    protected Class<OUTDTO> outDTOClass;
    @Autowired
    private SearchSpecificationBuilder<E> specificationBuilder;


    protected BaseServiceImpl(BaseRepository<E> repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.inDTOClass = (Class<INDTO>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        this.outDTOClass = (Class<OUTDTO>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    @Override
    public Class<E> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public Class<INDTO> getInDTOClass() {
        return this.inDTOClass;
    }

    @Override
    public Class<OUTDTO> getOutDTOClass() {
        return this.outDTOClass;
    }

    @Override
    public OUTDTO findById(UUID id) {
        long startTime = System.currentTimeMillis();

        try {
            Optional<E> data = repository.findByIdAndIsDeletedFalse(id);
            if (data.isEmpty()) {

            } else {
                OUTDTO result = modelMapper.map(data.get(), outDTOClass);
                return result;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    @Override
    public List<OUTDTO> findAll() {
        long startTime = System.currentTimeMillis();

        try {
            List<E> data = repository.findAllByIsDeletedFalse();
            return data.stream().map(item -> modelMapper.map(item, outDTOClass)).toList();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Page<OUTDTO> findAll(int page, int size, String sort, String direction) {
        long startTime = System.currentTimeMillis();

        try {
           // UUID tenantId = TenantContext.getCurrentTenant();
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);  // "ASC" or "DESC"
            Sort sortObject = Sort.by(sortDirection, sort);  // Sort by the field and direction
            Pageable pageable = PageRequest.of(page, size, sortObject);
            Page<E> data = repository.findAllByIsDeletedFalse( pageable);

            return data.map(item -> modelMapper.map(item, outDTOClass));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public OUTDTO save(INDTO request) {
        long startTime = System.currentTimeMillis();

        try {
            if (request == null) {
                return null;
            } else {
                E entity = this.modelMapper.map(request, this.entityClass);
                resolveEntityRelations(entity);
                E savedEntity = this.repository.save(entity);
                OUTDTO result = this.modelMapper.map(savedEntity, this.outDTOClass);

                return result;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<OUTDTO> save(List<INDTO> request) {
        long startTime = System.currentTimeMillis();

        try {
            if (request != null && !request.isEmpty()) {
                List<E> entities = request.stream()
                        .map((item) -> this.modelMapper.map(item, this.entityClass))
                        .toList();

                entities.forEach(this::resolveEntityRelations);

                entities = this.repository.saveAll(entities);
                return entities.stream()
                        .map(item -> {
                            return this.modelMapper.map(item, this.outDTOClass); // return value
                        })
                        .toList();
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public OUTDTO update(INDTO request) {
        long startTime = System.currentTimeMillis();

        try {
            if (request != null && request.getId() != null) {
                Optional<E> existedOptEntity = this.repository.findById(request.getId());
                if (existedOptEntity.isEmpty()) {
                    return null;
                } else {
                    E existedEntity = existedOptEntity.get();
                    UUID externalId = existedEntity.getExternalId();// side effect
                    this.modelMapper.map(request, existedEntity);
                    existedEntity.setExternalId(externalId);
                    resolveEntityRelations(existedEntity);

                    E updatedEntity = this.repository.save(existedEntity);
                    OUTDTO result = this.modelMapper.map(updatedEntity, this.outDTOClass);

                    return result;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void resolveEntityRelations(E entity) {
        long startTime = System.currentTimeMillis();

        try {
            // This method is meant to be overridden by subclasses
            // Default implementation does nothing
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void remove(UUID id) {

        try {
            if (id != null) {
                repository.deleteById(id);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public OUTDTO delete(UUID id) {

        try {
            if (id == null) {
                return null;
            }
            E entity = repository.findById(id).orElse(null);
            if (entity == null) {
                return null;
            }

            entity.setDeleted(true);
            E updatedEntity = repository.save(entity);
            return modelMapper.map(updatedEntity, outDTOClass);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void removeAll(Collection<INDTO> entities) {

        try {
            if (entities != null) {
                List<E> entitiesToDelete = entities.stream().map(item -> modelMapper.map(item, entityClass)).toList();
                repository.deleteAll(entitiesToDelete);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<Revision<Integer, E>> findLastRevisionById(UUID id) {

        try {
            return this.repository.findLastChangeRevision(id);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Revisions<Integer, E> findRevisionsById(UUID id) {

        try {
            return this.repository.findRevisions(id);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public SearchResponse<E, OUTDTO> search(SearchData searchData) {
        long startTime = System.currentTimeMillis();

        try {
            int page = searchData.getPage() != null ? searchData.getPage() : 0;
            int size = searchData.getSize() != null ? searchData.getSize() : 10;
            Sort.Direction direction = (searchData.getOrder() != null && searchData.getOrder().equalsIgnoreCase("DESC")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            String sort = searchData.getSort() != null ? searchData.getSort() : "createdDate";
            Pageable pageable = PageRequest.of(page, size, direction, sort);
            if (searchData.isFilterTenant()) {
                SearchDetails details = new SearchDetails();
                details.setEqualValue(TenantContext.getCurrentTenant());
                if (searchData.getSearchData() != null) {
                    searchData.getSearchData().getSearch().put("tenantId", details);
                }
            }
            Specification<E> spec = null;
            if (searchData.getSearchData() != null) {
                spec = specificationBuilder.buildSpecification(searchData.getSearchData());
            }

            Page<E> result;
            if (spec != null) {
                result = repository.findAll(spec, pageable);
            } else {
                if (searchData.isFilterTenant()) {
                    result = repository.findAllByTenantIdAndIsDeletedFalse(TenantContext.getCurrentTenant(), pageable);
                } else
                    result = repository.findAllByIsDeletedFalse(pageable);
            }
            List<OUTDTO> dtos = result.getContent().stream().map(
                    element -> modelMapper.map(element, outDTOClass)
            ).toList();

            return new SearchResponse<>(
                    result.getTotalElements(),
                    dtos,
                    result.getTotalPages(),
                    result.getNumber() + 1
            );
        } catch (Exception e) {
            return new SearchResponse<>(
                    0,
                    null,
                    0,
                    0
            );
        }
    }

    private SearchData cloneSearchDataForCount(SearchData original) {
        SearchData clone = new SearchData();
        clone.setPage(0);
        clone.setSize(Integer.MAX_VALUE);
        clone.setSort(original.getSort());
        clone.setOrder(original.getOrder());
        clone.setSearchData(original.getSearchData());
        return clone;
    }

    private Field getFieldFromClass(Class<?> cls, String fieldName) {
        Class<?> currentClass = cls;

        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // Field not found in current class, check the superclass
                currentClass = currentClass.getSuperclass();
            }
        }

        return null; // Field not found in class hierarchy
    }



    private Object getNestedFieldValue(Object entity, String fieldPath) throws IllegalAccessException {
        if (entity == null || fieldPath == null || fieldPath.isEmpty()) {
            return null;
        }

        String[] pathParts = fieldPath.split("\\.");
        Object currentObject = entity;
        Class<?> currentClass = entity.getClass();

        for (String fieldName : pathParts) {
            if (currentObject == null) {
                return null;
            }

            Field field = getFieldFromClass(currentClass, fieldName);
            if (field == null) {
                return null;
            }

            field.setAccessible(true);
            currentObject = field.get(currentObject);

            if (currentObject != null) {
                currentClass = currentObject.getClass();
            }
        }

        return currentObject;
    }



}
