package com.emtechhouse.accounts.Models.Accounts;

import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SearchRequestDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiltersSpecification <T>{
    public Specification<T> getSearchSpecification(SearchRequestDto searchRequestDto){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(searchRequestDto.getColumnName()),searchRequestDto.getValue());
    }

    public Specification<T> getSearchSpecification(List<SearchRequestDto> searchRequestDtos){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates= new ArrayList<>();
            for (SearchRequestDto requestDto: searchRequestDtos) {
                Predicate equal=criteriaBuilder.equal(root.get(requestDto.getColumnName()),requestDto.getValue());
                predicates.add(equal);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
