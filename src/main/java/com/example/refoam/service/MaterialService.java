package com.example.refoam.service;

import com.example.refoam.domain.Material;
import com.example.refoam.domain.MaterialName;
import com.example.refoam.domain.ProductName;
import com.example.refoam.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    public Map<MaterialName, Long> getMaterialQuantities(){
        //DB에서 재료를 가져옴
        List<Material> materials = materialRepository.findAll();

        //재료 이름별로 그룹화하고 총합 계산
        Map<MaterialName, Long> materialNameLongMap = materials.stream().collect(Collectors.groupingBy(Material :: getMaterialName, //재료 이름 기준으로 그룹화
                Collectors.summingLong(Material::getMaterialQuantity) // 각 그룹의 총합 계산
        ));
        return materialNameLongMap;
    }

    //제품 별 필요한 원재료 매핑
    private static final Map<ProductName, List<MaterialName>> PRODUCT_NAME_LIST_MAP = Map.of(
            ProductName.NORMAL30, List.of(MaterialName.EVA, MaterialName.ULTRAMARINE_BLUE, MaterialName.IRON_OXIDE_RED, MaterialName.TITANIUM_DIOXIDE), // 원통형 30CM 연보라(파+빨+흰)
            ProductName.NORMAL60, List.of(MaterialName.EVA, MaterialName.ULTRAMARINE_BLUE, MaterialName.TITANIUM_DIOXIDE), // 원통형 60CM 하늘색(파+흰)
            ProductName.NORMAL90, List.of(MaterialName.EVA, MaterialName. CARBON_BLACK,MaterialName.TITANIUM_DIOXIDE), // 원통형 90CM 회색(검+흰)
            ProductName.BUMP30, List.of(MaterialName.EVA, MaterialName.CARBON_BLACK),
            ProductName.BUMP60, List.of(MaterialName.EVA, MaterialName.CARBON_BLACK),
            ProductName.BUMP90, List.of(MaterialName.EVA, MaterialName.CARBON_BLACK), // 돌기형 30~90CM 검정
            ProductName.HALF30, List.of(MaterialName.EVA, MaterialName.TITANIUM_DIOXIDE, MaterialName.IRON_OXIDE_RED), // 반원형 30CM 연분홍(빨+흰)
            ProductName.HALF60, List.of(MaterialName.EVA, MaterialName.TITANIUM_DIOXIDE, MaterialName.ULTRAMARINE_BLUE), // 반원형 60CM 하늘색(파+흰)
            ProductName.HALF90, List.of(MaterialName.EVA, MaterialName.TITANIUM_DIOXIDE, MaterialName.IRON_OXIDE_RED, MaterialName.ULTRAMARINE_BLUE) // 반원형 90CM 연보라(파+빨+흰)
    );

    // productName에 필요한 재료의 총합 가져오기
    public Map<MaterialName, Long> getRequiredMaterialStock(ProductName productName){
        // 전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        // 제품별 필요한 원재료 리스트 가져오기
        List<MaterialName> requiredMaterials = PRODUCT_NAME_LIST_MAP.getOrDefault(productName, List.of());

        log.info("원재료 리스트{}", requiredMaterials);

        // 필요한 원재료 수량만 추출
        Map<MaterialName, Long> materialNameLongMap = requiredMaterials.stream().collect(Collectors.toMap(
                materialName -> materialName,
                materialName -> materialQuantities.getOrDefault(materialName,0L)
        ));
        return materialNameLongMap;
    }

    // 주문 수량과 비교하여 재고 체크, 주문을 넣을 때 현재 재고가 충분한지 검사
    public boolean isEnoughMaterial(ProductName productName, int orderQuantity){
        //productName에 따라 필요한 재료 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = getRequiredMaterialStock(productName);

        //전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        //모든 원재료가 주문량을 충족하는지 확인
        return requiredMaterialStock.entrySet().stream().allMatch(entry -> materialQuantities.getOrDefault(entry.getKey(), 0L) >= orderQuantity);
    }
}
