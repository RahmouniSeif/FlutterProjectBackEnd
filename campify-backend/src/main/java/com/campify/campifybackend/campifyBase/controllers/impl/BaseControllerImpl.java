package com.campify.campifybackend.campifyBase.controllers.impl;


import com.campify.campifybackend.campifyBase.apiDTOs.ApiResponse;
import com.campify.campifybackend.campifyBase.apiDTOs.ApiSingleResponse;
import com.campify.campifybackend.campifyBase.apiDTOs.SearchResponse;
import com.campify.campifybackend.campifyBase.controllers.BaseController;
import com.campify.campifybackend.campifyBase.dtos.BaseDto;
import com.campify.campifybackend.campifyBase.dtos.RevisionDto;
import com.campify.campifybackend.campifyBase.entities.BaseEntity;
import com.campify.campifybackend.campifyBase.models.SearchData;
import com.campify.campifybackend.campifyBase.services.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseControllerImpl<E extends BaseEntity, INDTO extends BaseDto<E>, OUTDTO extends BaseDto<E>> implements BaseController<E, INDTO, OUTDTO> {
    protected final BaseService<E, INDTO, OUTDTO> baseService;
    protected final ModelMapper modelMapper;


    public BaseControllerImpl(BaseService<E, INDTO, OUTDTO> baseService, ModelMapper modelMapper) {
        this.baseService = baseService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Override
    public BaseService<E, INDTO, OUTDTO> getBaseService() {
        return baseService;
    }

    @Override
    public ResponseEntity<ApiSingleResponse<E, OUTDTO>> findDtoByUuid(@PathVariable UUID id) {
        long startTime = System.currentTimeMillis();

        try {
            OUTDTO result = baseService.findById(id);

            return ResponseEntity.ok(new ApiSingleResponse<E, OUTDTO>(true, "Entity found successfully", result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<E, OUTDTO>> fetchAll() {
        long startTime = System.currentTimeMillis();

        try {
            List<OUTDTO> list = baseService.findAll();

            return ResponseEntity.ok(new ApiResponse<E, OUTDTO>(true, "Retrieved " + list.size() + " entities successfully", list));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<E, OUTDTO>> fetchAllPageable(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false, defaultValue = "createdDate") String sort, @RequestParam(required = false, defaultValue = "DESC") String direction

    ) {
        long startTime = System.currentTimeMillis();

        try {
            Page<OUTDTO> pageResult = baseService.findAll(page, size, sort, direction);

            return ResponseEntity.ok(new ApiResponse<E, OUTDTO>(true, "Retrieved page " + page + " successfully", pageResult.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);        }
    }

    @Override
    public ResponseEntity<ApiSingleResponse<E, OUTDTO>> create(
            @RequestBody INDTO dto
    ) {
        long startTime = System.currentTimeMillis();

        try {
            OUTDTO savedEntity = baseService.save(dto);

            return ResponseEntity.ok(new ApiSingleResponse<E, OUTDTO>(true, "Entity created successfully", savedEntity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<ApiSingleResponse<E, OUTDTO>> update(
            @RequestBody INDTO dto
    ) {
        long startTime = System.currentTimeMillis();

        try {
            OUTDTO savedEntity = baseService.update(dto);

            return ResponseEntity.ok(new ApiSingleResponse<E, OUTDTO>(true, "Entity updated successfully", savedEntity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> remove(
            @PathVariable UUID id
    ) {

        try {
            baseService.remove(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        long startTime = System.currentTimeMillis();

        try {
            OUTDTO deletedEntity = baseService.delete(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RevisionDto<E> findLastRevision(@PathVariable UUID id) {
        long startTime = System.currentTimeMillis();

        try {
            Optional<Revision<Integer, E>> revisionOptional = baseService.findLastRevisionById(id);
            RevisionDto<E> revisionDto = new RevisionDto<E>();
            if (revisionOptional.isPresent()) {
                Revision<Integer, E> revision = revisionOptional.orElse(null);
                E entity = revision.getEntity();
                revisionDto.setRevisionMetadata(revision.getMetadata());
                OUTDTO outDto = modelMapper.map(entity, baseService.getOutDTOClass());
                revisionDto.setData(outDto);

            }
            return revisionDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RevisionDto<E>> findAllRevisions(@PathVariable UUID id) {
        long startTime = System.currentTimeMillis();

        try {
            List<Revision<Integer, E>> listRevision = baseService.findRevisionsById(id).getContent();
            List<RevisionDto<E>> result = listRevision.stream()
                    .map(ls -> new RevisionDto<E>(ls.getMetadata(), modelMapper.map(ls.getEntity(), baseService.getOutDTOClass())))
                    .toList();

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    //public ResponseEntity<SearchResponse<E, OUTDTO>> advancedSearch(@RequestBody SearchData searchData, Authentication authentication) {
    @Override
    public ResponseEntity<SearchResponse<E, OUTDTO>> advancedSearch(@RequestBody SearchData searchData) {
        try {
        //    final String resource = getResourceName();
         //   Set<String> actions = extractResourcePermissions(authentication, resource);
           // String role = extractResourceRole(authentication);

            SearchResponse<E, OUTDTO> response = baseService.search(searchData);
//            List<OUTDTO> dtos = response.getData().stream().peek(
//                    element -> {
//                        E entity = modelMapper.map(element, baseService.getEntityClass());
//                        Set<Action> filteredActions = baseService.actionsMapping(entity);
//                        Set<String> roles = Set.of("ADMIN","OSMADMIN");
//                        if (!(roles.contains(role))) {
//                            filteredActions = filteredActions.stream().filter(
//                                    a-> actions.contains(a.name())
//                            ).collect(Collectors.toSet());
//                        }
//                        element.setActions(filteredActions);
//                    }
//            ).toList();

            response.setData(response.getData());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extractResourceRole(Authentication authentication) {
        // 1) Try to reflectively call getClaims() on the principal
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = null;
        try {
            Method m = principal.getClass().getMethod("getClaims");
            Object maybeClaims = m.invoke(principal);
            if (maybeClaims instanceof Map<?, ?>) {
                claims = (Map<String, Object>) maybeClaims;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // principal doesn’t have getClaims() or something went wrong → we'll ignore
        }

        // 2) From the claims map pull out “authorities” if present
        List<String> rawAuthorities = Collections.emptyList();
        if (claims != null) {
            return claims.get("role").toString();

        }
        return "ADMIN";
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractResourcePermissions(Authentication authentication, String resource) {
        if (authentication == null || resource == null) {
            return Collections.emptySet();
        }

        // 1) Try to reflectively call getClaims() on the principal
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = null;
        try {
            Method m = principal.getClass().getMethod("getClaims");
            Object maybeClaims = m.invoke(principal);
            if (maybeClaims instanceof Map<?, ?>) {
                claims = (Map<String, Object>) maybeClaims;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // principal doesn’t have getClaims() or something went wrong → we'll ignore
        }

        // 2) From the claims map pull out “authorities” if present
        List<String> rawAuthorities = Collections.emptyList();
        if (claims != null) {
            Object auths = claims.get("authorities");
            if (auths instanceof Collection<?>) {
                rawAuthorities = ((Collection<?>) auths).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        }

        // 3) Fallback to Spring’s built-in GrantedAuthority list if we got nothing
        if (rawAuthorities.isEmpty()) {
            rawAuthorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        // 4) Now filter by “:RESOURCE:” and strip off the resource prefix
        String filter = ":" + resource.toUpperCase() + ":";
        return rawAuthorities.stream()
                .filter(Objects::nonNull)
                .filter(auth -> auth.toUpperCase().contains(filter))
                .map(this::extractPermissionFromAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String extractPermissionFromAuthority(String authority) {
        if (authority == null || authority.isEmpty()) {
            return null;
        }
        int lastColon = authority.lastIndexOf(':');
        if (lastColon == -1) {
            return null;
        }
        try {
            return authority.substring(lastColon + 1);
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    protected abstract String getResourceName();


}
