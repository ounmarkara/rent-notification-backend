package com.rentnotification.service;

public class TenantUpdate {
    private Long id;
    private String fcmToken;

    public TenantUpdate(Long id, String fcmToken) {
        this.id = id;
        this.fcmToken = fcmToken;
    }

    public Long getId() {
        return id;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}