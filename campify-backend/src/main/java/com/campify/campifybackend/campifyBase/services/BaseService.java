package com.campify.campifybackend.campifyBase.services;

import com.campify.campifybackend.campifyBase.apiDTOs.SearchResponse;
import com.campify.campifybackend.campifyBase.dtos.BaseDto;
import com.campify.campifybackend.campifyBase.entities.BaseEntity;
import com.campify.campifybackend.campifyBase.models.Action;
import com.campify.campifybackend.campifyBase.models.SearchData;
import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;

import java.util.*;

public interface BaseService<E extends BaseEntity, INDTO extends BaseDto<E>, OUTDTO extends BaseDto<E>> {
    Class<E> getEntityClass();

    Class<INDTO> getInDTOClass();

    Class<OUTDTO> getOutDTOClass();

    OUTDTO findById(UUID id);

    List<OUTDTO> findAll();

    Page<OUTDTO> findAll(int page, int size, String sort, String direction);

    OUTDTO save(INDTO request);

    List<OUTDTO> save(List<INDTO> request);

    OUTDTO update(INDTO entity);

    void remove(UUID id);

    OUTDTO delete(UUID entity);

    void removeAll(Collection<INDTO> entities);

    Optional<Revision<Integer, E>> findLastRevisionById(UUID id);

    void resolveEntityRelations(E entity);

    Revisions<Integer, E> findRevisionsById(UUID id);

    SearchResponse<E, OUTDTO> search(SearchData searchData);


    default Set<Action> actionsMapping(E entity) {
        Set<Action> actions = new HashSet<>();
        actions.add(Action.READ);
        return actions;
    }
}
