package com.example.refoam.service;

import com.example.refoam.domain.Material;
import com.example.refoam.domain.MaterialName;
import com.example.refoam.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {
    private final MaterialRepository materialRepository;

    @Transactional
    public void save(Material material){
        materialRepository.save(material);
    }
    @Transactional
    public void delete(Long id){
        materialRepository.deleteById(id);
    }
    public List<Material> selectAll(){
        return materialRepository.findAll();
    }
    public Optional<Material> findOne(Long id){
        return materialRepository.findById(id);
    }
    public List<Material> findMaterialName(MaterialName materialName){
        return materialRepository.findAllByMaterialName(materialName);
    }
}
