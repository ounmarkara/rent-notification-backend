package com.rentnotification.repository;

import com.rentnotification.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TenantRepository {
    List<Tenant> getTenantsForReminders();
    void updateFcmToken(@Param("id") Long id, @Param("fcmToken") String fcmToken);
}