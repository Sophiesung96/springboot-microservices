package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client_apps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String clientId;

    @Column(nullable = false, length = 100, unique = true)
    private String clientSecret;

    @Transient
    private String rawClientSecret;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AppRole role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(orphanRemoval = true)
    @JoinTable(
            name = "locations",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "location_code", referencedColumnName = "code")}
    )
    private Location location;

    private boolean trashed;


}