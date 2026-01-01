package ajaajaja.debugging_rounge.common.image.api.mapper;

import ajaajaja.debugging_rounge.common.image.api.dto.BatchPresignedUrlRequest;
import ajaajaja.debugging_rounge.common.image.api.dto.BatchPresignedUrlResponse;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsRequestDto;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    
    GeneratePresignedUrlsRequestDto toRequestDto(BatchPresignedUrlRequest request);

    @Mapping(source = "presignedUrlInfos", target = "results")
    BatchPresignedUrlResponse toResponse(GeneratePresignedUrlsResponseDto responseDto);

}

