package fftl.usedtradingapi.image.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_url")
    private String url;

    @Column(name = "image_removed")
    private boolean removed;

    //user

    //product


}
