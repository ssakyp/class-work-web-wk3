package org.example.classworkwebwk3.repository;

import org.example.classworkwebwk3.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
