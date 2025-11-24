package com.campify.campifybackend.campifyBase.controllers;


import com.campify.campifybackend.campifyBase.apiDTOs.ApiResponse;
import com.campify.campifybackend.campifyBase.apiDTOs.ApiSingleResponse;
import com.campify.campifybackend.campifyBase.apiDTOs.SearchResponse;
import com.campify.campifybackend.campifyBase.dtos.BaseDto;
import com.campify.campifybackend.campifyBase.dtos.RevisionDto;
import com.campify.campifybackend.campifyBase.entities.BaseEntity;
import com.campify.campifybackend.campifyBase.models.SearchData;
import com.campify.campifybackend.campifyBase.services.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface BaseController<E extends BaseEntity, INDTO extends BaseDto<E>, OUTDTO extends BaseDto<E>> {
    ModelMapper getModelMapper();

    BaseService<E, INDTO, OUTDTO> getBaseService();

    @GetMapping(value = "/fetch/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiSingleResponse<E, OUTDTO>> findDtoByUuid(@PathVariable UUID id);

    @GetMapping(value = "/fetchAll", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse<E, OUTDTO>> fetchAll();

    @GetMapping(value = "/fetchAllPageable", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse<E, OUTDTO>> fetchAllPageable(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false, defaultValue = "createdDate") String sort, @RequestParam(required = false, defaultValue = "DESC") String direction
    );

    @PostMapping
    ResponseEntity<ApiSingleResponse<E, OUTDTO>> create(
            @RequestBody INDTO dto
    );

    @PutMapping
    ResponseEntity<ApiSingleResponse<E, OUTDTO>> update(
            @RequestBody INDTO dto
    );

    @DeleteMapping("/remove/{id}")
    ResponseEntity<?> remove(@PathVariable UUID id);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@PathVariable UUID id);

    @GetMapping(value = "/lastRevision/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    RevisionDto<E> findLastRevision(@PathVariable UUID id);

    @GetMapping(value = "/allRevision/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<RevisionDto<E>> findAllRevisions(@PathVariable UUID id);


//    @PostMapping("/advanced/search")
//    ResponseEntity<SearchResponse<E, OUTDTO>> advancedSearch(@RequestBody SearchData searchData, Authentication authentication);
@PostMapping("/advanced/search")
ResponseEntity<SearchResponse<E, OUTDTO>> advancedSearch(@RequestBody SearchData searchData);

}
