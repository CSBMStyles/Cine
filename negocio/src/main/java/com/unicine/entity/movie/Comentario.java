package com.unicine.entity.movie;

import java.time.LocalDateTime;

import com.unicine.entity.user.Cliente;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Comentario {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.COMMENT_TEXT_NOT_BLANK)
    @Column(nullable = false, length = 500)
    private String texto;

    @NotNull(message = ValidationMessages.COMMENT_LIKES_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.COMMENT_LIKES_POSITIVE_OR_ZERO)
    @Builder.Default
    @Column(nullable = false)
    private Integer likes = 0;

    @NotNull(message = ValidationMessages.COMMENT_DISLIKES_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.COMMENT_DISLIKES_POSITIVE_OR_ZERO)
    @Builder.Default
    @Column(nullable = false)
    private Integer dislikes = 0;

    @NotNull(message = ValidationMessages.COMMENT_DATE_NOT_NULL)
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // !SECTION
    // SECTION: Relaciones

    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Pelicula pelicula;

    // !SECTION
}
