package com.besza;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface TimetableMapper {

    @Mapping(source = "plannedDeparture", target = "plannedDeparture", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "plannedArrival", target = "plannedArrival", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "arrival", target = "arrival", dateFormat = "yyyy.MM.dd HH:mm")
    @Mapping(source = "departure", target = "departure", dateFormat = "yyyy.MM.dd HH:mm")
    TimetableDTO map(TimetableBE timetableBE);

    default List<TimetableDTO> map(List<TimetableBE> list) {
        return list.stream().map(this::map).collect(Collectors.toList());
    }
}
