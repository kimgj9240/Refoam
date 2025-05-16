package com.example.refoam.repository;

import com.example.refoam.domain.Material;
import com.example.refoam.domain.MaterialName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material,Long> {
    List<Material> findAllByMaterialName(MaterialName materialName);
}
