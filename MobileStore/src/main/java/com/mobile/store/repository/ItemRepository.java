package com.mobile.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.store.dto.Item;

public interface ItemRepository extends JpaRepository<Item, Integer>{

}
