package com.besza;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface TimetableMapper {

    @Mapping(source = "plannedDeparture", target = "plannedDeparture", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "plannedArrival", target = "plannedArrival", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "arrival", target = "arrival", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "departure", target = "departure", dateFormat = "yyyy.MM.dd HH:mm")
    TimetableDTO map(TimetableBE entity);

    @Named("simple")
    @Mapping(source = "plannedDeparture", target = "plannedDeparture", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(target = "plannedArrival", ignore = true)
    @Mapping(target = "arrival", ignore = true)
    @Mapping(target = "departure", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    TimetableDTO mapSimple(TimetableBE entity);

    List<TimetableDTO> map(List<TimetableBE> entities);

    @IterableMapping(qualifiedByName = "simple")
    List<TimetableDTO> mapSimple(List<TimetableBE> entities);

}
