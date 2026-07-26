package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para representar una version de archivo en ImageKit.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionArchivoResponse {

    private String fileId;

    private String name;

    private String updatedAt;
}
