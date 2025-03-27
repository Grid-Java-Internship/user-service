package com.internship.user_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JobCategory implements UserEnum {
    ALL(0),
    PLUMBER(1),
    ELECTRICIAN(2),
    CARPENTER(3),
    PAINTER(4),
    MECHANIC(5),
    LOCKSMITH(6),
    HANDYMAN(7),
    CLEANER(8),
    GARDENER(9),
    HAIRDRESSER(10),
    MAKEUP_ARTIST(11),
    MASSAGE_THERAPIST(12),
    DELIVERY_DRIVER(13);
    
    private final Integer id;

    @Override
    public int getId() {
        return id;
    }

    public static JobCategory fromId(Integer id) {
        return UserEnum.fromId(JobCategory.class, id);
    }
}
