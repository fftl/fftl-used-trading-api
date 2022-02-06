package fftl.usedtradingapi.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "users_username")
    private String username;

    @Column(name = "users_password")
    private String password;

    //내 동네 설정

    //관심 상품 목록(찜)

    //관심 카테고리

    //판매 상품

    //구매 상품

    @Column(name = "users_removed")
    private boolean removed;
}
