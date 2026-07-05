package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.movie.Comentario}.
 *
 * Incluido:
 * - {@code codigo}, {@code texto}, {@code likes}, {@code dislikes}, {@code fecha}.
 * - Identificadores de relaciones: {@code clienteCedula}, {@code peliculaCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.COMMENT_TEXT_NOT_BLANK)
    private String texto;

    @NotNull(message = ValidationMessages.COMMENT_LIKES_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.COMMENT_LIKES_POSITIVE_OR_ZERO)
    private Integer likes;

    @NotNull(message = ValidationMessages.COMMENT_DISLIKES_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.COMMENT_DISLIKES_POSITIVE_OR_ZERO)
    private Integer dislikes;

    @NotNull(message = ValidationMessages.COMMENT_DATE_NOT_NULL)
    private LocalDateTime fecha;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer clienteCedula;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer peliculaCodigo;
}
