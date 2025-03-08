package com.unicine.transfer.record;

public record VersionArchivo(String fileId, String name, String updatedAt) {

    public String fileId() {
        return fileId;
    }

    public String name() {
        return name;
    }

    public String updatedAt() {
        return updatedAt;
    }

}
