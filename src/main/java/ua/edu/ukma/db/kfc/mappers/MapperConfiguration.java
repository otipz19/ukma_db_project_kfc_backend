package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;

@MapperConfig(implementationPackage = "<PACKAGE_NAME>.generated", componentModel = MappingConstants.ComponentModel.CDI, uses = {EnumsMapper.class, DefaultTypeMapper.class})
public class MapperConfiguration {}
