package com.mobile.store.repository;


import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mobile.store.dto.MobileCover;

public interface MobileCoverRepository extends JpaRepository<MobileCover, Integer>{

	MobileCover findByName(String name);

	List<MobileCover> findByMobilebrand(String mobilebrand);



	

	



}
