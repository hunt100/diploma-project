package com.example.demo.service.mapper;

import com.example.demo.data.entity.Authority;
import com.example.demo.data.entity.UserProfile;
import com.example.demo.data.model.DtoUser;
import com.example.demo.service.UserProfileService;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (
        componentModel = "spring",
        uses = UserProfileService.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserProfileMapper extends BaseMapper<UserProfile, DtoUser>{

    //UserProfile modelToEntity(DtoUser user);

    @InheritInverseConfiguration
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.roles", target = "roles")
    DtoUser entityToModel(UserProfile userProfile);

    default DtoUser entityToModelPartially(UserProfile user) {
        if (user == null) {
            return null;
        }
        DtoUser dtoUser = new DtoUser();
        dtoUser.setId(user.getId());
        dtoUser.setFirstName(user.getFirstName());
        dtoUser.setLastName(user.getLastName());
        if (user.getPatronymic() != null) {
            dtoUser.setPatronymic(user.getPatronymic());
        }
        dtoUser.setEmail(user.getEmail());
        dtoUser.setTelephone(user.getTelephone());
        for (Authority a : user.getUser().getRoles()) {
            dtoUser.setRole(a.getName());
        }
        return dtoUser;
    }
}
