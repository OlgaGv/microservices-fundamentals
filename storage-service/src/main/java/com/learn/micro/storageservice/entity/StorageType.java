package com.learn.micro.storageservice.entity;

public enum StorageType {
        STAGING,
        PERMANENT;

        public static StorageType fromString(String name) {
            try {
                return StorageType.valueOf(name);
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }