package com.sky.api.weatherapicommon.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Entity
@Table(name = "realtime_weather")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealTimeWeather {

    @Id
    @Column(name = "location_code")
    //it also serves as the FK of the Location entity class
    //The locationCode field must match the id of the associated Location entity.
    private String locationCode;
    @Range(min=-50,max = 50,message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private int temperature;
    @Range(min=0,max = 100,message = "Humidity must be in the range of 0 to 100 percentage")
    private int humidity;
    @Range(min=0,max = 100,message = "precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;
    @Range(min=0,max = 200,message = "wind speed  must be in the range of 0 to 200 km/h")
    @JsonProperty("wind_speed")
    private int windSpeed;
    @NotBlank(message = "Status must not be blank")
    @Length(min=3, max=50, message="Status must in between 3 to 50 characters")
    private String status;
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "location_code", referencedColumnName = "code")
    @MapsId // Ensures shared PK-FK mapping
    @JsonBackReference //marks the back side (child), which will not be serialized because it is being ignored.
    private Location location;
}
