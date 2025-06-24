package com.sky.api.weatherapiservice.DTO;

import com.sky.api.weatherapiservice.Location.controller.FullWeatherApiController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FullWeatherModelAssembler
        implements RepresentationModelAssembler<FullWeatherDTO, EntityModel<FullWeatherDTO>> {

    @Override
    public EntityModel<FullWeatherDTO> toModel(FullWeatherDTO entity) {

        EntityModel<FullWeatherDTO> entityModel=EntityModel.of(entity);
        entityModel.add(linkTo(methodOn(FullWeatherApiController.class)
                .getFullWeatherByIPAddress(null)).withSelfRel());
        return null;
    }


}
