package com.subrutin.catalog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.subrutin.catalog.domain.Publisher;
import com.subrutin.catalog.dto.PublisherCreateRequestDTO;
import com.subrutin.catalog.dto.PublisherListResponseDTO;
import com.subrutin.catalog.dto.PublisherUpdateRequestDTO;
import com.subrutin.catalog.dto.ResultPageDTO;
import com.subrutin.catalog.exception.BadRequestException;
import com.subrutin.catalog.repository.PublisherRepository;
import com.subrutin.catalog.service.PublisherService;
import com.subrutin.catalog.util.PaginationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

	private final PublisherRepository publisherRepository;
	@Override
	public void createPublisher(PublisherCreateRequestDTO dto) {
		Publisher publisher = new Publisher();
		publisher.setName(dto.getPublisherName());
		publisher.setCompanyName(dto.getCompanyName());
		publisher.setAddress(dto.getAddress());
		
		publisherRepository.save(publisher);
		
	}

	@Override
	public void updatePublisher(String publisherId, PublisherUpdateRequestDTO dto) {
		Publisher publisher = publisherRepository.findBySecureId(publisherId)
		.orElseThrow(()-> new BadRequestException("invalid.publisher_id"));
		publisher.setName(dto.getPublisherName()==null|| dto.getPublisherName().isBlank()?publisher.getName():dto.getPublisherName());
		publisher.setCompanyName(dto.getCompanyName()==null|| dto.getCompanyName().isBlank()?publisher.getCompanyName():dto.getCompanyName());
		publisher.setAddress(dto.getAddress());
		
		publisherRepository.save(publisher);

	}

	@Override
	public ResultPageDTO<PublisherListResponseDTO> findPublisherList(Integer pages, Integer limit, String sortBy,
			String direction, String publisherName) {
	
        Sort sort = Sort.by(new Sort.Order(PaginationUtil.getSortBy(direction), sortBy));
        Pageable pageable = PageRequest.of(pages, limit, sort);
        Page<Publisher> resultPage= publisherRepository.findAllByNameLikeIgnoreCase(publisherName, pageable);
        List<PublisherListResponseDTO> dtos =  resultPage.stream().map((p)->{
        	PublisherListResponseDTO dto = new PublisherListResponseDTO();
        	dto.setCompanyName(p.getName());
        	dto.setPublisherName(p.getName());
        	return dto;
        }).collect(Collectors.toList());
        return PaginationUtil.createResultPageDTO(dtos, resultPage.getTotalElements(), resultPage.getTotalPages());
	}

}
